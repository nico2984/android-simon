package com.simon;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.ImageView;

public class SimonEngine {

	private static final String TAG = SimonEngine.class.getSimpleName();
	private MainActivity mainActivity;
	private List<Integer> startImages;
	private ImageView simonImage;
	private Integer loopCountStart;
	private Integer loopCountSequence;
	private Integer loopGreatWrong;
	private Integer loopShowColorTouched;
	private Integer loopFunLights;
	private Integer colorTouched;
	private Bitmap bitmapSimon;
	private Drawable drawableSimon;
	private SimonUtils simonUtils;
	private Boolean touchable;
	private int position;
	private int greatWrongImage;
	private int funLightsCounter;


	public SimonEngine(MainActivity mainActivity, ImageView simonImage) {
		this.mainActivity = mainActivity;
		this.simonImage = simonImage;
		loadStartImages();
		SimonUtils.loadSimonImages();
		setTouchable(true);
		simonUtils = new SimonUtils();
		simonUtils.setLevel(1);
		SimonUtils.sequence.clear();
	}


	public void start() {
		Log.i(TAG, "start sequence");
		setTouchable(false);
		setLoopCountStart(0);
		//SimonUtils.sequence.clear();
		new Timer().schedule(new timerTask(), 500, 500);
	}

	private void initSequence() {
		Log.i(TAG, "init sequence");
		setLoopCountSequence(0);
		setPosition(0);
		simonUtils.setShowImageOnScreen(true);
		new Timer().schedule(new timerSequence(), 1000, 500);
	}
	
	public void showColorTouched(int colorNumber) {
		Log.i(TAG, "show color touched");
		setLoopShowColorTouched(0);
		simonUtils.setShowImageOnScreen(true);
		setColorTouched(colorNumber);
		new Timer().schedule(new timerColorTouched(), 0, 250);
	}
	
	public TimerTask showFunLights() {
		Log.i(TAG, "show fun lights");
		setLoopFunLights(0);
		simonUtils.setShowImageOnScreen(true);
		funLightsCounter = 0;
		TimerFunLights tff = new TimerFunLights();
		new Timer().schedule(tff, 100, 250);
		return tff;
	}	

	private void loadStartImages() {
		startImages = new ArrayList<Integer>();
		startImages.add(0, R.drawable.simonthree);
		startImages.add(1, R.drawable.simontwo);
		startImages.add(2, R.drawable.simonone);
		startImages.add(3, R.drawable.simongo);
	}

	private int generateRandomNumber() {
		// generate random number between 0 and 3
		int number = (int) Math.round(Math.random() * 3);
		SimonUtils.logColor(TAG, number);
		return number;
	}
	
	public boolean matchPattern(String colorTouched, int position) {
		if (SimonUtils.matchPattern(colorTouched, position)){
			Log.i(TAG, "correct!");
			if(position == SimonUtils.sequence.size() - 1){
				Log.i(TAG, "Great!");
				setTouchable(false);
				setLoopGreatWrong(0);
				new Timer().schedule(new timerGreat(), 500, 2000);
				//set position to 0
				return false;
			}
			return true;
		}else{
			Log.i(TAG, "Wrong!");
			setTouchable(false);
			setLoopGreatWrong(0);
			//clear sequence
			SimonUtils.sequence.clear();
			new Timer().schedule(new timerWrong(), 500, 2000);
			return false;
		}
	}


	public void setImage(Integer image){
		bitmapSimon = BitmapFactory.decodeResource(
				mainActivity.getResources(), image);
		drawableSimon = new BitmapDrawable(mainActivity.getResources(),
				bitmapSimon);
		// to force refresh view
		simonImage.invalidate();
		simonImage.setImageDrawable(drawableSimon);
	}
	
