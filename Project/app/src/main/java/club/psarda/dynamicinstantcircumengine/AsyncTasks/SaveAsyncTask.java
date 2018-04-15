package club.psarda.dynamicinstantcircumengine.AsyncTasks;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.lang.ref.WeakReference;

import club.psarda.dynamicinstantcircumengine.R;
import club.psarda.dynamicinstantcircumengine.database.DataHolders.StatsData;
import club.psarda.dynamicinstantcircumengine.database.FireBase.DataStorage;

/**
 * Created by pfsar on 30/09/2017.
 */

public class SaveAsyncTask extends AsyncTask<StatsData, String, String> {
	
	private WeakReference<Context> _contex;
	
	public SaveAsyncTask(Context context){
		super();
		_contex = new WeakReference<>(context);
	}
	
	@Override
	protected String doInBackground(StatsData... params) {
		DataStorage.Companion.getINSTANCE().addStatsData(params[0]);

		if(_contex.get() != null){
			return String.format(_contex.get().getString(R.string.stats_synced), params[0].get_name());
		}
		
		return "";
	}
	
	
	@Override
	protected void onPostExecute(String result) {
		if(_contex.get() != null){
			Toast.makeText(_contex.get(), result, Toast.LENGTH_LONG).show();
		}
	}
	
	
	@Override
	protected void onPreExecute() {
	}
	
	
	@Override
	protected void onProgressUpdate(String... text) {
	
	}
}
