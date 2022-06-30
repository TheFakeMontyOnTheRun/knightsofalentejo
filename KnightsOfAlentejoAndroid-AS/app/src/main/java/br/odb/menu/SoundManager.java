package br.odb.menu;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

/**
 * Created by monty on 23/06/16.
 */
class SoundManager {
	private final Context mContext;
	private MediaPlayer mMusic;

	private boolean mayEnableSound() {
		android.media.AudioManager am = (android.media.AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);

		if (am.getRingerMode() == AudioManager.RINGER_MODE_NORMAL) {
			return true;
		}
		return false;
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
