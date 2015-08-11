package ca.brocku.easydjsoundpad;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Timer;
import android.util.Pair;

public class Recording implements Serializable {

	private static final long serialVersionUID = 4728190409042693540L;
	private ArrayList<PlaybackPair> soundValues;
	String RECORD_NAME;
	
	public Recording() {
		soundValues = new ArrayList<PlaybackPair>();
	}

	public void add(double time, int soundPos, float rate) {
		soundValues.add(new PlaybackPair(time,soundPos,rate));
	}
	
	public void delete(int index) {
		soundValues.remove(index);
	}
	
	public void copy(int index) {
		soundValues.add(new PlaybackPair(soundValues.get(index)));
		updatePairOrder();
	}
	
	public PlaybackPair get(int index) {
		return soundValues.get(index);
	}
	
	public void publish(String recordName) {
		this.RECORD_NAME = recordName;
	}
	
	public void updatePairOrder() {
		Collections.sort(soundValues,new Comparator<PlaybackPair>()  {
		    @Override
		    public int compare(PlaybackPair a, PlaybackPair b) {
		        if (a.time < b.time) {
		        	return -1;
		        } else {
		        	return 1;
		        }
		    }
		});
	}
	
	
	
	public int getNumPairs() {
		return soundValues.size();
	}
}
