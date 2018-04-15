package club.psarda.dynamicinstantcircumengine.database.DataHolders;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import club.psarda.dynamicinstantcircumengine.R;

/**
 * Created by pfsar on 12/10/2017.
 */

public class StatsDataFileGenerator {
	
	private Context _contex;
	private StatsData _statsData;
	
	public StatsDataFileGenerator(Context context){
		_contex = context;
	}
	
	public void set_statsData(StatsData statsData){
		_statsData = statsData;
	}
	
	public File GenrateFile(){
		File result;
		
		try	{
			result = File.createTempFile("stats", "stats");
			result.setWritable(true);
			
			if(result.canWrite()){
				FileWriter fileWriter = new FileWriter(result);
				
				fileWriter.write(_contex.getString(R.string.stats_export_secound_row));
				
				StringBuilder stringBuilder = new StringBuilder();
				
				stringBuilder.append(_statsData.get_name());
				stringBuilder.append(",");
				stringBuilder.append(_statsData.get_sides());
				stringBuilder.append(",");
				stringBuilder.append(_statsData.get_numberOfDice());
				stringBuilder.append(",");
				stringBuilder.append(_statsData.get_timeCreation());
				stringBuilder.append(",");
				stringBuilder.append(_statsData.get_averageRoll());
				stringBuilder.append(",");
				stringBuilder.append(_statsData.get_seed());
				
				fileWriter.write(stringBuilder.toString());
				
				fileWriter.write(_contex.getString(R.string.stats_export_secound_row));
				
				for(RollData rollData : _statsData.get_rollData()){
					for(int i = 0; i < rollData.get_rolls().size(); i++){
						fileWriter.write(rollData.get_target() + Integer.toString(i) + Integer.toString(rollData.get_rolls().get(i)));
					}
				}
				
				return result;
				
			}else{
				throw new IOException();
			}
			
		}catch (Exception e){
			Toast.makeText(_contex, _contex.getString(R.string.error_cannot_create_file), Toast.LENGTH_LONG).show();
		}
		
		return null;
	}
}
