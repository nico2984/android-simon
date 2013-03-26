package com.simon;

import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;

public class MainActivity extends Activity {

	private static final String TAG = MainActivity.class.getSimpleName();
	private ImageView simonImage;
	private SimonEngine simonEngine;
	private String colorTouched;
	private int position;
	private Boolean onlyStart;
	private CheckBox music;
	private MediaPlayer player;
	//private TimerTask funLightsTimer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		simonImage = (ImageView) findViewById(R.id.simonImage);
		music = (CheckBox) findViewById(R.id.music);
		simonEngine = new SimonEngine(this, simonImage);
		setOnlyStart(true);
		SimonUtils.initSound(this);
		playMusicBackground();
		
		//funLightsTimer = simonEngine.showFunLights();
		
		music.setChecked(true);
//		Log.i(TAG, "Max Memory: " + Runtime.getRuntime().maxMemory());
		music.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(music.isChecked()){
					playMusicBackground();
				}else{
					stopMusicBackground();
				}
			}
		});
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.i(TAG, "Coords: x=" + event.getX() + ",y=" + event.getY());
		
		if (simonEngine.getTouchable() && onlyStart) {
			if (event.getX() > 200 && event.getX() < 290 && event.getY() > 390
					&& event.getY() < 480) {
				Log.i(TAG, "Calling Start");
				setOnlyStart(false);
				//cancel Fun Lights before call start
				//funLightsTimer.cancel();
				simonEngine.start();
			}
		}
		
		if (simonEngine.getTouchable() && !onlyStart) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
					colorTouched = SimonUtils.colorTouch(event);
					if (colorTouched != null){
							//show color on screen
							simonEngine.setImage(SimonUtils.simonImages.get(
									SimonUtils.getColorNumber(colorTouched)));
							//play sound
							SimonUtils.playSound(SimonUtils.getColorNumber(colorTouched));
						}
			}
			
			if (event.getAction() == MotionEvent.ACTION_UP) {
				colorTouched = SimonUtils.colorTouch(event);
				if (colorTouched != null){
					if(simonEngine.matchPattern(colorTouched, position)){
						position++;
					}else{
						position = 0;
					}
				}
			//show color touched for 200ms and then show simon blank
				simonEngine.showColorTouched(SimonUtils.getColorNumber(colorTouched));
			}
		}
		return super.onTouchEvent(event);
	}
	
	private void playMusicBackground(){
		player = MediaPlayer.create(MainActivity.this, R.raw.simon_music);
    player.setLooping(true); // Set looping 
    player.setVolume(80,80); 
    player.start(); 
	}
	
	private void stopMusicBackground(){
		player.stop();
	}

	public Boolean getOnlyStart() {
		return onlyStart;
	}

	public void setOnlyStart(Boolean onlyStart) {
		this.onlyStart = onlyStart;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		if(item.getTitle().equals("About")){
			Intent intent = new Intent(this, AboutActivity.class);
			startActivity(intent);
		}
		return super.onMenuItemSelected(featureId, item);
	}
}
