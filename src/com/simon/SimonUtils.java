package com.simon;

import java.util.ArrayList;
import java.util.List;

import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.MotionEvent;

public class SimonUtils {
	private static final String TAG = SimonUtils.class.getSimpleName();
	private Integer level;
	private Boolean showImageOnScreen;
	public static List<Integer> simonImages;
	private static SoundPool soundPool;
	// this is a HashMap<Integer, Integer>
	private static SparseIntArray soundPoolMap;
	public static final String RED = "RED";
	public static final String BLUE = "BLUE";
	public static final String YELLOW = "YELLOW";
	public static final String GREEN = "GREEN";
	public static final List<String> sequence = new ArrayList<String>();
	private List<String> circleLights;
	
	
	public SimonUtils(){
		level = 1;
		showImageOnScreen = true;
		loadFunLightsSequence();
	}
	
	public static void playSound(int sound) {
		soundPool.play(soundPoolMap.get(sound), AudioManager.STREAM_MUSIC,
				AudioManager.STREAM_MUSIC, 1, 0, 1f);
	}
	
	public static void initSound(MainActivity mainActivity) {
		soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 100);
		soundPoolMap = new SparseIntArray();

		soundPoolMap.put(0,
				soundPool.load(mainActivity.getBaseContext(), R.raw.simonsound1, 1));
		soundPoolMap.put(1,
				soundPool.load(mainActivity.getBaseContext(), R.raw.simonsound2, 1));
		soundPoolMap.put(2,
				soundPool.load(mainActivity.getBaseContext(), R.raw.simonsound3, 1));
		soundPoolMap.put(3,
				soundPool.load(mainActivity.getBaseContext(), R.raw.simonsound4, 1));
		soundPoolMap.put(4,
				soundPool.load(mainActivity.getBaseContext(), R.raw.wrong, 1));
		soundPoolMap.put(5,
				soundPool.load(mainActivity.getBaseContext(), R.raw.correct, 1));
	}
	
	public static void loadSimonImages() {
		SimonUtils.simonImages = new ArrayList<Integer>();
		SimonUtils.simonImages.add(0, R.drawable.simonred2);
		SimonUtils.simonImages.add(1, R.drawable.simonblue2);
		SimonUtils.simonImages.add(2, R.drawable.simongreen2);
		SimonUtils.simonImages.add(3, R.drawable.simonyellow2);
		SimonUtils.simonImages.add(4, R.drawable.simon_great);
		SimonUtils.simonImages.add(5, R.drawable.simon_wrong);
		SimonUtils.simonImages.add(6, R.drawable.simonstart);
		SimonUtils.simonImages.add(7, R.drawable.simon_blank);
	}
	
	private void loadFunLightsSequence(){
		circleLights = new ArrayList<String>();
		circleLights.add(GREEN);
		circleLights.add(RED);
		circleLights.add(YELLOW);
		circleLights.add(BLUE);
		
		circleLights.add(YELLOW);
		circleLights.add(RED);
		circleLights.add(GREEN);
	}
	
	public static boolean matchPattern(String color, int position){
		try{
			if(sequence.get(position).equals(color)){
				return true;
			}else{
				return false;
			}
		}catch (Exception e) {
			Log.d(TAG, e.toString());
			return false;
		}
	}
	
	public static void logColor(String TAG, int number) {
		
		Log.i(TAG, "Random: " + getColor(number));
	}
	
	public static String getColor(int number){
		String color;
		if (number == 0) {
			color = RED;
		} else if (number == 1) {
			color = BLUE;
		} else if (number == 2) {
			color = GREEN;
		} else {
			color = YELLOW;
		}
		return color;
	}
	
	public static int getColorNumber(String color){
		int n = 0;
		if (color.equals(RED)) {
			n = 0;
		} else if (color.equals(BLUE)) {
			n = 1;
		} else if (color.equals(GREEN)) {
			n = 2;
		} else if (color.equals(YELLOW)) {
			n = 3;
		}
		return n;
	}
	
	
	/**
	 * RED = 265 < X < 400, 290 < Y < 435
	 * YELLOW = 265 < X < 400, 480 < Y < 580
	 * GREEN = 75 < X < 205, 290 < Y < 435
	 * BLUE = 75 < X < 205, 480 < Y < 580
	 * @param event
	 * @return
	 */
	public static String colorTouch(MotionEvent event) {
		String color = null;
	//Evaluate x first
		if (event.getX() > 265 && event.getX() < 400) {
			//RED or YELLOW
			if(event.getY() > 290 && event.getY() < 435){
				SimonUtils.logColor(TAG, SimonUtils.RED);
				color = SimonUtils.RED;
			}else if (event.getY() > 480 && event.getY() < 580){
				SimonUtils.logColor(TAG, SimonUtils.YELLOW);
				color = SimonUtils.YELLOW;
			}
		}else if(event.getX() > 75 && event.getX() < 205){
			//GREEN or BLUE
			if(event.getY() > 290 && event.getY() < 435){
				SimonUtils.logColor(TAG, SimonUtils.GREEN);
				color = SimonUtils.GREEN;
			}else if (event.getY() > 480 && event.getY() < 580){
				SimonUtils.logColor(TAG, SimonUtils.BLUE);
				color = SimonUtils.BLUE;
			}
		}
		return color;
	}
	
	
	public static void logColor(String TAG, String color) {
		Log.i(TAG, "Random: " + color);
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public String getRED() {
		return RED;
	}

	public String getBLUE() {
		return BLUE;
	}

	public String getYELLOW() {
		return YELLOW;
	}

	public String getGREEN() {
		return GREEN;
	}

	public Boolean getShowImageOnScreen() {
		return showImageOnScreen;
	}

	public void setShowImageOnScreen(Boolean showImageOnScreen) {
		this.showImageOnScreen = showImageOnScreen;
	}

	public List<String> getCircleLights() {
		return circleLights;
	}
}
