package club.psarda.dynamicinstantcircumengine.UI.Fragments.StatsFocus;

import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import club.psarda.dynamicinstantcircumengine.R;
import club.psarda.dynamicinstantcircumengine.UI.Fragments.TargetSelectorFragment;
import club.psarda.dynamicinstantcircumengine.database.DataHolders.StatsData;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StatsFocusChartFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StatsFocusChartFragment#newInstance} factory method to
 * create an INSTANCE of this fragment.
 */
public class StatsFocusChartFragment extends Fragment {
	
	public static StatsFocusChartFragment newInstance() {
		return new StatsFocusChartFragment();
	}
	
	private class ViewHolder{

		public final PieChart PieChart;
		public final BarChart BarChart;

		public ViewHolder(View view){
			PieChart = (PieChart) view.findViewById(R.id.pie_chart);
			BarChart = (BarChart)view.findViewById(R.id.bar_chart);
		}
	}
	
	public class InitialiseChartsTask extends AsyncTask<Integer, Void, Map<Class, Object>> {
		
		private WeakReference<ViewHolder> _viewHolderWeak;
		private WeakReference<StatsData> _statsDataWeak;
		
		
		public InitialiseChartsTask(ViewHolder viewHolder, StatsData statsData){
			super();
			_viewHolderWeak = new WeakReference<>(viewHolder);
			_statsDataWeak = new WeakReference<>(statsData);
		}
		
		@Override
		protected Map<Class, Object> doInBackground(Integer... params) {
			
			if(_viewHolderWeak.get() != null && _statsDataWeak.get() != null){
				SparseArray<Long> rolls = _statsDataWeak.get().get_rollData().get(params[0]).GetHistorgram();
				
				Map<Class, Object> charts = new HashMap<>();
				
				charts.put(PieChart.class, CreatePieChart(rolls, _statsDataWeak.get()));
				charts.put(BarChart.class, CreateBarChart(rolls, _statsDataWeak.get()));
				charts.put(CombinedChart.class, CreateBarChart(rolls, _statsDataWeak.get()));
				
				return charts;
			}
			
			return null;
		}
		
		
		@Override
		protected void onPostExecute(Map<Class, Object> result) {
			
			ViewHolder viewHolder = _viewHolderWeak.get();
			
			if(viewHolder != null){
				Legend legend;
				
				_viewHolderWeak.get().PieChart.setVisibility(View.VISIBLE);
				_viewHolderWeak.get().PieChart.setData((PieData)result.get(PieChart.class));
				_viewHolderWeak.get().PieChart.invalidate();
				_viewHolderWeak.get().PieChart.setCenterText(getString(R.string.stats_focus_pie_chart_title));
				legend = _viewHolderWeak.get().PieChart.getLegend();
				legend.setTextSize(13);
				
				_viewHolderWeak.get().BarChart.setVisibility(View.VISIBLE);
				_viewHolderWeak.get().BarChart.setAutoScaleMinMaxEnabled(false);
				_viewHolderWeak.get().BarChart.setData((BarData)result.get(BarChart.class));
				_viewHolderWeak.get().BarChart.invalidate();
				legend = _viewHolderWeak.get().PieChart.getLegend();
				legend.setTextSize(10);
			}
			
		}
		
		@Override
		protected void onPreExecute() {
		}
		
		
		@Override
		protected void onProgressUpdate(Void... text) {
		}
		
		private Object CreatePieChart(SparseArray<Long> rolls, StatsData statsData){
			
			List<PieEntry> entries = new ArrayList<>();
			
			for (int i = 0; i <= statsData.get_sides(); i++) {
				if(rolls.get(i, -1L) != -1L){
					entries.add(new PieEntry(rolls.get(i), Integer.toString(i)));
				}
			}
			
			PieDataSet dataSet = new PieDataSet(entries, getString(R.string.stats_focus_pie_chart_title));
			
			dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
			dataSet.setValueTextSize(11);
			
			return new PieData(dataSet);
		}
		
		private Object CreateBarChart(SparseArray<Long> rolls, StatsData statsData){
			
			ShapeDrawable shape = null;
			Drawable icon;
			boolean iconMode = statsData.get_sides() <= 6;
			
			
			List<BarEntry> entires = new ArrayList<>();
			
			if(iconMode){
				icon = getResources().getDrawable(R.drawable.dice_sides);
				
				shape = new ShapeDrawable (new RectShape());
				shape.setIntrinsicWidth (icon.getBounds().width() + 10);
				shape.setIntrinsicHeight (icon.getBounds().height() + 10);
				shape.getPaint().setColor(Color.WHITE);
			}
			
			for (int i = 0; i <= statsData.get_sides(); i++) {
				if(rolls.get(i, -1L) != -1L){
					if(iconMode){
						// Don't understand why i need to do this but i do
						icon = getResources().getDrawable(R.drawable.dice_sides);
						
						icon.setLevel(i - 1);
						
						LayerDrawable finalDrawable = new LayerDrawable(new Drawable[] {shape, icon});
						
						entires.add(new BarEntry(i, rolls.get(i), finalDrawable));
					}else{
						entires.add(new BarEntry(rolls.get(i), i));
					}
				}
			}
			
			BarDataSet dataSet = new BarDataSet(entires, getString(R.string.stats_focus_pie_chart_title));
			dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
			dataSet.setValueTextSize(11);
			dataSet.setIconsOffset(new MPPointF(0, 20));
			
			return new BarData(dataSet);
		}
		
	}

	private static StatsData _statsData;

	private ViewHolder _viewHolder;
	private InitialiseChartsTask _chartsTask;
	
	public StatsFocusChartFragment() {
		// Required empty public constructor
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_stats_focus_chart, container, false);
	}
	
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		
		Bundle bundle = this.getArguments();
		
		if (bundle != null && bundle.getParcelable("stats") != null) {
			_statsData = (StatsData)bundle.getParcelable("stats");
		}
		
		if(_statsData != null){
			_viewHolder = new ViewHolder(getView());
			InitialiseChart(0);
			InitialiseTargetSpinner();
		}
	}
	
	@Override
	public void onDetach() {
		super.onDetach();
	}
	
	public interface OnFragmentInteractionListener {
		void onFragmentInteraction(Uri uri);
	}
	
	private void InitialiseTargetSpinner() {
		
		AdapterView.OnItemSelectedListener itemSelectedListener = new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				InitialiseChart(i);
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {
			
			}
		};
		
		FragmentManager fragmentManager = getChildFragmentManager();
		TargetSelectorFragment targetSelectorFragment = TargetSelectorFragment.newInstance(itemSelectedListener, _statsData);
		fragmentManager.beginTransaction().add(R.id.chart_target_selector_fragment, targetSelectorFragment).commit();
	}
	
	private void InitialiseChart(int index){
		
		if(_chartsTask != null){
			_chartsTask.cancel(true);
			
		}
		
		_chartsTask = new InitialiseChartsTask(_viewHolder, _statsData);
		_chartsTask.execute(index);
	}
}
