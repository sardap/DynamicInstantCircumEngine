package club.psarda.dynamicinstantcircumengine.UI.Fragments;

import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;

import club.psarda.dynamicinstantcircumengine.R;
import club.psarda.dynamicinstantcircumengine.Utils.UtilsClass;
import club.psarda.dynamicinstantcircumengine.database.DataHolders.StatsData;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TargetSelectorFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TargetSelectorFragment#newInstance} factory method to
 * create an INSTANCE of this fragment.
 */
public class TargetSelectorFragment extends Fragment {
	
	public static final String PRAM_STATS_DATA = "stats";
	
	public static TargetSelectorFragment newInstance(AdapterView.OnItemSelectedListener itemSelectedListener, StatsData statsData) {
		
		Bundle bundle = new Bundle();
		bundle.putParcelable(PRAM_STATS_DATA, statsData);
		
		TargetSelectorFragment targetSelectorFragment = new TargetSelectorFragment();
		targetSelectorFragment.set_itemSelectedListener(itemSelectedListener);
		targetSelectorFragment.setArguments(bundle);
		
		return targetSelectorFragment;
	}
	
	private AdapterView.OnItemSelectedListener _itemSelectedListener;
	
	public TargetSelectorFragment() {
		// Required empty public constructor
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_target_selector, container, false);
	}
	
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		Bundle bundle = this.getArguments();

		StatsData statsData= null;

		if (bundle != null && bundle.getParcelable(PRAM_STATS_DATA) != null) {
			statsData = (StatsData)bundle.getParcelable(PRAM_STATS_DATA);
		}

		if(statsData != null){
			initialiseTargetSelector(statsData);
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
	
	public void set_itemSelectedListener(AdapterView.OnItemSelectedListener itemSelectedListener){
		_itemSelectedListener = itemSelectedListener;
	}
	
	public interface OnFragmentInteractionListener {
		void onFragmentInteraction(Uri uri);
	}
	
	private void initialiseTargetSelector(StatsData statsData){

		Spinner targetSpinner = (Spinner) getView().findViewById(R.id.target_selector);
		
		ArrayList<String> entires = new ArrayList<String>();
		
		for (int i = 0; i < statsData.get_rollData().size(); i++) {
			entires.add(getString(R.string.stats_focus_roll_grid_spinner, UtilsClass.ordinal(i + 1)));
		}
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(), R.layout.normal_spinner, entires);
		adapter.setDropDownViewResource(R.layout.dropdown_item_frag_spinner);
		targetSpinner.setAdapter(adapter);
		targetSpinner.setOnItemSelectedListener(_itemSelectedListener);
	}
}
