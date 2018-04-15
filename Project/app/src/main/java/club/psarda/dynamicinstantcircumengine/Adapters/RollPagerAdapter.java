package club.psarda.dynamicinstantcircumengine.Adapters;


import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;

import club.psarda.dynamicinstantcircumengine.UI.Fragments.StatsFocus.ResultsFragment;
import club.psarda.dynamicinstantcircumengine.UI.Fragments.StatsFocus.StatsFocusChartFragment;
import club.psarda.dynamicinstantcircumengine.UI.Fragments.StatsFocus.StatsFocusRollFragment;
import club.psarda.dynamicinstantcircumengine.R;
import club.psarda.dynamicinstantcircumengine.UI.Fragments.StatsFocus.KeyStatsFragment;
import club.psarda.dynamicinstantcircumengine.database.DataHolders.StatsData;

/**
 * Created by pfsar on 13/09/2017.
 */

public class RollPagerAdapter extends FragmentPagerAdapter {
	
	private enum FragmentTypes {
		RESULT, ROLL, STATS, HISTOGRAM
	}
	
	private static final int NUM_ITEMS = 4;
	private Context _mContext;
	private StatsData _statsData;
	
	public RollPagerAdapter(FragmentManager fragmentManager) {
		super(fragmentManager);
	}
	
	public void set_statData(StatsData statData){
		_statsData = statData;
	}
	
	public void SetContex(Context context){
		_mContext = context;
	}
	
	// Returns total number of pages
	@Override
	public int getCount() {
		return NUM_ITEMS;
	}
	
	// Returns the fragment to display for that page
	@Override
	public Fragment getItem(int position) {
		Bundle bundle = new Bundle();
		bundle.putParcelable("stats", _statsData);
		
		Fragment fragment = null;
		
		FragmentTypes type = FragmentTypes.values()[position];
		
		switch (type) {
			case STATS:
				fragment = KeyStatsFragment.newInstance();
				bundle.putBoolean("view_mode", true);
				break;
			case ROLL:
				fragment = StatsFocusRollFragment.newInstance();
				break;
			case HISTOGRAM:
				fragment = StatsFocusChartFragment.newInstance();
				break;
			case RESULT:
				fragment = ResultsFragment.newInstance();
				bundle.putString(ResultsFragment.States.class.getCanonicalName(), ResultsFragment.States.STATS.name());
				break;
		}
		
		fragment.setArguments(bundle);
		
		return fragment;
	}
	
	// Returns the page title for the top indicator
	@Override
	public CharSequence getPageTitle(int position) {
		int nameResource = 0;
		
		FragmentTypes type = FragmentTypes.values()[position];
		
		switch (type){
			case STATS:
				nameResource = R.string.stats_fragmnet_title;
				break;
			case ROLL:
				nameResource = R.string.stats_focus_roll_title;
				break;
			case HISTOGRAM:
				nameResource = R.string.stats_focus_chart_title;
				break;
			case RESULT:
				nameResource = R.string.result_fragmnet_title;
				break;
		}
		
		return _mContext.getString(nameResource);
	}
	
}