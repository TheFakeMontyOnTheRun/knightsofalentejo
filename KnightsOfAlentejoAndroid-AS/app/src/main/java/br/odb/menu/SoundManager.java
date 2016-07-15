package br.odb.menu;

import android.content.Context;
import android.media.MediaPlayer;

import br.odb.knights.R;

/**
 * Created by monty on 23/06/16.
 */
public class SoundManager {
	private final Context mContext;
	private MediaPlayer mMusic;

	public boolean mayEnableSound() {
		android.media.AudioManager am = (android.media.AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);

		switch (am.getRingerMode()) {
			case android.media.AudioManager.RINGER_MODE_SILENT:
			case android.media.AudioManager.RINGER_MODE_VIBRATE:
				return false;
			case android.media.AudioManager.RINGER_MODE_NORMAL:
				return true;
			default:
				return false;
		}
	}

	public SoundManager(Context context) {
		this.mContext = context;
	}

	public void playMusic(int musicRes) {
		if ( mayEnableSound() ) {
			mMusic = MediaPlayer.create( mContext, musicRes );
			mMusic.start();
		}
	}

	public void stop() {
		if ( mMusic != null ) {
			mMusic.stop();
			mMusic.release();
			mMusic = null;
		}
	}
}