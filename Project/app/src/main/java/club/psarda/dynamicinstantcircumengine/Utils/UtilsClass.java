package club.psarda.dynamicinstantcircumengine.Utils;

import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import club.psarda.dynamicinstantcircumengine.database.DataHolders.TargetEnum;
import club.psarda.dynamicinstantcircumengine.database.DataHolders.RollData;
import club.psarda.dynamicinstantcircumengine.database.DataHolders.StatsData;
import club.psarda.dynamicinstantcircumengine.database.DataHolders.TargetData;

/**
 * Created by pfsar on 5/09/2017.
 */

public class UtilsClass {
	
	// Stolen https://stackoverflow.com/questions/6810336/is-there-a-way-in-java-to-convert-an-integer-to-its-ordinal
	public static String ordinal(int i) {
		return  ordinal((long)i);
	}
	
	public static String ordinal(long i) {
		
		String[] sufixes = new String[] { "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th" };
		
		switch ((int)i % 100) {
			case 11:
			case 12:
			case 13:
				return i + "th";
			default:
				return i + sufixes[(int)i % 10];
			
		}
	}
	
	public static boolean isStringNumeric(String string){
		try {
			double parseDouble = Double.parseDouble(string);
		} catch(Exception e) {
			return false;
		}
		return  true;
	}
	
	public static boolean isInteger(String string){
		try {
			int parseDouble = Integer.parseInt(string);
		} catch(Exception e) {
			return false;
		}
		return  true;
	}
	
	public static boolean vaildTarget(String string){
		
		boolean a = string.contains("+");
		boolean b = string.contains("-");
		
		if(a && b){
			if(!(StringUtils.countMatches("+", string) > 1 && StringUtils.countMatches("-", string) > 1)){
				return false;
			}
		}
		
		string = string.replace("+", "");
		string = string.replace("-", "");
		
		return isStringNumeric(string);
	}
	
	public static int getPostionOfNonNumber(String string){
		int i = 0;
		while(i < string.length()){
			if(!Character.isDigit(string.charAt(i))){
				return  i;
			}
			i++;
		}
		
		return 0;
	}
	
	public static String removeAllNonDigits(String string){
		return string.replaceAll("[^\\d.]", "");
	}

	public static String toString(TargetEnum targetEnum){
		
		switch (targetEnum){
			case ABOVE:
				return "+";
				
			case BELOW:
				return "-";
				
			case EQUAL:
				return "";
		}
		
		return "";
	}
	
	public static TargetEnum fromChar(char ch){
		switch (ch){
			case '+':
				return TargetEnum.ABOVE;
			
			case '-':
				return TargetEnum.BELOW;
			
			default:
				return TargetEnum.EQUAL;
		}
	}
	
	public static double averageInteger(List<Integer> list) {
		if (list == null || list.isEmpty()){
			return 0.0;
		}
		
		int sum = 0;
		
		for (int i = 0; i < list.size(); i++){
			sum += list.get(i);
		}
		
		return ((double) sum) / list.size();
	}
	
	public static long getSum(List<Integer> list){
		int sum = 0;
		
		for (Integer i : list){
			sum	+= i;
		}
		
		return sum;
	}

	public static Pair<Integer, Integer> getNumberStartAndEndInString(String string){
		List<Integer> positions = new ArrayList<>();
		
		for(int i = 0; i < string.length(); i++){
			if(Character.isDigit(string.charAt(i))){
				positions.add(i);
			}
		}
		
		if(positions.size() == 1){
			return new Pair<>(0, 1);
		}
		
		return new Pair<>(positions.get(0), positions.get(positions.size()-1) + 1);
	}

	public static String addToString(String string, int toAdd){
		
		String newString = string;
		
		Pair<Integer, Integer> startEnd = getNumberStartAndEndInString(newString);
		
		int intStart = startEnd.first;
		int intEnd = startEnd.second;
		
		String substr = string.substring(intStart, intEnd);
		
		if(isInteger(substr)){
			int editTextIntValue = Integer.parseInt(string.substring(intStart, intEnd));
			
			editTextIntValue += toAdd;
			
			
			if(intStart == 0 && intEnd == string.length() -1){
				newString = Integer.toString(editTextIntValue) + string.substring(intEnd);
			} else if (string.substring(intStart, intEnd).length() == string.length()){
				newString = Integer.toString(editTextIntValue);
			} else {
				String partOne = string.substring(0, intStart - 1);
				String partTwo =  string.substring(intEnd + 1, string.length() - 1);
				String fuckyou = Integer.toString(editTextIntValue);
				
				newString = string.substring(0, intStart - 1) + Integer.toString(editTextIntValue) + string.substring(intEnd + 1, string.length() - 1);
			}
			
		}
		
		return newString;
	}
	
	public static void addToInput(int toAdd, EditText editText){
		String newText = editText.toString();
		
		if(newText != ""){
			newText = addToString(newText, toAdd);
			
			if(newText.length() != 0 && newText.charAt(0) != '0'){
				editText.setText(newText);
			}
		}else{
			editText.setText("1");
		}
		
	}
}