	class timerTask extends TimerTask {
		@Override
		public void run() {
			if (getLoopCountStart() > 3) {
				Log.i(TAG, "cancel start");
				cancel();
				initSequence();
			} else {
				mainActivity.runOnUiThread(new Runnable() {
					@Override
					public synchronized void run() {
						try {
							if (simonUtils.getShowImageOnScreen()) {
								setImage(startImages.get(getLoopCountStart()));
								setLoopCountStart(getLoopCountStart() + 1);
								simonUtils.setShowImageOnScreen(false);
							} else {
								// turn simon's lights off
								setImage(SimonUtils.simonImages.get(7));
								simonUtils.setShowImageOnScreen(true);
							}
						} catch (Exception e) {
							Log.i(TAG, e.toString());
						}
					}
				});
			}
		}
	}
	
	class timerSequence extends TimerTask{
		@Override
		public synchronized void run() {
			// level * 2 --> each level has two runs
			// (turn light on and turn light off)
			if (getLoopCountSequence() >= simonUtils.getLevel() * 2) {
				Log.i(TAG, "cancel initSequence");
				Log.i(TAG, "Sequence: " + SimonUtils.sequence);
				this.cancel();
				setTouchable(true);
			} else {
				mainActivity.runOnUiThread(new Runnable() {
					@Override
					public synchronized void run() {
						if (simonUtils.getShowImageOnScreen()) {
							// simon's lights on
							
							if(SimonUtils.sequence.size() > 0 && 
									SimonUtils.sequence.size() > getPosition() && 
									SimonUtils.sequence.get(getPosition()) != null){
								String color = SimonUtils.sequence.get(getPosition());
								setImage(SimonUtils.simonImages.get(SimonUtils.getColorNumber(color)));
								SimonUtils.playSound(SimonUtils.getColorNumber(color));
							}else{
								int ramdonNumber = generateRandomNumber();
								setImage(SimonUtils.simonImages.get(ramdonNumber));
								//fill color's list
								SimonUtils.sequence.add(getPosition(),SimonUtils.getColor(ramdonNumber));
								SimonUtils.playSound(ramdonNumber);
							}
							setPosition(getPosition() + 1);
							simonUtils.setShowImageOnScreen(false);
						} else {
							// simon's lights off
							setImage(SimonUtils.simonImages.get(7));
							simonUtils.setShowImageOnScreen(true);
						}
					}
				});
				setLoopCountSequence(getLoopCountSequence() + 1);
			}
		}
	}
	
	class timerWrong extends TimerTask{
		@Override
		public synchronized void run() {
			if (getLoopGreatWrong() > 1) {
				this.cancel();
				setTouchable(true);
			} else {
				mainActivity.runOnUiThread(new Runnable() {
					@Override
					public synchronized void run() {
						if (simonUtils.getShowImageOnScreen()) {
							//play wrong sound
							SimonUtils.playSound(4);
							//show wrong image
							setImage(SimonUtils.simonImages.get(5));
							simonUtils.setShowImageOnScreen(false);
						} else {
							// show start image
							setImage(SimonUtils.simonImages.get(6));
							setPosition(0);
							//set level to 0
							simonUtils.setLevel(1);
							//set start only
							mainActivity.setOnlyStart(true);
							simonUtils.setShowImageOnScreen(true);
						}
					}
				});
				setLoopGreatWrong(getLoopGreatWrong() + 1);
			}
		}
	}
	
	class timerGreat extends TimerTask{
		@Override
		public synchronized void run() {
			if (getLoopGreatWrong() > 1) {
				this.cancel();
			} else {
				mainActivity.runOnUiThread(new Runnable() {
					@Override
					public synchronized void run() {
						if (simonUtils.getShowImageOnScreen()) {
							//play great sound
							SimonUtils.playSound(5);
							//show great image
							setImage(SimonUtils.simonImages.get(4));
							simonUtils.setShowImageOnScreen(false);
						} else {
							simonUtils.setShowImageOnScreen(true);
							//show blank image
							setImage(SimonUtils.simonImages.get(7));
							simonUtils.setLevel(simonUtils.getLevel() + 1);
							//call sequence
							initSequence();
						}
					}
				});
				setLoopGreatWrong(getLoopGreatWrong() + 1);
			}
		}
	}
	
