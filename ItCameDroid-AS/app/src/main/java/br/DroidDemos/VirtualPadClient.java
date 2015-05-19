package br.DroidDemos;

import android.graphics.Bitmap;

public interface VirtualPadClient {
	public void handleKeys( boolean[] keymap );

	public Bitmap getBitmapOverlay();
}
