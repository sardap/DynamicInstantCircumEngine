package club.psarda.dynamicinstantcircumengine.Filters;

import android.content.Context;
import android.text.InputFilter;
import android.text.Spanned;
import android.widget.Toast;

import club.psarda.dynamicinstantcircumengine.R;

/**
 * Created by pfsar on 8/10/2017.
 */

public class NumberFitler implements InputFilter {
	
	private Context _context;
	
	public NumberFitler(Context context){
		_context = context;
	}
	
	public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
		
		String result = "";
		String errorMessage = "";
		
		for (int i = start; i < end && errorMessage.length() == 0; i++) {
			
			if(!Character.isDigit(source.charAt(i))){
				errorMessage = _context.getString(R.string.error_must_be_number_field) + "\n";
				break;
			}
			
			result += source.charAt(i);
		}
		
		if(errorMessage.length() == 0 && result != "" && dest.toString().length() == 0 && Integer.parseInt(result) < 1){
			result = "";
			errorMessage += _context.getString(R.string.error_must_be_greater_than_x, "1") + "\n";
		}
		
		if(errorMessage.length() != 0){
			Toast.makeText(_context, errorMessage.substring(0, errorMessage.length() - 1), Toast.LENGTH_SHORT).show();
		}
		
		return result;
	}
}
