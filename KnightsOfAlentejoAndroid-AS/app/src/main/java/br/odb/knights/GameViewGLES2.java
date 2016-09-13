/**
 *
 */
package br.odb.knights;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewManager;
import android.widget.Toast;

import java.io.IOException;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;

import br.odb.GL2JNILib;
import br.odb.droidlib.Renderable;
import br.odb.droidlib.Tile;
import br.odb.droidlib.Vector2;
import br.odb.menu.GameActivity;

/**
 * @author monty
 */
public class GameViewGLES2 extends GLSurfaceView implements GLSurfaceView.Renderer  {

	public interface GameRenderer {
		void fadeIn();
		void fadeOut();
		void setNeedsUpdate();
		void displayKnightIsDeadMessage();
		void displayLevelAdvanceMessage();
		void displayKnightEnteredDoorMessage();
		void displayGreetingMessage();
		void toggleCamera();
	}

	public static final int SPLAT_NONE = -1;
	public static final int ID_NO_ACTOR = 0;

	public enum KB {
		UP, RIGHT, DOWN, LEFT, TOGGLE_CAMERA, CYCLE_CURRENT_KNIGHT
	}

	public enum ETextures {
		None,
		Grass,
		Bricks,
		Arch,
		Bars,
		Begin,
		Exit,
		BricksBlood,
		BricksCandles,
		Boss0,
		Boss1,
		Boss2,
		Cuco0,
		Cuco1,
		Cuco2,
		Demon0,
		Demon1,
		Demon2,
		Lady0,
		Lady1,
		Lady2,
		Bull0,
		Bull1,
		Bull2,
		Falcon0,
		Falcon1,
		Falcon2,
		Turtle0,
		Turtle1,
		Turtle2,
		Shadow,
		CursorGood0,
		CursorGood1,
		CursorGood2,
		CursorBad0,
		CursorBad1,
		CursorBad2,
		Ceiling,
		CeilingDoor,
		CeilingBegin,
		CeilingEnd,
		Splat0,
		Splat1,
		Splat2,
		CeilingBars,
	};

	private static class ContextFactory implements GLSurfaceView.EGLContextFactory {
		private static final int EGL_CONTEXT_CLIENT_VERSION = 0x3098;

		public EGLContext createContext(EGL10 egl, EGLDisplay display, EGLConfig eglConfig) {
			int[] attrib_list = {EGL_CONTEXT_CLIENT_VERSION, 2, EGL10.EGL_NONE};
			return egl.eglCreateContext(display, eglConfig, EGL10.EGL_NO_CONTEXT, attrib_list);
		}

		public void destroyContext(EGL10 egl, EGLDisplay display, EGLContext context) {
			egl.eglDestroyContext(display, context);
		}
	}

	GameRenderer gameRenderer = new GameRenderer() {
		@Override
		public void fadeIn() {
			GL2JNILib.fadeIn();
		}

		@Override
		public void fadeOut() {
			GL2JNILib.fadeOut();
		}

		@Override
		public void setNeedsUpdate() {
			GameViewGLES2.this.setNeedsUpdate();
		}

		@Override
		public void displayKnightIsDeadMessage() {
			Toast.makeText(getContext(), R.string.knight_dead,
					Toast.LENGTH_SHORT).show();
		}

		@Override
		public void displayLevelAdvanceMessage() {
			Toast.makeText(getContext(), R.string.level_greeting_others, Toast.LENGTH_SHORT).show();
		}

		@Override
		public void displayKnightEnteredDoorMessage() {
			Toast.makeText(getContext(), R.string.knight_escaped, Toast.LENGTH_SHORT).show();
		}

		@Override
		public void displayGreetingMessage() {
			Toast.makeText(getContext(), R.string.level_greeting_0, Toast.LENGTH_SHORT).show();
		}

		@Override
		public void toggleCamera() {
			( (GameActivity)getContext() ).toggleCamera();
		}
	};

