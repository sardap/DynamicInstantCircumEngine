package club.psarda.dynamicinstantcircumengine.Adapters.ListNumberInput;

/**
 * Created by pfsar on 5/09/2017.
 */

public class TargetAdapterData {
	
	private String _title;
	private String _subTitle;
	private String _inputtedText;
	private boolean _checked;
	private ListInputAdapterNew.EntireType _type;
	
	public TargetAdapterData(String title, String subTitle, String input, ListInputAdapterNew.EntireType type){
		_title = title;
		_subTitle = subTitle;
		_inputtedText = input;
		_type = type;
	}
	
	public String get_title(){
		return _title;
	}

	public String get_subTitle(){
		return _subTitle;
	}

	public String get_inputtedText(){
		return _inputtedText;
	}
	
	public void set_inputtedText(String imputedText){
		_inputtedText = imputedText;
	}

	public boolean get_checked(){
		return _checked;
	}
	
	public void set_checked(boolean newValue){
		_checked = newValue;
	}
	
	public ListInputAdapterNew.EntireType get_type(){
		return _type;
	}
	
}
