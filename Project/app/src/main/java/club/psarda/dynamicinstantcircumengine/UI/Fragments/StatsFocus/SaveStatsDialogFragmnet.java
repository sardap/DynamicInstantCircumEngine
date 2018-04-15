package club.psarda.dynamicinstantcircumengine.UI.Fragments.StatsFocus;

import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import club.psarda.dynamicinstantcircumengine.AsyncTasks.SaveAsyncTask;
import club.psarda.dynamicinstantcircumengine.R;
import club.psarda.dynamicinstantcircumengine.UI.MainActivity;
import club.psarda.dynamicinstantcircumengine.database.DataHolders.StatsData;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SaveStatsDialogFragmnet.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SaveStatsDialogFragmnet#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SaveStatsDialogFragmnet extends DialogFragment {
	
	private class ViewHolder{
		
		public EditText NameInput;
		public Button SaveButton;
		
		public ViewHolder(View view) {
			NameInput = (EditText) view.findViewById(R.id.stats_input);
			SaveButton = (Button) view.findViewById(R.id.stats_save_dialog_save_button);
		}
	}
	
	private StatsData _statsData;
	private ViewHolder _viewHolder;
	
	public SaveStatsDialogFragmnet() {
		// Required empty public constructor
	}
	
	public static SaveStatsDialogFragmnet newInstance(StatsData statsData) {
		SaveStatsDialogFragmnet f = new SaveStatsDialogFragmnet();
		
		// Supply num input as an argument.
		Bundle args = new Bundle();
		args.putParcelable("stats", statsData);
		f.setArguments(args);
		
		return f;
	}
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_save_stats_dialog, container, false);
	}
	
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		
		Bundle bundle = this.getArguments();
		
		if (bundle != null && bundle.getParcelable("stats") != null) {
			_statsData = (StatsData)bundle.getParcelable("stats");
		}
		
		if(_statsData != null){
			_viewHolder = new ViewHolder(getView());
			InitialiseNameInput();
			InistiliseSaveButton();
		}
	}
	
	
	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
	}
	
	public interface OnFragmentInteractionListener {
		// TODO: Update argument type and name
		void onFragmentInteraction(Uri uri);
	}
	
	private void InitialiseNameInput(){
		
		_viewHolder.NameInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int keyCode, KeyEvent keyEvent) {
				if (keyEvent.getAction() == KeyEvent.ACTION_DOWN)
				{
					switch (keyCode)
					{
						case 0:
							SaveAndSwitchIfValid();
							View view = getActivity().getCurrentFocus();
							if (view != null) {
								InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
								imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
							}
							
							return true;
						
						default:
							break;
					}
				}
				return false;
			}
		});
	}
	
	private void InistiliseSaveButton(){
		_viewHolder.SaveButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				SaveAndSwitchIfValid();
			}
		});
	}
	
	private void SaveAndSwitchIfValid(){
		String errorMessage = "";
		String nameInputString = _viewHolder.NameInput.getText().toString();
		
		if(nameInputString.length() == 0){
			errorMessage += getString(R.string.stats_error_empty_name);
		}
		
		if(errorMessage.length() == 0){
			_statsData.set_name(_viewHolder.NameInput.getText().toString());
			SaveAsyncTask saveTask = new SaveAsyncTask(getActivity().getApplicationContext());
			saveTask.execute(_statsData);
			((MainActivity)getActivity()).switchToRoll(null);
			this.dismiss();
		}else{
			Toast.makeText(getActivity().getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
		}
	}
	
}
