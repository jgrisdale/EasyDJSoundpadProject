package ca.brocku.easydjsoundpad;
import java.io.Serializable;
import java.util.HashMap;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

public class LoadedSounds {

	private SoundPool soundpool;
	HashMap<Integer,Integer> mappedSounds;
	Context context;
	
	public LoadedSounds(Context c) {
		soundpool = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
		mappedSounds = new HashMap<Integer,Integer>();
		context = c;
		loadSounds();
	}
	
	public int getSoundId(int index) {
		return mappedSounds.get(index);
	}
	
	public SoundPool getSoundPool() {
		return soundpool;
	}

	private void loadSounds() {
		mappedSounds.put(0,soundpool.load(context,R.raw.dj0, 1));
		mappedSounds.put(1,soundpool.load(context,R.raw.dj1, 1));
		mappedSounds.put(2,soundpool.load(context,R.raw.dj2, 1));
		mappedSounds.put(3,soundpool.load(context,R.raw.dj3, 1));
		mappedSounds.put(4,soundpool.load(context,R.raw.dj4, 1));
		mappedSounds.put(5,soundpool.load(context,R.raw.dj5, 1));
		mappedSounds.put(6,soundpool.load(context,R.raw.dj6, 1));
		mappedSounds.put(7,soundpool.load(context,R.raw.dj7, 1));
		mappedSounds.put(8,soundpool.load(context,R.raw.dj8, 1));
		mappedSounds.put(9,soundpool.load(context,R.raw.dj9, 1));
		mappedSounds.put(10,soundpool.load(context,R.raw.dj10, 1));
		mappedSounds.put(11,soundpool.load(context,R.raw.dj11, 1));
		mappedSounds.put(12,soundpool.load(context,R.raw.dj12, 1));
		mappedSounds.put(13,soundpool.load(context,R.raw.dj13, 1));
		mappedSounds.put(14,soundpool.load(context,R.raw.dj14, 1));
		mappedSounds.put(15,soundpool.load(context,R.raw.dj15, 1));
		mappedSounds.put(16,soundpool.load(context,R.raw.dj16, 1));
		mappedSounds.put(17,soundpool.load(context,R.raw.dj17, 1));
		mappedSounds.put(18,soundpool.load(context,R.raw.dj18, 1));
		mappedSounds.put(19,soundpool.load(context,R.raw.dj19, 1));
	}
	
}