package club.psarda.dynamicinstantcircumengine.UI.Fragments.StatsFocus;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.math.RoundingMode;
import java.text.DecimalFormat;

import club.psarda.dynamicinstantcircumengine.Adapters.ListNumberInput.ListInputAdapterNew;
import club.psarda.dynamicinstantcircumengine.Adapters.ListNumberInput.TargetAdapterData;
import club.psarda.dynamicinstantcircumengine.R;
import club.psarda.dynamicinstantcircumengine.Utils.UtilsClass;
import club.psarda.dynamicinstantcircumengine.database.DataHolders.StatsData;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link KeyStatsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link KeyStatsFragment#newInstance} factory method to
 * create an INSTANCE of this fragment.
 */
public class KeyStatsFragment extends Fragment {
	
	private static StatsData _statsData;
	
	private class ViewHolder{
		
		public RecyclerView statsList;
		
		public ViewHolder(View view){
			statsList = (RecyclerView) view.findViewById(R.id.stats_target_results);
		}
	}
	
	private ViewHolder _viewHolder;
	
	public KeyStatsFragment() {
		// Required empty public constructor
	}
	
	public static KeyStatsFragment newInstance() {
		return new KeyStatsFragment();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_stats, container, false);
	}
	
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		
		Bundle bundle = this.getArguments();
		
		if (bundle != null && bundle.getParcelable("stats") != null) {
			_statsData = (StatsData)bundle.getParcelable("stats");
		}
		
		if(_statsData != null){
			_viewHolder = new ViewHolder(getView());
			
			initialiseTargetStats();
		}
	}
	
	@Override
	public void onResume(){
		super.onResume();
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
	}
	
	public interface OnFragmentInteractionListener {
		void onFragmentInteraction(Uri uri);
	}
	
	private void initialiseTargetStats(){
		
		ListInputAdapterNew resultTargetAdapter = new ListInputAdapterNew(getContext());
		_viewHolder.statsList.setAdapter(resultTargetAdapter);
		_viewHolder.statsList.addItemDecoration(new DividerItemDecoration(_viewHolder.statsList.getContext(), DividerItemDecoration.VERTICAL));
		
		String subTitlePercentage = getString(R.string.stats_target_subtitle_percentage);
		String subTitleAverage = getString(R.string.stats_target_subtitle_percentage);
		String subTitleRerolls = getString(R.string.stats_target_reroll_percentage_subtitle);
		
		resultTargetAdapter.addData(getAvengeRoll());
		resultTargetAdapter.addData(getTargetDataForRollGood());
		
		ListInputAdapterNew.EntireType layoutType = ListInputAdapterNew.EntireType.RESULT_ONE;
		
		for (int i = 0; i < _statsData.get_rollData().size(); i++){
			
			String title = String.format(getString(R.string.stats_target_title_average), UtilsClass.ordinal(i + 1));
			String result = getDecimalFormat().format(_statsData.get_rollData().get(i).GetAverageDouble());
			
			resultTargetAdapter.addData(new TargetAdapterData(title, subTitlePercentage, result, layoutType));
			
			title = String.format(getString(R.string.stats_target_title_percentage), UtilsClass.ordinal(i + 1));
			result = getDecimalFormat().format(_statsData.get_rollData().get(i).GetPercentageOfHits()) + "%";
			
			resultTargetAdapter.addData(new TargetAdapterData(title, subTitleAverage, result, layoutType));
			
			
			if(_statsData.get_rollData().get(i).GetRawTargetData().get_reroll() != 0){
				
				title = String.format(getString(R.string.stats_target_reroll_percentage_title), UtilsClass.ordinal(i + 1));
				result = getDecimalFormat().format(_statsData.get_rollData().get(i).get_rerolls());
				
				resultTargetAdapter.addData(new TargetAdapterData(title, subTitleRerolls, result, layoutType));
			}
		}
		
	}
	
	private TargetAdapterData getAvengeRoll(){
		
		Double averageRoll = _statsData.get_averageRoll();
		
		return new TargetAdapterData(
				getString(R.string.stats_average_good_roll_title),
				getString(R.string.stats_average_good_roll_subtitle),
				getDecimalFormat().format(averageRoll),
				ListInputAdapterNew.EntireType.RESULT_ONE
		);
	}
	
	private TargetAdapterData getTargetDataForRollGood(){
		
		Double averageDiff = _statsData.get_averageRoll() - _statsData.getAverageForSides();
		
		char symobl;
		
		if(averageDiff > 0){
			symobl = '>';
		}else{
			symobl = '<';
			averageDiff *= -1;
		}
		
		return new TargetAdapterData(
				getString(R.string.stats_average_roll_title),
				getString(R.string.stats_average_roll_subtitle),
				symobl + (getDecimalFormat().format(averageDiff)),
				ListInputAdapterNew.EntireType.RESULT_ONE
		);
	}
	
	
	
	private DecimalFormat getDecimalFormat(){
		DecimalFormat df = new DecimalFormat("#.###");
		df.setRoundingMode(RoundingMode.CEILING);
		
		return df;
	}
}