	View.OnKeyListener keyListener = new OnKeyListener() {
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {

			GameViewGLES2.KB key = null;

			if ( event.getAction() == KeyEvent.ACTION_DOWN ) {
				if (keyCode == KeyEvent.KEYCODE_X || keyCode == KeyEvent.KEYCODE_BUTTON_X) {
					key = KB.CYCLE_CURRENT_KNIGHT;
				}

				if (keyCode == KeyEvent.KEYCODE_Y || keyCode == KeyEvent.KEYCODE_BUTTON_Y) {
					key = GameViewGLES2.KB.TOGGLE_CAMERA;
				}

				if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
					key = GameViewGLES2.KB.UP;
				}

				if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
					key = GameViewGLES2.KB.DOWN;
				}
				if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
					key = GameViewGLES2.KB.LEFT;
				}
				if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
					key = GameViewGLES2.KB.RIGHT;
				}

				if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER) {
					key = GameViewGLES2.KB.TOGGLE_CAMERA;
				}
			}

			if ( key != null ) {
				handleCommand(key);
			}

			return key != null;
		}
	};

	final public Object renderingLock = new Object();
	private boolean needsUpdate = true;
	private volatile boolean running = true;
	private boolean birdView;
	private Vector2 cameraPosition;
	private long timeUntilTick;
	private long t0;

	//snapshot
	private final int[] map = new int[20 * 20];
	private final int[] ids = new int[20 * 20];
	private final int[] snapshot = new int[20 * 20];
	private final int[] splats = new int[20 * 20];
	private final Vector2 v = new Vector2();

	//game logic stuff - that shouldn't really be here.
	private GameLevel currentLevel;
	private GameActivity.GameDelegate gameDelegate;
	private GameSession gameSession;
	private int currentLevelNumber;


	private long tick() {

		long delta = (System.currentTimeMillis() - t0);

		timeUntilTick -= delta;

		if (timeUntilTick < 0) {

			centerOn(currentLevel.getSelectedPlayer() );

			currentLevel.updateSplats(500 - timeUntilTick);
			needsUpdate = needsUpdate || currentLevel.needsUpdate();
			timeUntilTick = 500;
			t0 = System.currentTimeMillis();
		}

		return delta;
	}

	public GameViewGLES2(Context context, AttributeSet attrs) {
		super(context, attrs);

		setEGLContextClientVersion(2);
		setEGLContextFactory(new ContextFactory());
		setRenderer(this);

		t0 = System.currentTimeMillis();
	}


	@Override
	public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
		Log.d("Monty", "surface created");
	}

	@Override
	public void onSurfaceChanged(GL10 gl10, int width, int height) {
		Log.d("Monty", "surface changed");
		GL2JNILib.init(width, height);
		float hue = ((255.0f / GameLevelLoader.NUMBER_OF_LEVELS) * currentLevelNumber )/ 255.0f;
		GL2JNILib.setClearColour( hue, 1.0f - (hue), 1.0f - hue);
	}

	@Override
	public void onDrawFrame(GL10 gl10) {

		if (!running) {
			return;
		}

		synchronized (renderingLock) {
			long delta = tick();
			if (needsUpdate) {
				needsUpdate = false;
				updateNativeSnapshot();
			}

			GL2JNILib.step(delta);
		}
	}

	private void updateNativeSnapshot() {
		int position;
		GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

		for (int y = 0; y < 20; ++y) {
			for (int x = 0; x < 20; ++x) {
				v.x = x;
				v.y = y;
				position = (y * 20) + x;

				Tile tile = this.currentLevel.getTile(v);

				ETextures index = tile.getTextureIndex();
				map[position] = tile.getMapTextureIndex().ordinal();
				splats[position] = SPLAT_NONE;
				ids[ position ] = ID_NO_ACTOR;

				Renderable occupant = tile.getOccupant();

				if (  occupant instanceof Actor ) {
					ids[ position ] = ((Actor)occupant).mId;
				}

				if ( ETextures.Boss0.ordinal() <= index.ordinal() && index.ordinal() < ETextures.Shadow.ordinal()) {
					snapshot[position] = index.ordinal();
				} else {
					snapshot[position] = ETextures.None.ordinal();
				}
			}
		}

		for ( Vector2 pos : currentLevel.mSplats.keySet() ) {
			Splat splat = currentLevel.mSplats.get(pos);

			position = (int) ((pos.y * 20) + pos.x);
			splats[position] = splat.getSplatFrame();
		}

		GL2JNILib.setMapWithSplatsAndActors(map, snapshot, splats);
		GL2JNILib.setActorIdPositions( ids );
		GL2JNILib.setCurrentCursorPosition( cameraPosition.x, cameraPosition.y);
		GL2JNILib.setCameraPosition(cameraPosition.x, cameraPosition.y);
	}



	public void init(Context context, GameActivity.GameDelegate delegate, int level) {
		cameraPosition = new Vector2();

		this.gameSession = GameConfigurations.getInstance()
				.getCurrentGameSession();

		this.gameDelegate = delegate;
		buildPresentation(context.getResources(), level);
		this.currentLevelNumber = level;
		gameDelegate.onGameStarted();
	}


	private void buildPresentation(Resources res, int level) {
		currentLevel = gameSession.obtainCurrentLevel(res, level, gameDelegate, gameRenderer);
	}

	public void centerOn(Actor actor) {
		if ( actor != null ) {
			cameraPosition = actor.getPosition();
		}
	}

	public synchronized void setNeedsUpdate() {
		needsUpdate = true;
	}

	public void toggleCamera() {
		birdView = !birdView;
		GL2JNILib.toggleCloseupCamera();
	}

	public boolean isOnBirdView() {
		return birdView;
	}


	public void fadeOut() {
		gameRenderer.fadeOut();
	}

	public void onDestroy() {
		GL2JNILib.onDestroy();
	}

	public void onCreate(AssetManager assets) {
		GL2JNILib.onCreate(assets);
		loadTextures( assets );
	}

	public void setTextures(Bitmap[] bitmaps) {
		GL2JNILib.setTextures(bitmaps);
	}

	public void loadTextures( AssetManager assets ) {
		try {
			Bitmap[] bitmaps;

			boolean isDungeonSurfaceLevel = this.currentLevelNumber > 0;

			bitmaps = loadBitmaps(assets, new String[]{
					"grass.png", //none
					(isDungeonSurfaceLevel ? "stonefloor.png" : "grass.png"),
					"bricks.png",
					"arch.png",
					"bars.png",
					"begin.png",
					"exit.png",
					"bricks_blood.png",
					"bricks_candles.png",
					"boss0.png",
					"boss1.png",
					"boss2.png",
					"cuco0.png",
					"cuco1.png",
					"cuco2.png",
					"demon0.png",
					"demon1.png",
					"demon2.png",
					"lady0.png",
					"lady1.png",
					"lady2.png",
					"bull0.png",
					"bull1.png",
					"bull2.png",
					"falcon0.png",
					"falcon1.png",
					"falcon2.png",
					"turtle0.png",
					"turtle1.png",
					"turtle2.png",
					(isDungeonSurfaceLevel ? "stoneshadow.png" : "shadow.png"),
					(isDungeonSurfaceLevel ? "stonecursorgood.png" : "cursorgood0.png"),
					"cursorgood1.png",
					"cursorgood2.png",
					(isDungeonSurfaceLevel ? "stonecursorbad.png" : "cursorbad0.png"),
					"cursorbad1.png",
					"cursorbad2.png",
					(isDungeonSurfaceLevel ? "stoneceiling.png" : "ceiling.png"),
					"ceilingdoor.png",
					"ceilingbegin.png",
					"ceilingend.png",
					"splat0.png",
					"splat1.png",
					"splat2.png",
					"ceilingbars.png",
			});
			setTextures(bitmaps);
		} catch (IOException e) {
			e.printStackTrace();
			gameDelegate.onFatalError();
		}
	}

	private Bitmap[] loadBitmaps(AssetManager assets, String[] filenames) throws IOException {
		Bitmap[] toReturn = new Bitmap[filenames.length];

		for (int i = 0; i < filenames.length; i++) {
			toReturn[i] = BitmapFactory.decodeStream(assets.open(filenames[i]));
		}

		return toReturn;
	}

	public ViewManager getParentViewManager() {
		return (ViewManager) getParent();
	}

	public void stopRunning() {
		this.running = false;
	}

	@Override
	public void onResume() {
		super.onResume();
		setIsPlaying( true );
	}

	public void setIsPlaying(boolean isPlaying) {
		this.running = isPlaying;
	}

	public void displayLevelAdvanceMessage() {
		gameRenderer.displayLevelAdvanceMessage();
	}

	public void displayGreetingMessage() {
		gameRenderer.displayGreetingMessage();
	}

	private void displayKnightIsDeadMessage() {
		gameRenderer.displayKnightIsDeadMessage();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return keyListener.onKey( this, keyCode, event);
	}
// game logic - that shouldn't really be here.

	public int getExitedKnights() {
		return currentLevel.getTotalExitedKnights();
	}

	public synchronized void handleCommand(KB key) {

		if (!running) {
			return;
		}

		if (currentLevel.getSelectedPlayer() == null) {
			return;
		}

		synchronized (renderingLock) {
			currentLevel.handleCommand(key);
		}
	}

	public GameLevel getCurrentLevel() {
		return currentLevel;
	}
}
