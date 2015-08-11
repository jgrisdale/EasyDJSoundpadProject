package ca.brocku.easydjsoundpad;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.SystemClock;
import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.SeekBar;

public class RecordActivity extends Activity implements View.OnTouchListener {
    
	SoundPool soundpool;
    LoadedSounds loadedSounds;
    MediaPlayer mediaPlayer;
    GridView grid;
    int[] soundStreams = new int[25];//played soundIds for playback control
    ImageAdapter adapter;
    SeekBar rateBar;

    boolean isRecording;
    private Recording newRecording;
    double startTime;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.record_activity);
        grid = (GridView) findViewById(R.id.grid);
        loadedSounds = MainActivity.sounds;
        soundpool = loadedSounds.getSoundPool();

        adapter = new ImageAdapter(this);
        grid.setAdapter(adapter);
        grid.setOnTouchListener(this);
        rateBar =  (SeekBar)findViewById(R.id.rate);
        
        isRecording = getIntent().getBooleanExtra("recording", false);
        Log.d("k",Boolean.toString(isRecording));
        if (isRecording) {
        	newRecording = new Recording();
        	startTime = ((double)SystemClock.elapsedRealtime())/1000.0;
        } else {
        	((Button)findViewById(R.id.endButton)).setText("Back to Menu");
        }
        
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int action = event.getActionMasked();
        int index=event.getActionIndex();
        float currentXPosition = event.getX(index);
        float currentYPosition = event.getY(index);
        int position = grid.pointToPosition((int) currentXPosition, (int) currentYPosition);
        if (position == -1) {return true;}
        float rate = (float)(rateBar.getProgress() + 50)/(float)100;
        //if a touch is made play the chosen sound based on grid location
        if (action == MotionEvent.ACTION_DOWN || action == MotionEvent.ACTION_POINTER_DOWN) {
        	 if (isRecording) {
    			 newRecording.add(((double)SystemClock.elapsedRealtime())/1000.00 - startTime, position, rate);
    			 Log.d("pair","time:" + (((double)SystemClock.elapsedRealtime())/1000.00 - startTime) + "...position:" + position
    					 + "...rate:" + rate);
    		 }
        	 
        	if (position>=20) {
        		playBackgroundLoop(position);
        	} else {
            soundStreams[position]= soundpool.play(loadedSounds.getSoundId(position),1,1,0,0,rate);
        	}
            adapter.setImage(position, R.drawable.button1);
            adapter.notifyDataSetChanged();
            grid.setAdapter(adapter);
            grid.invalidateViews();
        }
        //when finger removed update button image
        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_POINTER_UP){
            //soundpool.stop(soundid[position]);
            adapter.setImage(position, R.drawable.button);
            adapter.notifyDataSetChanged();
            grid.setAdapter(adapter);
            grid.invalidateViews();
        }
        return false;
    }
    
    public void playBackgroundLoop(int position) {
    	if (mediaPlayer != null && mediaPlayer.isPlaying()) {
    		mediaPlayer.stop();
    	}
    	switch (position) {
    	case 20: mediaPlayer = MediaPlayer.create(this, R.raw.loop1); break;
    	case 21: mediaPlayer = MediaPlayer.create(this, R.raw.loop2); break;
    	case 22: mediaPlayer = MediaPlayer.create(this, R.raw.loop3); break;
    	case 23: mediaPlayer = MediaPlayer.create(this, R.raw.loop4); break;
    	case 24: mediaPlayer = MediaPlayer.create(this, R.raw.loop5); break;
    	}
		mediaPlayer.setLooping(true);
		mediaPlayer.start();
    }
    
    public void doEndAction(View v) {
    	  if (isRecording) {
    		  String name = getIntent().getStringExtra("name");
    		  //add sentinel value to signal end
    		  newRecording.add(((double)SystemClock.elapsedRealtime())/1000.00 - startTime, -1, -1);
    		  newRecording.publish(name);
    			try {
    				// Open the file
    				FileOutputStream outputFile = openFileOutput(name + ".rec", Context.MODE_PRIVATE);
    				// Write contact out to file
    				ObjectOutputStream out = new ObjectOutputStream(outputFile);
    				out.writeObject(newRecording);
    				out.close();
    				outputFile.close();
    			} catch (Exception e) {
    				Log.d("Save FAILED", e.toString());
    			}
    		}
    	  		if (mediaPlayer != null && mediaPlayer.isPlaying()) {
	             mediaPlayer.stop();
	    	  	 mediaPlayer.release();
	             }
			finish();
    }

    public void onBackPressed(){
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        for (int i=0; i<loadedSounds.mappedSounds.size() ; i++) soundpool.stop(loadedSounds.mappedSounds.get(i));
        finish();
    }

}
