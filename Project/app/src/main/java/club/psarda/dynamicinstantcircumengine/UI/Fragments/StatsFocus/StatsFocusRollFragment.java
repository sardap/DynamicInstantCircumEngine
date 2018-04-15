package club.psarda.dynamicinstantcircumengine.UI.Fragments.StatsFocus;


import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.squareup.seismic.ShakeDetector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import club.psarda.dynamicinstantcircumengine.Listners.DiceShakeListener;
import club.psarda.dynamicinstantcircumengine.R;
import club.psarda.dynamicinstantcircumengine.UI.Fragments.TargetSelectorFragment;
import club.psarda.dynamicinstantcircumengine.Utils.UtilsClass;
import club.psarda.dynamicinstantcircumengine.database.DataHolders.RollData;
import club.psarda.dynamicinstantcircumengine.database.DataHolders.StatsData;
import club.psarda.dynamicinstantcircumengine.database.FireBase.DataStorage;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StatsFocusRollFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StatsFocusRollFragment#newInstance} factory method to
 * create an INSTANCE of this fragment.
 */
public class StatsFocusRollFragment extends Fragment implements ShakeDetector.Listener {
	
	private static final int NUMBER_OF_COL = 3;
	
	private static StatsData _statsData;
	
	public static StatsFocusRollFragment newInstance() {
		return new StatsFocusRollFragment();
	}
	
	public class RollGridAdapter extends RecyclerView.Adapter<RollGridAdapter.ViewHolder> {

		public class ViewHolder extends RecyclerView.ViewHolder{

			public TextView title;
			public ImageButton imgCenter;
			public TextView textCenter;
			public View layout;

			public ViewHolder(View v) {
				super(v);
				layout = v;

				title = (TextView)v.findViewById(R.id.grid_title);
				imgCenter = (ImageButton)v.findViewById(R.id.grid_image);
				textCenter = (TextView)v.findViewById(R.id.grid_text);
			}
		}
		
		private class DiceShakeListenerUpdater extends DiceShakeListener {
			
			public DiceShakeListenerUpdater(ImageButton imageButton, int roll, Context context) {
				super(imageButton, roll, 0, START_ANIMATION_DURATION, context);
			}
			
			private DiceShakeListenerUpdater(ImageButton imageButton, int roll, int repeats, long curDuration, Context context){
				super(imageButton, roll, repeats, curDuration, context);
			}
				
			@Override
			public void onAnimationEnd(Animation animation) {
				super.onAnimationEnd(animation);
				
				if(get_repeats() > NUMBER_OF_REPEATS){
					_playAnmnation = false;
				}
			}
		}
		
		public boolean _playAnmnation = false;
		private Context mContext;
		private List<Integer> _data = new ArrayList<>();
		private LayoutInflater mInflater;
		private boolean _sixsided;
		private MediaPlayer _rollingSound;

		public RollGridAdapter(Context c, RollData rollData) {
			mContext = c;
			mInflater = LayoutInflater.from(mContext);
			
			if(rollData.get_rolls().size() > 0){
				_data = rollData.get_rolls();
				_sixsided = Collections.min(_data) >= 1 && Collections.max(_data) <= 6;
				_rollingSound = MediaPlayer.create(mContext, R.raw.dice_roll_small);
			}
		}

		public void add(int position, Integer item) {
			_data.add(position, item);
			notifyItemInserted(position);
		}

		@Override
		public RollGridAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			View view = mInflater.inflate(R.layout.grid_adapter_roll, parent, false);
			return new RollGridAdapter.ViewHolder(view);
		}

		@Override
		public void onBindViewHolder(RollGridAdapter.ViewHolder holder, final int position) {
			String title = String.format(mContext.getString(R.string.stats_focus_roll_grid_title), UtilsClass.ordinal(position + 1));

			holder.title.setText(title);
			if(_sixsided) {
				initialiseImageButton(holder, _data.get(position));
			}else{
				holder.textCenter.setText(GetTextForCenter(_data.get(position)));
			}
		}

		private String GetTextForCenter(int roll){
			return Integer.toString(roll);
		}

		
		private class ImageButtonListner implements View.OnClickListener{

			private ImageButton _imageButton;
			private int _roll;

			public ImageButtonListner(ImageButton imageButton, int roll){
				_imageButton = imageButton;
				_roll = roll;
			}

			@Override
			public void onClick(View view) {
				if(_imageButton.getAnimation() != null){
					_imageButton.setAnimation(null);
					_imageButton.setImageLevel(_roll - 1);
				}
			}
		}

		private void initialiseImageButton(RollGridAdapter.ViewHolder holder, int rollData){
			
			if(_playAnmnation){
				
				Animation shake = AnimationUtils.loadAnimation(mContext, R.anim.dice_roll);
				shake.setAnimationListener(new DiceShakeListenerUpdater(holder.imgCenter, rollData, getContext()));
				
				holder.imgCenter.setAnimation(shake);
				shake.start();

				holder.imgCenter.setOnClickListener(new ImageButtonListner(holder.imgCenter, rollData));

				if(!_rollingSound.isPlaying()){
					_rollingSound = MediaPlayer.create(mContext, R.raw.dice_roll_small);
					_rollingSound.start();
				}
			}else{
				holder.imgCenter.setImageLevel(rollData - 1);
			}

		}

		@Override
		public int getItemCount() {
			return _data.size();
		}
	}


	private class ViewHolder {
		public final RecyclerView recyclerView;
		
		public ViewHolder(View v){
			recyclerView = (RecyclerView)getActivity().findViewById(R.id.roll_grid);
		}
	}
	
	private ViewHolder _viewHolder;
	private RecyclerView.Adapter _adapter;

	public StatsFocusRollFragment() {
		// Required empty public constructor
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		DataStorage dataStorage = DataStorage.Companion.getINSTANCE();
		
		SensorManager sensorManager = (SensorManager) getActivity().getSystemService(getActivity().SENSOR_SERVICE);
		ShakeDetector sd = new ShakeDetector(this);
		if(DataStorage.Companion.getINSTANCE().getSettingsLoaded()){
			sd.setSensitivity(dataStorage.getSettings().getShakeThreseHold());
		}
		sd.start(sensorManager);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_stats_foucs_roll, container, false);
	}
	
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		
		Bundle bundle = this.getArguments();
		
		if (bundle != null && bundle.getParcelable("stats") != null) {
			_statsData = (StatsData)bundle.getParcelable("stats");
		}
		
		if(_statsData != null){
			_viewHolder = new ViewHolder(getView());
			InitialiseTargetSpinner();
		}
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
	}

	@Override
	public void hearShake() {
		if(isVisible()){
			((RollGridAdapter)_adapter)._playAnmnation = true;
			_adapter.notifyDataSetChanged();
		}
	}
	
	public interface OnFragmentInteractionListener {
		void onFragmentInteraction(Uri uri);
	}
	
	private void InitialiseRollGrid(int index){
		_viewHolder.recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), NUMBER_OF_COL));
		_adapter = new RollGridAdapter(getActivity(), _statsData.get_rollData().get(index));
		_viewHolder.recyclerView.setAdapter(_adapter);
	}
	
	private void InitialiseTargetSpinner(){
		
		AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				InitialiseRollGrid(i);
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {
			
			}
		};
		
		FragmentManager fragmentManager = getChildFragmentManager();
		TargetSelectorFragment targetSelectorFragment = TargetSelectorFragment.newInstance(itemSelectedListener, _statsData);
		fragmentManager.beginTransaction().add(R.id.roll_target_selector_fragment, targetSelectorFragment).commit();
	}
}
