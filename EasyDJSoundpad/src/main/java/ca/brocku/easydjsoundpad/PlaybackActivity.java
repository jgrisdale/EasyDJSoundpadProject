package ca.brocku.easydjsoundpad;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.text.DecimalFormat;

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
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

public class PlaybackActivity extends Activity {
    
	SoundPool soundpool;
    LoadedSounds loadedSounds;
    MediaPlayer mediaPlayer;
    GridView grid;
    int[] soundStreams = new int[25];//played soundIds for playback control
    ImageAdapter adapter;
    ProgressBar progressBar;
    Button backButton;
    private Recording recording;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.playback_activity);
        grid = (GridView) findViewById(R.id.playbackGrid);
        loadedSounds = MainActivity.sounds;
        soundpool = loadedSounds.getSoundPool();

        adapter = new ImageAdapter(this);
        grid.setAdapter(adapter);
        progressBar =  (ProgressBar)findViewById(R.id.time);
        
        recording = (Recording) getIntent().getSerializableExtra("recording");
        backButton = ((Button)findViewById(R.id.back));
        
        double maxSeconds = recording.get(recording.getNumPairs()-1).time;
        progressBar.setMax((int)(maxSeconds*1000));
        ((TextView)findViewById(R.id.endTime)).setText(formatSeconds(maxSeconds));
        playRecording();
        backButton.setEnabled(false);
    }
    
    public String formatSeconds(double seconds) {
    	int totalSecs = (int) seconds;
    	int hours = totalSecs / 3600;
    	int minutes = (totalSecs % 3600) / 60;
    	seconds = totalSecs % 60;
		if (++seconds == 60) {   // update the timer accordingly
			seconds = 00; 
			if (++minutes == 60) {
				minutes = 0; hours++;
			}
		}
		return String.format("%02d",hours) + ":"+ String.format("%02d",minutes) + ":" 
			+ String.format("%02d",totalSecs);
    }

public void playRecording() {
	new Thread(new Runnable() { 
        public void run() {
        	int currentIndex = 0;
        	final double startTime = ((double)SystemClock.elapsedRealtime())/1000.00;
        	while (true) {
        		if (currentIndex >= recording.getNumPairs()) {
        			break;
        		}
        		runOnUiThread (new Thread(new Runnable() { 
   		         public void run() {
   		             progressBar.setProgress((int)SystemClock.elapsedRealtime() - ((int)startTime)*1000);
   		         }
   		     }));
            	//Log.d("wtf",Double.toString(currentTime) + "..." + ((double)SystemClock.elapsedRealtime())/1000.00);
        		if (((double)SystemClock.elapsedRealtime())/1000.00 - startTime >= recording.get(currentIndex).time) {
        			if (currentIndex == recording.getNumPairs()-1) {
        				currentIndex++;
            			continue;
        			}
        			final int position = recording.get(currentIndex).soundPos;
        			final float rate = recording.get(currentIndex).rate;
        			if (position < 20) {
   		             soundpool.play(loadedSounds.getSoundId(position),1,1,0,0,rate);
   		             } else {
   		            	 playBackgroundLoop(position);
   		             }
        			//Log.d("yay",Integer.toString(currentIndex) + ".." + Integer.toString(recording.getNumPairs()));
        			runOnUiThread (new Thread(new Runnable() { 
        		         public void run() {
        		             final double initMs = ((double)SystemClock.elapsedRealtime());
        		             adapter.setImage(position, R.drawable.button1);
        		             adapter.notifyDataSetChanged();
        		             grid.setAdapter(adapter);
        		             grid.invalidateViews();
        		             new Thread(new Runnable() { 
        		   		         public void run() {
        		   		        	while (((double)SystemClock.elapsedRealtime())- initMs < 20);
        		   		        	runOnUiThread (new Thread(new Runnable() { 
        		   	   		         public void run() {
        		   	   		        	 adapter.setImage(position, R.drawable.button);
        		   	   		        	 adapter.notifyDataSetChanged();
        		   	   		        	 grid.setAdapter(adapter);
        		   	   		        	 grid.invalidateViews();  
        		   	   		         }
        		   	   		     }));
        		   		         }
        		   		     }).start();
        		             
        		             
        		         }
        		     }));
        			currentIndex++;
        		}     		
        		}
        	runOnUiThread (new Thread(new Runnable() { 
		         public void run() {
		             backButton.setEnabled(true);
		             if ((mediaPlayer != null && mediaPlayer.isPlaying())) {
		             mediaPlayer.stop();
		    	  	 mediaPlayer.release();
		             }
                 }
                public void onBackPressed(){

                }
}));
        }
    }).start();
	}

    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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
			finish();
    }

    public void onBackPressed(){
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        System.exit(0);
        //for (int i=0; i<loadedSounds.mappedSounds.size() ; i++) soundpool.stop(loadedSounds.mappedSounds.get(i));
        finish();
    }
 
  
}
