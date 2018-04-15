package club.psarda.dynamicinstantcircumengine.UI.Fragments.StatsFocus;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import club.psarda.dynamicinstantcircumengine.Adapters.RollPagerAdapter;
import club.psarda.dynamicinstantcircumengine.R;
import club.psarda.dynamicinstantcircumengine.UI.MainActivity;
import club.psarda.dynamicinstantcircumengine.database.DataHolders.StatsData;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StatsFocusFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ResultsFragment#newInstance} factory method to
 * create an INSTANCE of this fragment.
 */
public class StatsFocusFragment extends Fragment {
	
	public enum States{
		VIEW_MODE, POST_ROLL
	}
	
	private static StatsData _statsData;
	private States _state = States.POST_ROLL;
	private ShareActionProvider _shareActionProvider;
	
	public StatsFocusFragment() {
		// Required empty public constructor
	}

	public static StatsFocusFragment newInstance() {
		return new StatsFocusFragment();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if(((MainActivity)getActivity()).getGoogleSignedIn()){
			setHasOptionsMenu(true);
		}
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.stats_fragmnet_menu, menu);
		
		switch (_state){
			case VIEW_MODE:
				menu.findItem(R.id.save_menu_button).setVisible(false);
				break;
		}
		
		MenuItem shareItem = menu.findItem(R.id.stats_export_button);
		
		_shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
		
		
		super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		
		switch (id){
			case R.id.save_menu_button:
				((MainActivity)getActivity()).showSaveDialog(_statsData);
				break;
			case R.id.stats_export_button:

				break;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_stats_focus, container, false);
	}
	
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

		Bundle bundle = this.getArguments();
		
		if (bundle != null) {
			
			if(bundle.getParcelable("stats") != null){
				_statsData = (StatsData)bundle.getParcelable("stats");
			}
			
			if(bundle.getString(States.class.getCanonicalName()) != null){
				_state = States.valueOf(bundle.getString(States.class.getCanonicalName()));
			}
		}
		
		if(_statsData != null){
			RollPagerAdapter rollPagerAdapter = new RollPagerAdapter(getChildFragmentManager());
			rollPagerAdapter.SetContex(getContext());
			rollPagerAdapter.set_statData(_statsData);
			
			ViewPager mViewPager = (ViewPager) view.findViewById(R.id.stats_focus_pager);
			mViewPager.setAdapter(rollPagerAdapter);
		}
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
	}
	
	public interface OnFragmentInteractionListener {
		void onFragmentInteraction(Uri uri);
	}
	
}
