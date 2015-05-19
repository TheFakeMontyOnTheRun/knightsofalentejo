/**
 * 
 */
package br.DroidLib;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.MotionEvent;
import br.DroidDemos.VirtualPadClient;

/**
 * @author monty
 * 
 */
public class VirtualPad extends Drawable {
	public static final int KB_UP = 0;
	public static final int KB_RIGHT = 1;
	public static final int KB_DOWN = 2;
	public static final int KB_LEFT = 3;
	public static final int KB_FIRE = 4;

	private boolean[] keyMap;

	private Rect[] vKeys;
	private Rect lastTouch1;
	private Paint paint;
	private VirtualPadClient listener;

	public VirtualPad(VirtualPadClient listener) {
		super();
		this.listener = listener;
		paint = new Paint();
		vKeys = new Rect[4];
		vKeys[0] = new Rect();
		vKeys[1] = new Rect();
		vKeys[2] = new Rect();
		vKeys[3] = new Rect();
		lastTouch1 = new Rect();
		keyMap = new boolean[5];
	}

	public void setBounds(Rect bounds) {
		setBounds(bounds.left, bounds.top, bounds.right, bounds.bottom);
	}

	public void setBounds(int left, int top, int right, int bottom) {
		super.setBounds(left, top, right, bottom);
		int width = right - left;
		int height = bottom - left;
		vKeys[0].set((int) ((width * 15L) / 100L),
				(int) ((height * 30L) / 100L), (int) ((width * 20L) / 100L),
				(int) ((height * 40L) / 100L));
		vKeys[1].set((int) ((width * 25L) / 100L),
				(int) ((height * 40L) / 100L), (int) ((width * 30L) / 100L),
				(int) ((height * 50L) / 100L));
		vKeys[2].set((int) ((width * 15L) / 100L),
				(int) ((height * 50L) / 100L), (int) ((width * 20L) / 100L),
				(int) ((height * 60L) / 100L));
		vKeys[3].set((int) ((width * 5L) / 100L),
				(int) ((height * 40L) / 100L), (int) ((width * 10L) / 100L),
				(int) ((height * 50L) / 100L));
	}

	@Override
	public void draw(Canvas canvas) {
		for (int c = 0; c < vKeys.length; c++) {

			if (keyMap[c])
				paint.setARGB(64, 0, 0, 255);
			else
				paint.setARGB(64, 255, 0, 0);

			canvas.drawCircle( vKeys[ c ].exactCenterX(), vKeys[ c ].exactCenterY(), vKeys[ c ].width(), paint );
		}
		
		synchronized( listener ) {
			
			android.graphics.Bitmap overlay = listener.getBitmapOverlay();
			
			if ( overlay != null ) {
				paint.setARGB(128, 0, 0, 0);
				Rect rect = new Rect();
				
				rect.top = (int) vKeys[ 0 ].exactCenterY();
				rect.bottom = (int) vKeys[ 2 ].exactCenterY();
				rect.left = (int) vKeys[ 3 ].exactCenterX();
				rect.right = (int) vKeys[ 1 ].exactCenterX();
				
				canvas.drawBitmap( overlay, null, rect, paint );
			}
		}
		

		paint.setARGB(255, 0, 0, 0);
	}

	@Override
	public int getOpacity() {
		return 0;
	}

	@Override
	public void setAlpha(int arg0) {
	}

	@Override
	public void setColorFilter(ColorFilter arg0) {
	}

	public boolean onTouchEvent(MotionEvent event) {

		lastTouch1.set((int) event.getX() - 25, (int) event.getY() - 25,
				(int) (event.getX() + 25), (int) (event.getY() + 25));
		
		return updateTouch((event.getAction() != MotionEvent.ACTION_UP));
	}

	public boolean updateTouch(boolean down) {

		boolean returnValue = false;

		for (int c = 0; c < vKeys.length; c++) {

			if (Rect.intersects(vKeys[c], lastTouch1)) {

				keyMap[c] = down;
				returnValue = keyMap[c];
			} else {
				keyMap[c] = false;
			}
		}

		return returnValue;
	}

	public boolean[] getKeyMap() {

		return keyMap;
	}

}
