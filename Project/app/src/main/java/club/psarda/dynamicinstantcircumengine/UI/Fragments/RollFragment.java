package club.psarda.dynamicinstantcircumengine.UI.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.seismic.ShakeDetector;

import java.util.ArrayList;
import java.util.List;

import club.psarda.dynamicinstantcircumengine.Adapters.ListNumberInput.ListInputAdapterNew;
import club.psarda.dynamicinstantcircumengine.Listners.DiceShakeListener;
import club.psarda.dynamicinstantcircumengine.R;
import club.psarda.dynamicinstantcircumengine.Adapters.ListNumberInput.TargetAdapterData;
import club.psarda.dynamicinstantcircumengine.UI.MainActivity;
import club.psarda.dynamicinstantcircumengine.Utils.UtilsClass;
import club.psarda.dynamicinstantcircumengine.database.DataHolders.SettingsData;
import club.psarda.dynamicinstantcircumengine.database.DataHolders.StatsData;
import club.psarda.dynamicinstantcircumengine.database.DataHolders.TargetData;
import club.psarda.dynamicinstantcircumengine.database.DataHolders.TargetEnum;
import club.psarda.dynamicinstantcircumengine.database.FireBase.DataStorage;
import club.psarda.dynamicinstantcircumengine.database.FireBase.FireBaseHelper;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RollFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RollFragment#newInstance} factory method to
 * create an INSTANCE of this fragment.
 */
public class RollFragment extends Fragment implements ShakeDetector.Listener {

	public enum States{
		ROLL, KEEP_ON_ROLLING
	}

	private class ViewHolder {
		public final android.support.design.widget.FloatingActionButton rollButton;
		public final RelativeLayout settingLayout;
		public final RelativeLayout rollingLayout;
		public final ImageButton rollingImage;
		public final android.support.v7.widget.RecyclerView recyclerView;

		public ViewHolder(View view){
			rollButton = (android.support.design.widget.FloatingActionButton)view.findViewById(R.id.roll_button);
			settingLayout = (RelativeLayout)view.findViewById(R.id.setting_up_roll_layout);
			rollingLayout = (RelativeLayout)view.findViewById(R.id.rolling_layout);
			rollingImage = (ImageButton)view.findViewById(R.id.rolling_image);
			recyclerView = (android.support.v7.widget.RecyclerView)view.findViewById(R.id.roll_input_new);
		}
	}

	private class EndlessDiceShakeListner extends DiceShakeListener{

		public EndlessDiceShakeListner(ImageButton imageButton, Context context){
			super(imageButton, START_ANIMATION_DURATION, context);
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			Animation shake = AnimationUtils.loadAnimation(_context, R.anim.dice_roll);
			shake.setAnimationListener(new EndlessDiceShakeListner(_imageButton, _context));
			shake.setDuration(_nextDur);
			_imageButton.setAnimation(shake);
			shake.start();
		}
	}

	private class RollAsync extends AsyncTask<StatsData, String, StatsData> {

		private List<TargetData> _targets;

		public RollAsync(List<TargetData> targets){
			_targets = targets;
		}

		@Override
		protected StatsData doInBackground(StatsData... params) {
			params[0].roll(_targets);
			return params[0];
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			switchToRollingUi();
		}

		@Override
		protected void onPostExecute(StatsData result) {
			switchToResults();
		}

		@Override
		protected void onProgressUpdate(String... text) {
			super.onProgressUpdate();
		}
	}

	private ViewHolder _viewHolder;
    private List<String> _targetHints = new ArrayList<>();
    private List<TargetAdapterData> _targetAdapterData = new ArrayList<>();
    private ListInputAdapterNew _inputListAdapter;
	private StatsData _statsData;
	private States _state = States.ROLL;
	private ShakeDetector _shakeDetector;
	private RollAsync _rollAsync;

	public RollFragment() {
        // Required empty public constructor
    }

