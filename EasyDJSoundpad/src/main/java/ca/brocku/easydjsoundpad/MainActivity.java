package ca.brocku.easydjsoundpad;


import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {
    
    Button playButton;
    Button recordButton;
    Button freemodeButton;
    Button editButton;
    Button deleteButton;
    Button quitButton;
    ImageAdapter adapter;
    public static LoadedSounds sounds;
    Recording[] recordings;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        playButton = (Button) findViewById(R.id.playButton);
        recordButton = (Button) findViewById(R.id.recordButton);
        freemodeButton = (Button) findViewById(R.id.freemodeButton);
        editButton = (Button) findViewById(R.id.editButton);
        deleteButton = (Button) findViewById(R.id.deleteButton);
        quitButton = (Button) findViewById(R.id.quitButton);
        playButton.setOnClickListener(this);
        recordButton.setOnClickListener(this);
        freemodeButton.setOnClickListener(this);
        editButton.setOnClickListener(this);
        deleteButton.setOnClickListener(this);
        quitButton.setOnClickListener(this);
        sounds = new LoadedSounds(this.getBaseContext());
     //   getRecordingList();
    }
    
 
    

	/* This method handles all actions that occur when the options menu is created.
	 * 
	 * @param     menu         the menu that was created
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	} //onCreateOptionsMenu
	
	
	/* This method handles all actions that occur when a button is clicked. The type of button
	 * is broken down into operands and non operands - depending on which type of button
	 * is pressed, a different action will occur. The previous operand and operator is remembered
	 * for calculation purposes.
	 * 
	 * @param     v        the currently saved instance state
	 */
	
	public void onClick(View v) {
		int spinnerIndex = ((Spinner)findViewById(R.id.searchOptions)).getSelectedItemPosition();
		if (v == playButton) {
			Intent myIntent = new Intent(this, PlaybackActivity.class);
			myIntent.putExtra("recording", recordings[spinnerIndex]);
			startActivity(myIntent);
		} else if (v == recordButton) {
			getInput();
		} else if (v == freemodeButton) {
			Intent myIntent = new Intent(this, RecordActivity.class);
			myIntent.putExtra("recording", false);
			startActivity(myIntent);
		} else if (v == editButton) {
			Intent myIntent = new Intent(this, EditActivity.class);
			myIntent.putExtra("recording", recordings[spinnerIndex]);
			startActivity(myIntent);
		} else if (v == deleteButton) {
			deleteRecording(spinnerIndex);
		} else if (v == quitButton) {
			finish();
		}
	}
	
	 public void getInput() {
	    	final EditText input = new EditText(this);
	    	//final String name;
	    	input.setHint("Name of Record");
	    	new AlertDialog.Builder(this)
	    	.setTitle("Record Name")
	    	.setMessage("Enter Name of New Record:")
	    	.setView(input)
	    	.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	    	public void onClick(DialogInterface dialog, int whichButton) {
	    			newRecording(input.getText().toString());
	    	}
	    	})
	    	.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	    	public void onClick(DialogInterface dialog, int whichButton) {
	    	}
	    	})
	    	.show();
	    }
	 
	 public void newRecording(String name) {
		 Intent myIntent = new Intent(this, RecordActivity.class);
			myIntent.putExtra("recording", true);
			myIntent.putExtra("name", name);
			startActivity(myIntent);
	 }
	 
	 public void getRecordingList() {
		 Log.d("names","?");
		 recordings = new Recording[fileList().length];
		 String[] names = new String[recordings.length];

			for (int i=0; i< recordings.length;i++) { // for all existing files in directory, deserialize associated contact
				try
				{
					FileInputStream fileIn = openFileInput(fileList()[i]);
					ObjectInputStream in = new ObjectInputStream(fileIn);
					recordings[i] = (Recording) in.readObject();
					names[i] = recordings[i].RECORD_NAME;
					in.close();
					fileIn.close();
				}catch(Exception e) {
					Log.e("Record Deserialization Error",e.getMessage());
					return;
				}
			}
			  Spinner spinner = (Spinner) findViewById(R.id.searchOptions);
			  Log.d("names",Integer.toString(names.length));
			if (names.length > 0) {
			    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this,
			            android.R.layout.simple_spinner_dropdown_item,
			                names);
			        spinner.setAdapter(spinnerArrayAdapter);
			        spinner.setEnabled(true);
					playButton.setEnabled(true);
					deleteButton.setEnabled(true);
					editButton.setEnabled(true);
			} else {
				spinner.setEnabled(false);
				playButton.setEnabled(false);
				deleteButton.setEnabled(false);
				editButton.setEnabled(false);
			}
			
	 }
	 
		private void deleteRecording(int position) {
			deleteFile(recordings[position].RECORD_NAME + ".rec");
			getRecordingList();
		}
	 
		protected void onResume() {
			super.onResume();
			getRecordingList();
			((Spinner)findViewById(R.id.searchOptions)).setSelection(recordings.length-1,true);
		}
}