	class timerColorTouched extends TimerTask{
		@Override
		public synchronized void run() {
			if (getLoopShowColorTouched() > 1) {
				this.cancel();
			} else {
				mainActivity.runOnUiThread(new Runnable() {
					@Override
					public synchronized void run() {
						if (simonUtils.getShowImageOnScreen()) {
							//show color image
							setImage(SimonUtils.simonImages.get(getColorTouched()));
							simonUtils.setShowImageOnScreen(false);
						} else {
							simonUtils.setShowImageOnScreen(true);
							//show blank image
							setImage(SimonUtils.simonImages.get(7));
						}
					}
				});
				setLoopShowColorTouched(getLoopShowColorTouched() + 1);
			}
		}
	}
	
	class TimerFunLights extends TimerTask{
		@Override
		public synchronized void run() {
			if (getLoopFunLights() > (simonUtils.getCircleLights().size() - 1)*2 ) {
				this.cancel();
				showFunLights();
			} else {
				mainActivity.runOnUiThread(new Runnable() {
					@Override
					public synchronized void run() {
						if (simonUtils.getShowImageOnScreen()) {
							//show color image
							List<String> funLightsColorList= simonUtils.getCircleLights();
							Log.i(TAG, "show: " + funLightsColorList.get(funLightsCounter));
							setImage(SimonUtils.simonImages.get(simonUtils.
											getColorNumber(funLightsColorList.
											get(funLightsCounter))));
							simonUtils.setShowImageOnScreen(false);
							funLightsCounter++;
						} else {
							simonUtils.setShowImageOnScreen(true);
							//show blank image
							setImage(SimonUtils.simonImages.get(7));
						}
					}
				});
				setLoopFunLights(getLoopFunLights() + 1);
			}
		}
	}
	
	public Boolean getTouchable() {
		return touchable;
	}

	public void setTouchable(Boolean touchable) {
		this.touchable = touchable;
	}

	public int getPosition() {
		return position;
	}

	public void setPosition(int position) {
		this.position = position;
	}

	public Integer getLoopGreatWrong() {
		return loopGreatWrong;
	}

	public void setLoopGreatWrong(Integer loopGreatWrong) {
		this.loopGreatWrong = loopGreatWrong;
	}

	public int getGreatWrongImage() {
		return greatWrongImage;
	}

	public void setGreatWrongImage(int greatWrongImage) {
		this.greatWrongImage = greatWrongImage;
	}
	
	public int getLoopCountStart() {
		return loopCountStart;
	}

	public void setLoopCountStart(int loopCount) {
		this.loopCountStart = loopCount;
	}

	public Integer getLoopCountSequence() {
		return loopCountSequence;
	}

	public void setLoopCountSequence(Integer loopCountSequence) {
		this.loopCountSequence = loopCountSequence;
	}


	public Integer getLoopShowColorTouched() {
		return loopShowColorTouched;
	}


	public void setLoopShowColorTouched(Integer loopShowColorTouched) {
		this.loopShowColorTouched = loopShowColorTouched;
	}


	public Integer getColorTouched() {
		return colorTouched;
	}


	public void setColorTouched(Integer colorTouched) {
		this.colorTouched = colorTouched;
	}


	public Integer getLoopFunLights() {
		return loopFunLights;
	}


	public void setLoopFunLights(Integer loopFunLights) {
		this.loopFunLights = loopFunLights;
	}
}

// private Runnable mUpdateTimeTask = new Runnable() {
// public void run() {
// Bitmap bitmap = BitmapFactory.decodeResource(mainActivity.getResources(),
// startImages.get(0));
// Drawable d = new BitmapDrawable(mainActivity.getResources(),
// bitmap);
// simonImage.setImageDrawable(d);
// Log.i(TAG, "Run");
// mHandler.removeCallbacks(mUpdateTimeTask);
// mHandler.postAtTime(mUpdateTimeTask, 5000);
// }
// };