    public static RollFragment newInstance() {
        return new RollFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	
		SensorManager sensorManager = (SensorManager) getActivity().getSystemService(getActivity().SENSOR_SERVICE);
		_shakeDetector = new ShakeDetector(this);
		_shakeDetector.start(sensorManager);
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.roll_fragmnet_menu, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		AddTargetToList();
		
		RecyclerView listView = _viewHolder.recyclerView;
		
		DisplayMetrics displayMetrics = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

		if(_viewHolder.recyclerView.getHeight() > 300){
			ViewGroup.LayoutParams params = listView.getLayoutParams();
			
			listView.setLayoutParams(params);
			listView.requestLayout();
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_roll, container, false);
    }
    
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		if(savedInstanceState == null){
			Bundle bundle = this.getArguments();
			
			if(bundle == null){
				bundle = RollDefaultBundle();
			}

			if(bundle.getString(States.class.getCanonicalName()) != null){
				_state = States.valueOf(bundle.getString(States.class.getCanonicalName()));
			}

			_viewHolder = new ViewHolder(getView());
			InitialiseRollButton();
			Refresh(bundle);

			switch (_state){
				case ROLL:
					SettingsUpdated();
					break;
			}
		}
    }
	
    @Override
    public void onDetach() {
        super.onDetach();
    }
    
	@Override
	public void hearShake() {
		if(isVisible()){
			Roll(true);
		}
	}
	
	public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
    
    private void Refresh(Bundle bundle){
		InistliseNewListInput(bundle);
	}
	
	private Bundle RollDefaultBundle(){
		DataStorage dataStorage = DataStorage.Companion.getINSTANCE();
		
		SettingsData settingsData;
		
		if(dataStorage.getSettingsLoaded()){
			settingsData = dataStorage.getSettings();
			
		}else{
			settingsData = new SettingsData();
		}
		
		Bundle bundle = new Bundle();
		bundle.putString("sides", Long.toString(settingsData.getDefualtSides()));
		bundle.putString("NumberOfDice", Long.toString(settingsData.getDefualtNumberOfDice()));
		bundle.putStringArrayList("targets", settingsData.getTargetsAsStrings());
		
		return bundle;
	}
    
    private void SettingsUpdated(){
		
		FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
			@Override
			public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
				if(FirebaseAuth.getInstance().getCurrentUser() != null && isVisible()){
					FireBaseHelper.Companion.getInstance().userSettingsQuery(FirebaseAuth.getInstance().getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
						@Override
						public void onDataChange(DataSnapshot dataSnapshot) {
							if(isVisible()){
								Refresh(RollDefaultBundle());
								Toast.makeText(getActivity(), getString(R.string.defualt_data_refreshed), Toast.LENGTH_LONG).show();
								_shakeDetector.setSensitivity(DataStorage.Companion.getINSTANCE().getSettings().getShakeThreseHold());
							}
						}
						
						@Override
						public void onCancelled(DatabaseError databaseError) {
						
						}
					});
				}
			}
		});
		
	}
	
	private void InistliseNewListInput(Bundle bundle){
  
		_inputListAdapter = new ListInputAdapterNew(getActivity());
		_viewHolder.recyclerView.setAdapter(_inputListAdapter);
		_viewHolder.recyclerView.addItemDecoration(new DividerItemDecoration(_viewHolder.recyclerView.getContext(), DividerItemDecoration.VERTICAL));
		
		_targetAdapterData.clear();
		_inputListAdapter.addData(GetSidesInputAdapterData(bundle.getString("sides")));
		_inputListAdapter.addData(GetAmountAdapterData(bundle.getString("NumberOfDice")));
		UpdateTargetList(bundle);
		_inputListAdapter.addData(_targetAdapterData);
	}
	
	private TargetAdapterData GetSidesInputAdapterData(String input){
		String title = getString(R.string.sides_input_title);
		String subTitle = getString(R.string.sides_input_subtitle);
		
		return new TargetAdapterData(title, subTitle, input, ListInputAdapterNew.EntireType.NUMBER_INPUT);
	}
	
	private TargetAdapterData GetAmountAdapterData(String input){
		String title = getString(R.string.amount_input_title);
		String subTitle = getString(R.string.amount_input_subtitle);
		
		return new TargetAdapterData(title, subTitle, input, ListInputAdapterNew.EntireType.NUMBER_INPUT);
	}
	
	private void InitialiseRollButton(){
		_viewHolder.rollButton.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View view) {
				Roll();
			}
		});
	}
	
	private void AddTargetToList(){
		String hint;
		
		if(_inputListAdapter.getItemCount() >= _targetHints.size()){
			hint = "4+";
		}else{
			hint = _targetHints.get(_inputListAdapter.getItemCount());
		}
		
		String title = String.format(getString(R.string.target_title), UtilsClass.ordinal(_inputListAdapter.getCountForType(ListInputAdapterNew.EntireType.TARGET_INPUT) + 1));
		
		_inputListAdapter.addData(new TargetAdapterData(title, getString(R.string.target_subTitle), hint, ListInputAdapterNew.EntireType.TARGET_INPUT));
	}
	
	private void UpdateTargetList(Bundle bundle){
		_targetHints = bundle.getStringArrayList("targets");

		for (int i = 0; i < _targetHints.size(); i++){
			AddTargetToList();
		}
		
		FixListViewHeight();
	}
	
	private void FixListViewHeight(){
		
		//Needs to be done after it has been ready made good
		getView().getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						
						android.view.ViewGroup.LayoutParams lp = (android.view.ViewGroup.LayoutParams)_viewHolder.recyclerView.getLayoutParams();
						
						lp.height = (int)(_viewHolder.rollButton.getY()- _viewHolder.recyclerView.getY());
						
						_viewHolder.recyclerView.setLayoutParams(lp);
						
						if(getView() != null){
							getView().getViewTreeObserver().removeOnGlobalLayoutListener(this);
						}
					}
				}
		);
	}
	
	private void Roll(Boolean playSound){
		StringBuilder errorMessageBuilder = new StringBuilder("");
		
		ListInputAdapterNew inputAdapterNew = (ListInputAdapterNew)_viewHolder.recyclerView.getAdapter();
		
		String sidesInput = inputAdapterNew.getItemWithType(0, ListInputAdapterNew.EntireType.NUMBER_INPUT).get_inputtedText();
		String amountInput = inputAdapterNew.getItemWithType(1, ListInputAdapterNew.EntireType.NUMBER_INPUT).get_inputtedText();
		
		ArrayList<String> inputs = new ArrayList<>();
		inputs.add(sidesInput);
		inputs.add(amountInput);
		
		int sides = 0;
		
		if(!sidesInput.equals("") && !amountInput.equals("")){
			sides = Integer.parseInt(sidesInput);
		}else{
			errorMessageBuilder.append(getString(R.string.error_empty_field));
		}
		
		
		List<String> targetStrings = new ArrayList<>();
		List<TargetData> targets = new ArrayList<>();
		
		ListInputAdapterNew.EntireType targetType = ListInputAdapterNew.EntireType.TARGET_INPUT;
		
		for (int i = 0; i < inputAdapterNew.getCountForType(ListInputAdapterNew.EntireType.TARGET_INPUT); i++){
			
			targetStrings.add(inputAdapterNew.getItemWithType(i, targetType).get_inputtedText());
			inputs.add(targetStrings.get(i));
			
			if(!UtilsClass.vaildTarget(targetStrings.get(i))){
				errorMessageBuilder.append(getString(R.string.error_invalid_target_field));
				errorMessageBuilder.append("\n");
			} else {
				
				TargetEnum targetEnum;
				
				switch (targetStrings.get(i).charAt(UtilsClass.getPostionOfNonNumber(targetStrings.get(i)))){
					case '+':
						targetEnum = TargetEnum.ABOVE;
						break;
					
					case '-':
						targetEnum = TargetEnum.BELOW;
						break;
					
					default:
						targetEnum = TargetEnum.EQUAL;
						break;
				}
				
				int reroll = 0;
				
				if(inputAdapterNew.getItemWithType(i, targetType).get_checked()){
					reroll = 1;
				}
				
				targets.add(new TargetData(Integer.parseInt(UtilsClass.removeAllNonDigits(targetStrings.get(i))), targetEnum, reroll));
				
				if(targets.get(targets.size()-1).get_target() > sides) {
					errorMessageBuilder.append(getString(R.string.error_target_cannot_be_higher_than_sides));
					errorMessageBuilder.append("\n");
				}
			}
		}
		
		for (int i = 0; i < inputs.size(); i++){
			if(inputs.get(i).length() == 0){
				errorMessageBuilder.append(getString(R.string.error_empty_field));
				errorMessageBuilder.append("\n");
			}
		}
		
		String errorMessage = errorMessageBuilder.toString();
		
		if(errorMessage.length() == 0){
			
			_statsData = new StatsData();
			
			long numberOfDice = Integer.parseInt(amountInput);
			
			_statsData.set_sides(sides);
			_statsData.set_numberOfDice(numberOfDice);

			if(numberOfDice > 1000){

				if(_rollAsync != null){
					_rollAsync.cancel(true);
				}

				_rollAsync = new RollAsync(targets);
				_rollAsync.execute(_statsData);

			} else {
				_statsData.roll(targets);
				switchToResults();
			}

			if(playSound){
				PlayRollSound(_statsData.get_sides());
			}

		}else{
			Toast.makeText(getActivity().getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
		}
	}
	
	private void Roll(){
		Roll(false);
	}
	
	private void PlayRollSound(int sides){
		int soundID = R.raw.dice_roll_loud;
		
		if(sides < 10){
			soundID = R.raw.dice_roll_small;
		}else if (sides < 20){
			soundID = R.raw.dice_roll_medaium;
		}
		
		MediaPlayer ring = MediaPlayer.create(getActivity(), soundID);
		ring.start();
	}

	private void switchToRollingUi(){

		ViewHolder viewHolderReference = _viewHolder;
		Context contextRefence = getContext();

		// I don't want to rename ignore this
		if(viewHolderReference != null && contextRefence != null){

			viewHolderReference.settingLayout.setVisibility(View.GONE);
			viewHolderReference.rollingLayout.setVisibility(View.VISIBLE);

			Animation shake = AnimationUtils.loadAnimation(contextRefence, R.anim.dice_roll);
			shake.setAnimationListener(new EndlessDiceShakeListner(viewHolderReference.rollingImage, contextRefence));

			viewHolderReference.rollingImage.setAnimation(shake);
			shake.start();
		}
	}

	private void switchToResults(){

		Bundle bundle = new Bundle();
		bundle.putParcelable("stats", _statsData);

		((MainActivity)getActivity()).switchToStatsFocus(bundle);
	}
}
