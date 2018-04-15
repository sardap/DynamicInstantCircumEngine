package club.psarda.dynamicinstantcircumengine.database.DataHolders;

import club.psarda.dynamicinstantcircumengine.Utils.UtilsClass;

/**
 * Created by pfsar on 6/09/2017.
 */

public class TargetData implements java.io.Serializable {

	private int _target;
	private int _reroll;
	private TargetEnum _targetEnum;
	
	public TargetData(){
	
	}

	public TargetData(int target, TargetEnum targetEnum, int reroll){
		_target = target;
		_targetEnum = targetEnum;
		_reroll = reroll;
	}
	
	public TargetData(String string){
		
		if(string.contains(",")){
			String[] col = string.split(",");
			
			TargetEnum targetEnum;
			
			switch (col[0].charAt(UtilsClass.getPostionOfNonNumber(col[0]))){
				case '+':
					targetEnum = TargetEnum.ABOVE;
					break;
				
				case '-':
					targetEnum = TargetEnum.BELOW;
					break;
				
				default:
					targetEnum = TargetEnum.EQUAL;
					break;
			}
			
			_target = Integer.parseInt(UtilsClass.removeAllNonDigits(col[0]));
			_targetEnum = targetEnum;
			_reroll = Integer.parseInt(col[1]);
		} else {
			_target = Integer.parseInt(UtilsClass.removeAllNonDigits(string));
			_targetEnum = UtilsClass.fromChar(string.charAt(string.length() - 1));
		}
		
		
	}
	
	public String GetString(){
		return get_target() + UtilsClass.toString(get_targetEnum());
	}

	public int get_target(){
		return _target;
	}
	
	public int get_reroll(){
		return _reroll;
	}
	
	public TargetEnum get_targetEnum(){
		return _targetEnum;
	}
}
