package ca.brocku.easydjsoundpad;

import java.io.Serializable;

public class PlaybackPair implements Serializable {

	private static final long serialVersionUID = 4232699165291132258L;
	Double time;
	Integer soundPos;
	Float rate;
	
	public PlaybackPair(Double time, Integer soundPos, Float rate) {
		this.time = time;
		this.soundPos = soundPos;
		this.rate = rate;
	}
	
	public PlaybackPair(PlaybackPair p) {
		this.time = p.time;
		this.soundPos = p.soundPos;
		this.rate = p.rate;
				
	}

}
