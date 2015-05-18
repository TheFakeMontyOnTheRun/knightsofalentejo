package br.odb.droidlib;

import android.graphics.Bitmap;

/**
 * 
 * @author monty
 *
 */
public class StripSprite extends Sprite {

	public StripSprite(Bitmap source) {
		super(source);
	}

	public void play() {
		this.setVisible( true );
		currentFrame = 0;
	}
}
