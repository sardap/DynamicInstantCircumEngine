package club.psarda.dynamicinstantcircumengine.Listners;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;

import java.util.Random;

import club.psarda.dynamicinstantcircumengine.R;

/**
 * Created by pfsar on 15/10/2017.
 */

public class DiceShakeListener implements Animation.AnimationListener{
	
	private static Random random = new Random();

	protected static final int START_ANIMATION_DURATION = 100;
	protected static final int NUMBER_OF_REPEATS = 5;
	
	protected ImageButton _imageButton;
	protected int _roll;
	protected int _repeats;
	protected long _nextDur;
	protected Context _context;
	
	protected DiceShakeListener(ImageButton imageButton, int roll, int repeats, long curDuration, Context context){
		_imageButton = imageButton;
		_roll = roll;
		_repeats = repeats;
		_nextDur = curDuration;
		_context = context;
	}
	
	public DiceShakeListener(ImageButton imageButton, int roll, Context context){
		this(imageButton, roll, 0, START_ANIMATION_DURATION, context);
	}
	
	protected int get_repeats(){
		return _repeats;
	}
	
	@Override
	public void onAnimationStart(Animation animation) {
		_imageButton.setImageLevel(random.nextInt(6));
	}
	
	@Override
	public void onAnimationEnd(Animation animation) {
		if(_repeats > NUMBER_OF_REPEATS){
			_imageButton.setImageLevel(_roll - 1);
			_imageButton.setAnimation(null);
		}else{
			_repeats++;
			Animation shake = AnimationUtils.loadAnimation(_context, R.anim.dice_roll);
			shake.setAnimationListener(CreateNewInstance());
			shake.setDuration(_nextDur);
			_imageButton.setAnimation(shake);
			shake.start();
		}
	}
	
	@Override
	public void onAnimationRepeat(Animation animation) {
	
	}
	
	protected DiceShakeListener CreateNewInstance(){
		return new DiceShakeListener(_imageButton, _roll, _repeats, (long)(_nextDur * 1.3f), _context);
	}
}