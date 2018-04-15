package club.psarda.dynamicinstantcircumengine.UI.Fragments.StatsFocus;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import club.psarda.dynamicinstantcircumengine.Adapters.ListTargetResults.ListTwoResultAdapter;
import club.psarda.dynamicinstantcircumengine.Adapters.ListTargetResults.ListTwoResultData;
import club.psarda.dynamicinstantcircumengine.R;
import club.psarda.dynamicinstantcircumengine.UI.Fragments.RollFragment;
import club.psarda.dynamicinstantcircumengine.UI.MainActivity;
import club.psarda.dynamicinstantcircumengine.Utils.UtilsClass;
import club.psarda.dynamicinstantcircumengine.database.DataHolders.RollData;
import club.psarda.dynamicinstantcircumengine.database.DataHolders.StatsData;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ResultsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ResultsFragment#newInstance} factory method to
 * create an INSTANCE of this fragment.
 */
public class ResultsFragment extends Fragment {
	
	public static ResultsFragment newInstance() {
		return new ResultsFragment();
	}
	
	public enum States{
		POST_ROLL, STATS
	}
	
	private class ViewHolder{

		public final ListView targetsList;
		public final Button keepRollingButton;
		public final Button viewStatsButton;


		public ViewHolder(View view){
			targetsList = (ListView)view.findViewById(R.id.result_targets);
			keepRollingButton = (Button)view.findViewById(R.id.result_keep_rolling);
			viewStatsButton = (Button)view.findViewById(R.id.result_view_stats);
		}

	}

    private static StatsData _statsData;
	private ViewHolder _viewHolder;
	private States _state = States.POST_ROLL;

    public ResultsFragment() {
        // Required empty public constructor
    }
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_results, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

		_viewHolder = new ViewHolder(getView());

		Bundle bundle = this.getArguments();

		if (bundle != null && bundle.getParcelable("stats") != null) {
			
			// This is legacy which i don't have the time to fix igonre it
            InitialiseResultTotalDice((StatsData)bundle.getParcelable("stats"));
            
			if(bundle.getString(ResultsFragment.States.class.getCanonicalName()) != null){
				_state = States.valueOf(bundle.getString(ResultsFragment.States.class.getCanonicalName()));
			}
			
			// End of garbage
			
			InitialiseKeepGoingButton();
			InitialiseViewStatsButton();
			FixResultListHeight();
			
		}
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
	
    private void InitialiseResultTotalDice(StatsData statsData){
        
        _statsData = statsData;
        
		GetTotalDiceSideTextView().setText(statsData.get_numberOfDice() + getString(R.string.results_D) + statsData.get_sides());
	
		ArrayList<ListTwoResultData> targetAdapterData = new ArrayList<>();

		int i = 0;
		for (RollData rollData : statsData.get_rollData()){
			i++;
			
			List<String> titles = new ArrayList<>();
			List<String> results = new ArrayList<>();
			
			titles.add(getString(R.string.results_successful_title));
			titles.add(getString(R.string.results_failed_title));
			results.add(Long.toString(rollData.get_successfulRolls()));
			results.add(Long.toString((rollData.get_totalRolls() - rollData.get_successfulRolls()) - 1));
			
			targetAdapterData.add(
				new ListTwoResultData(
					getString(R.string.results_target_title, UtilsClass.ordinal(i), rollData.get_target()),
					titles,
					results
				)
			);
		}
	
        ListAdapter resultTargetAdapter = new ListTwoResultAdapter(getActivity(), targetAdapterData);
		_viewHolder.targetsList.setAdapter(resultTargetAdapter);
    }
    
    private void InitialiseKeepGoingButton(){
	
		switch (_state) {
			case STATS:
				
				RelativeLayout.LayoutParams ll = (RelativeLayout.LayoutParams) _viewHolder.keepRollingButton.getLayoutParams();
				ll.addRule(RelativeLayout.CENTER_IN_PARENT);
				_viewHolder.keepRollingButton.setLayoutParams(ll);
				
				break;
		}
  
		_viewHolder.keepRollingButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				List<RollData> rollDataList = _statsData.get_rollData();

				Bundle bundle = new Bundle();
				bundle.putString("NumberOfDice", Long.toString(rollDataList.get(rollDataList.size() - 1).get_successfulRolls()));
				bundle.putString("sides", Integer.toString(_statsData.get_sides()));

				ArrayList<String> targets = new ArrayList<>();

				for(int i = 0; i < rollDataList.size(); i++){
					targets.add(rollDataList.get(i).get_target());
				}

				bundle.putStringArrayList("targets", targets);
				bundle.putString(RollFragment.States.class.getCanonicalName(), RollFragment.States.KEEP_ON_ROLLING.name());

				((MainActivity)getActivity()).switchToRoll(bundle);
			}
		});
	}
	
	private void InitialiseViewStatsButton(){

    	switch (_state){
			case STATS:
				_viewHolder.viewStatsButton.setVisibility(View.GONE);
				break;
				
			case POST_ROLL:
				_viewHolder.viewStatsButton.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Bundle bundle = new Bundle();
						bundle.putParcelable("stats", _statsData);
						((MainActivity)getActivity()).switchToStats(bundle);
					}
				});
				break;
		}
		
	}
	
	private TextView GetTotalDiceSideTextView(){
    	return (TextView)getActivity().findViewById(R.id.results_total_dice_count_sides);
	}
	
	private void FixResultListHeight(){
		
		//Needs to be done after it has been ready made good
		getView().getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {
					@Override
					public void onGlobalLayout() {
						
						android.view.ViewGroup.LayoutParams lp = (android.view.ViewGroup.LayoutParams)_viewHolder.targetsList.getLayoutParams();
						
						lp.height = (int)(_viewHolder.keepRollingButton.getY()- _viewHolder.targetsList.getY());
						
						_viewHolder.targetsList.setLayoutParams(lp);
						
						if(getView() != null){
							getView().getViewTreeObserver().removeOnGlobalLayoutListener(this);
						}
					}
				}
		);
	}
}
