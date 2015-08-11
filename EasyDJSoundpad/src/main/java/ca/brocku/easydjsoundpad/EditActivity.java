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
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

public class EditActivity extends Activity {
    
	
    Button backButton;
    private Recording recording;
    SeekBar timeBar;
    SeekBar rateBar;
    Spinner soundValues;
    final int NUM_SOUNDS = 20;
    final int NUM_LOOPS = 5;
    PlaybackPair currentEdit;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.edit_activity);  
        recording = (Recording) getIntent().getSerializableExtra("recording");
        showEditPanel(false);
        ((ListView)findViewById(R.id.recordValuesList)).setOnItemClickListener(new OnItemClickListener()
		{
			@Override public void onItemClick(AdapterView<?> arg0, View arg1,int position, long arg3)
			{ 
				PlaybackPair selected = recording.get(position);
				if (currentEdit != null) {
					updatePair(currentEdit);
					fillValuesList();
				}
				showEditPanel(true);
				timeBar.setProgress((int)(selected.time*1000));
				rateBar.setProgress((int)(selected.rate*100-50));
				soundValues.setSelection(selected.soundPos,true);
				currentEdit = selected;
			}
		});
		registerForContextMenu(((ListView)findViewById(R.id.recordValuesList)));
		rateBar = ((SeekBar)findViewById(R.id.rateBar));
		timeBar = ((SeekBar)findViewById(R.id.timeBar));
		timeBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
	        @Override
	        public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
	        	((TextView)findViewById(R.id.currentTime)).setText(formatSeconds(arg1/1000.00));  
	        }

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				
			}
	    });
		rateBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener(){
	        @Override
	        public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
	        	((TextView)findViewById(R.id.rateVal)).setText("(" + ((float)arg1+50.00)/100.00 + ")");  
	        }

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				
			}
	    });
		soundValues = ((Spinner)findViewById(R.id.soundSpinner));
		((EditText)findViewById(R.id.recordName)).setText(recording.RECORD_NAME);
		fillValuesList();
        double maxSeconds = recording.get(recording.getNumPairs()-1).time;
        timeBar.setMax((int)(maxSeconds*1000));
        ((TextView)findViewById(R.id.endTime)).setText(formatSeconds(maxSeconds));
        initSpinnerValues();
       // backButton.setEnabled(false);
    }
    
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
    	((TextView)findViewById(R.id.currentTime)).setText(formatSeconds(progress/1000.00));        
    }
    
    private void updatePair(PlaybackPair pair) {
    	pair.rate = (float)((((float)rateBar.getProgress())+50.00)/100.00);
    	pair.soundPos = soundValues.getSelectedItemPosition();
    	pair.time = timeBar.getProgress()/1000.00;
    	Log.d("init",Double.toString(recording.get(0).time));
    	recording.updatePairOrder();
    	Log.d("init",Double.toString(recording.get(0).time));
    	
    }
    
    public void initSpinnerValues() {
    	String valuesList[] = new String[NUM_SOUNDS+NUM_LOOPS];
    	for (int i=0;i<NUM_SOUNDS;i++) {
    		valuesList[i] = "Sound " + i;
    	}
    	for (int i=0;i<NUM_LOOPS;i++) {
    		valuesList[i+20] = "Loop " + i;
    	}
    	 ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this,
		            android.R.layout.simple_spinner_dropdown_item,
		                valuesList);
		        soundValues.setAdapter(spinnerArrayAdapter);
    }
    
    public void fillValuesList() {
    	String[] names = new String[recording.getNumPairs()-1];
    	for (int i=0;i<recording.getNumPairs()-1;i++) {
    		PlaybackPair current = recording.get(i);
    		String prefix = "Sound " + current.soundPos;
    		if (current.soundPos > 19) {
    			prefix = "Loop " + (current.soundPos-20);
    		}
    		names[i] = prefix + " @ " + formatSeconds(current.time);
    	}
    	ArrayAdapter<String> adapter;
		adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,names);
		((ListView)findViewById(R.id.recordValuesList)).setAdapter(adapter);
    }

	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.context_menu, menu);
	}

	/* This functions handles the context menu for each contact. An option to view/edit or delete a contact
	 * is present.
	 */
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo adapInfo = (AdapterContextMenuInfo) item
				.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.copy: // if cancel is selected do nothing
			recording.copy(((AdapterContextMenuInfo)item.getMenuInfo()).position);
			fillValuesList();
			return true;
		case R.id.cancel: // if cancel is selected do nothing
			return true;
		case R.id.delete: // if delete is selected
			recording.delete(((AdapterContextMenuInfo)item.getMenuInfo()).position);
			fillValuesList();
			return true;
		}
		return false;
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

	/*int startTime = (int) (SystemClock.elapsedRealtime()/1000);
	while (true) {
		Log.d("k",Integer.toString((int) (SystemClock.elapsedRealtime()/1000) - startTime));
		if ((int)(SystemClock.elapsedRealtime()/1000) - startTime > 25) {
			break;
		}
	}*/
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    public void showEditPanel(boolean show) {
    	int visibility = show ? View.VISIBLE : View.INVISIBLE;
    	findViewById(R.id.timeBar).setVisibility(visibility);
    	findViewById(R.id.startTime).setVisibility(visibility);
    	findViewById(R.id.currentTime).setVisibility(visibility);
    	findViewById(R.id.endTime).setVisibility(visibility);
    	findViewById(R.id.rateBar).setVisibility(visibility);
    	findViewById(R.id.rateLbl).setVisibility(visibility);
    	findViewById(R.id.rateVal).setVisibility(visibility);
    	findViewById(R.id.soundLbl).setVisibility(visibility);
    	findViewById(R.id.soundSpinner).setVisibility(visibility);
    }

    public void doEndAction(View v) {
			finish();
    }
    
    public void save(View v) {
    	String newName = ((EditText)findViewById(R.id.recordName)).getText().toString();
    	deleteFile(recording.RECORD_NAME + ".rec");
    	if (currentEdit != null) {
    	updatePair(currentEdit);
    	}
    	recording.publish(newName);
		try {
			// Open the file
			FileOutputStream outputFile = openFileOutput(newName + ".rec", Context.MODE_PRIVATE);
			// Write recording out to file
			ObjectOutputStream out = new ObjectOutputStream(outputFile);
			out.writeObject(recording);
			out.close();
			outputFile.close();
		} catch (Exception e) {
			Log.d("Save FAILED", e.toString());
		}
		finish();
    }
 
}
