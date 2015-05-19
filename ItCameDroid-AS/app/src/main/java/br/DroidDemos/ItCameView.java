package br.DroidDemos;

import java.util.ArrayList;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import br.DroidLib.Constants;
import br.DroidLib.VirtualPad;
import br.GlobalGameJam.Actor;
import br.GlobalGameJam.Level;
import br.GlobalGameJam.LevelFactory;
import br.GlobalGameJam.Miner;
import br.GlobalGameJam.MiniMapWidget;
import br.GlobalGameJam.Vec2;

public class ItCameView extends View implements Runnable, VirtualPadClient,
		OnTouchListener {

	private static final long INTERVAL = 100;
	MiniMapWidget map;
	private static final int KB_UP = 0;
	private static final int KB_RIGHT = 1;
	private static final int KB_DOWN = 2;
	private static final int KB_LEFT = 3;
	private VirtualPad vPad;
	private boolean[] keyMap;
	private boolean running = true;
	private Paint paint;
	private static Level level;
	private Vec2 camera;
	private Miner actor;
	private Bitmap controlPadOverlay;
	public boolean playing = false;
	public static boolean playSounds = true;
	long timeSinceAcquiredFocus = 0;
	boolean drawOnScreenController;

	public static Rect viewport = new Rect();

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		boolean handled = false;

		if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
			keyMap[KB_UP] = false;
			handled = true;
		}

		if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
			keyMap[KB_DOWN] = false;
			handled = true;
		}
		if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
			keyMap[KB_LEFT] = false;
			handled = true;
		}
		if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
			keyMap[KB_RIGHT] = false;
			handled = true;
		}
		return handled;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean handled = false;

		if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
			keyMap[KB_UP] = true;
			handled = true;
		}

		if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
			keyMap[KB_DOWN] = true;
			handled = true;
		}
		if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
			keyMap[KB_LEFT] = true;
			handled = true;
		}
		if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
			keyMap[KB_RIGHT] = true;
			handled = true;
		}

		if (keyCode == KeyEvent.KEYCODE_BACK)
			System.exit(0);

		return handled;
	}

	public ItCameView(Context context) {
		super(context);

		setFocusable(true);
		setClickable(true);
		setLongClickable(true);
		
		android.media.AudioManager am = (android.media.AudioManager) getContext().getSystemService( Context.AUDIO_SERVICE);

		switch (am.getRingerMode()) {
		    case android.media.AudioManager.RINGER_MODE_SILENT:
		    case android.media.AudioManager.RINGER_MODE_VIBRATE:
		    	playSounds = false;
		        break;
		    case android.media.AudioManager.RINGER_MODE_NORMAL:
		    	playSounds = true;
		        break;
		} 		

		controlPadOverlay = BitmapFactory.decodeResource( getResources(), R.drawable.control_brown ); 
		
		drawOnScreenController = getGameControllerIds().size() == 0;
		
		vPad = new VirtualPad( this );

		this.requestFocus();
		this.setFocusableInTouchMode(true);
		keyMap = vPad.getKeyMap();
		camera = new Vec2(0, 0);
		
		if ( MainMenuActivity.needsReset ) {
			
			level = LevelFactory.createRandomLevel(
					br.GlobalGameJam.Constants.SIZEX,
					br.GlobalGameJam.Constants.SIZEY, getResources(), context);
			
			MainMenuActivity.needsReset = false;
		}
		
		map = new MiniMapWidget(level);

		actor = level.getMiner();
		

		paint = new Paint();
		setBackgroundColor(Color.BLACK);
		Thread monitorThread = new Thread(this, "main game ticker");
		monitorThread.setPriority(Thread.MIN_PRIORITY);
		monitorThread.start();

		 DisplayMetrics displaymetrics = new DisplayMetrics();
		 ( (Activity)this.getContext()
		 ).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		 int screenWidth = displaymetrics.widthPixels;
		 int screenHeight = displaymetrics.heightPixels;
		 viewport.set(0, 0, screenWidth, screenHeight );

		setOnTouchListener(this);
		
 
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {

		super.onWindowFocusChanged(hasFocus);
		
		 int screenWidth = getWidth();
		 int screenHeight = getHeight();
		 
		 if ( hasFocus ) {
			 timeSinceAcquiredFocus = 5000;
		 }

		viewport.set(0, 0, screenWidth, screenHeight );
	}
	
	@SuppressLint("NewApi")
	@SuppressWarnings("rawtypes")
	public ArrayList getGameControllerIds() {
	    ArrayList gameControllerDeviceIds = new ArrayList();
	    
	    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
	    	
	    	int[] deviceIds = InputDevice.getDeviceIds();
	    	for (int deviceId : deviceIds) {
	    		InputDevice dev = InputDevice.getDevice(deviceId);
	    		int sources = dev.getSources();
	    		
	    		// Verify that the device has gamepad buttons, control sticks, or both.
	    		if (((sources & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD)
	    				|| ((sources & InputDevice.SOURCE_JOYSTICK)
	    						== InputDevice.SOURCE_JOYSTICK)) {
	    			// This device is a game controller. Store its device ID.
	    			if (!gameControllerDeviceIds.contains(deviceId)) {
	    				gameControllerDeviceIds.add(deviceId);
	    			}
	    		}
	    	}
	    }
	    return gameControllerDeviceIds;
	}


	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		synchronized (actor) {

			vPad.setBounds(0, 0, getWidth(), getHeight());

			level.setCurrentCamera(camera);

			if (level != null && paint != null) {
				level.setCurrentCamera(actor.getPosition());
				level.draw(canvas, paint);
			}

			paint.setARGB(255, 0, 0, 0);
			
			if ( drawOnScreenController ) {
				
				vPad.draw(canvas);
			}
			
			drawMap(canvas);
			paint.setColor(Color.YELLOW);

			paint.setFakeBoldText(true);

			canvas.drawText("Você derrotou " + level.dead
					+ " monstros; Tempo para a detonação: "
					+ (level.dynamite.timeToBlow / 1000) + "s", 0,
					getHeight() - 50, paint);

			paint.setFakeBoldText(false);
		}
		
		
		if ( timeSinceAcquiredFocus > 0 ) {
			String text = "Jogo começando em " + ( timeSinceAcquiredFocus / 1000 );
			Rect bounds = new Rect();
			paint.getTextBounds( text, 0, text.length(), bounds );
			float prevSize = paint.getTextSize();
			paint.setTextSize( 30 );
			canvas.drawText( text, ( getWidth()  )/ 2 - bounds.width(), getHeight() / 2, paint );
			paint.setTextSize( prevSize );
		}
	}

	private void drawMap(Canvas canvas) {

		int x2;
		int y2;

//		paint.setAntiAlias(true);
//		paint.setDither(true);
//		paint.setFilterBitmap(true);

		paint.setColor(Color.YELLOW);
		paint.setAlpha(128);
		
		for (int x = 0; x < level.getWidth(); ++x) {
			for (int y = 0; y < level.getHeight(); ++y) {

				if (!level.mayMoveTo(x, y)) {

					canvas.drawRect(x * 5, y * 5, (x + 1) * 5, (y + 1) * 5,
							paint);
				}
			}
		}
		
		paint.setColor(Color.BLUE);
		paint.setAlpha(128);
		
		for (Actor a : level.getActors()) {

			if (a.killed) {
				continue;
			}

			x2 = (int) (a.getPosition().x / Constants.BASETILEWIDTH);
			y2 = (int) (a.getPosition().y / Constants.BASETILEHEIGHT);

			canvas.drawRect(x2 * 5, y2 * 5, (x2 + 1) * 5,
					(y2 + 1) * 5, paint);
		}
		
		paint.setAlpha(255);
	}

	@Override
	public void run() {

		running = true;

		while (running) {
			

			try {
				Thread.sleep(INTERVAL);
			} catch (InterruptedException e) {
				e.printStackTrace();
				running = false;
			}

			if ( !playing ) {
				continue;
			}
			
			if (timeSinceAcquiredFocus > 0 ) {
				timeSinceAcquiredFocus -= INTERVAL;
				postInvalidate();
				continue;
			}

			handleKeys(keyMap);

			this.level.tick(INTERVAL);

			if (level.gameShouldEnd) {
				running = false;
				Intent intent = ((ItCameFromTheCaveActivity) this.getContext())
						.getIntent();
				intent.putExtra("result", level.getMiner().killed ? "failure"
						: "victory");
				((ItCameFromTheCaveActivity) this.getContext()).setResult(
						Activity.RESULT_OK, intent);
				((ItCameFromTheCaveActivity) this.getContext()).finish();
				return;
			}

			if (level.dynamite.killed
					&& level.dynamite.getPosition()
							.isCloseEnoughToConsiderEqualTo(
									level.getMiner().getPosition())) {
				Intent intent = ((ItCameFromTheCaveActivity) this.getContext())
						.getIntent();

				intent.putExtra("result", level.getMiner().killed ? "failure"
						: "victory");
				((ItCameFromTheCaveActivity) this.getContext()).setResult(
						Activity.RESULT_OK, intent);

				running = false;
				((ItCameFromTheCaveActivity) this.getContext()).finish();
			}

			postInvalidate();
		}
	}

	public void pause() {
		running = false;
	}

	@Override
	public void handleKeys(boolean[] keymap) {
		
		if ( timeSinceAcquiredFocus > 0 ) {
			return;
		}
		
		synchronized (actor) {

			boolean handled = false;
			int x = 0;
			int y = 0;
			int currentX = (int) (actor.getPosition().x / Constants.BASETILEWIDTH);
			int currentY = (int) (actor.getPosition().y / Constants.BASETILEHEIGHT);

			if (keymap[KB_UP]) {
				y -= 1.25f;
				actor.setDirection(0);
				handled = true;
			} else if (keymap[KB_DOWN]) {
				y += 1.25f;
				actor.setDirection(2);
				handled = true;
			} else if (keymap[KB_LEFT]) {
				x -= 1.25f;
				actor.setDirection(3);
				handled = true;
			} else if (keymap[KB_RIGHT]) {
				actor.setDirection(1);
				x += 1.25f;
				handled = true;
			}

			if (handled) {
				if (level.mayMoveTo(currentX + x, currentY + y)) {
					actor.move(x * Constants.BASETILEWIDTH, y
							* Constants.BASETILEHEIGHT);
					actor.setState(Actor.states.MOVING);
				}
			} else
				actor.setState(Actor.states.STILL);

			if (actor.getPosition().x < 0)
				actor.getPosition().x = 0;

			if (actor.getPosition().y < 0)
				actor.getPosition().y = 0;
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		return vPad.onTouchEvent(event);
	}

	@Override
	public Bitmap getBitmapOverlay() {
		return controlPadOverlay;
	}
}
