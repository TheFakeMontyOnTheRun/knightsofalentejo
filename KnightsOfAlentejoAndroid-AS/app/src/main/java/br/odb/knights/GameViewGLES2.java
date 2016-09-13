/**
 *
 */
package br.odb.knights;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewManager;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;

import br.odb.GL2JNILib;
import br.odb.droidlib.Renderable;
import br.odb.droidlib.Tile;
import br.odb.droidlib.Updatable;
import br.odb.droidlib.Vector2;
import br.odb.menu.GameActivity;
import br.odb.menu.KnightsOfAlentejoSplashActivity;

/**
 * @author monty
 */
public class GameViewGLES2 extends GLSurfaceView implements GLSurfaceView.Renderer {

	public enum KB {
		UP, RIGHT, DOWN, LEFT, CENTER
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

	final public Object renderingLock = new Object();
	private boolean needsUpdate = true;
	private int currentLevelNumber;

	private GameSession gameSession;
	private Vector2 cameraPosition;
	private GameLevel currentLevel;
	private Knight selectedPlayer;
	private List<Updatable> updatables;

	private final boolean[] keyMap = new boolean[5];
	private final int[] map = new int[20 * 20];
	private final int[] ids = new int[20 * 20];
	private final int[] snapshot = new int[20 * 20];
	private final int[] splats = new int[20 * 20];
	private final Vector2 v = new Vector2();

	private volatile boolean running = true;

	private Updatable gameDelegate;
	private int exitedKnights;

	private long timeUntilTick;
	private long t0;

	private long tick() {

		long delta = (System.currentTimeMillis() - t0);

		timeUntilTick -= delta;

		if (timeUntilTick < 0) {

			for (int c = 0; c < updatables.size(); ++c) {
				updatables.get(c).update(500 - timeUntilTick);
			}

			centerOn( selectedPlayer );

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
				splats[position] = -1;
				ids[ position ] = 0;

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

	public void selectDefaultKnight() {
		Knight newSelected = null;

		for ( Knight k : currentLevel.getKnights()) {
			if ( k.isAlive() && !k.hasExited) {
				newSelected = k;
			}
		}

		setSelectedPlayer( newSelected);
	}

	public void init(Context context, Updatable updateDelegate, int level) {

		updatables = new ArrayList<>();
		selectedPlayer = null;
		cameraPosition = new Vector2();

		this.gameSession = GameConfigurations.getInstance()
				.getCurrentGameSession();

		buildPresentation(context.getResources(), level);
		this.gameDelegate = updateDelegate;
		this.currentLevelNumber = level;
		gameDelegate.update(0);
	}


	private void buildPresentation(Resources res, int level) {

		currentLevel = gameSession.obtainCurrentLevel(res, level);

		for (int c = 0; c < currentLevel.getTotalActors(); ++c) {
			updatables.add(currentLevel.getActor(c));
		}
	}

	public void centerOn(Actor actor) {
		if ( actor != null ) {
			cameraPosition = actor.getPosition();
		}
	}

	public void handleKeys(boolean[] keymap) {

		if (!running) {
			return;
		}

		if (selectedPlayer == null)
			return;

		synchronized (renderingLock) {
			if (!selectedPlayer.isAlive() || selectedPlayer.hasExited) {
				selectedPlayer = null;
				gameDelegate.update(0);
				return;
			}

			needsUpdate = true;

			boolean moved = false;

			Tile loco = currentLevel.getTile(selectedPlayer.getPosition());

			selectedPlayer.checkpointPosition();

			if (keymap[KB.UP.ordinal()]) {
				moved = true;
				selectedPlayer.act(Actor.Actions.MOVE_UP);
			} else if (keymap[KB.DOWN.ordinal()]) {
				moved = true;
				selectedPlayer.act(Actor.Actions.MOVE_DOWN);
			} else if (keymap[KB.LEFT.ordinal()]) {
				moved = true;
				selectedPlayer.act(Actor.Actions.MOVE_LEFT);
			} else if (keymap[KB.RIGHT.ordinal()]) {
				moved = true;
				selectedPlayer.act(Actor.Actions.MOVE_RIGHT);
			} else if ( keymap[ KB.CENTER.ordinal() ] ) {
				GameActivity activity = ((GameActivity) getContext());
				activity.toggleCamera();
			}

			if (!this.currentLevel.validPositionFor(selectedPlayer)) {

				if (currentLevel.getActorAt(selectedPlayer.getPosition()) != null
						&& !(currentLevel.getActorAt(selectedPlayer.getPosition()) instanceof Knight)) {
					currentLevel.battle(selectedPlayer,
							currentLevel.getActorAt(selectedPlayer.getPosition()));
				}

				if (!selectedPlayer.isAlive()) {
					selectedPlayerHasDied();
					gameDelegate.update(0);
					return;
				}
				selectedPlayer.undoMove();
			} else {
				loco.setOccupant(null);
				loco = currentLevel.getTile(selectedPlayer.getPosition());
				loco.setOccupant(selectedPlayer);
			}

			if (moved) {
				currentLevel.tick();
			}

			if (!selectedPlayer.isAlive()) {
				selectedPlayerHasDied();
			}

			if (loco.getKind() == KnightsConstants.DOOR) {

				selectedPlayer.setAsExited();
				++exitedKnights;

				selectedPlayerHasExited();

				if ((currentLevel.getTotalAvailableKnights() - exitedKnights) > 0) {
					Toast.makeText(this.getContext(), R.string.knight_escaped, Toast.LENGTH_SHORT).show();
				} else {
					advanceLevel();
					return;
				}
			}

			gameDelegate.update(0);
		}
	}

	private void advanceLevel() {

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				currentLevel.tick();
				selectedPlayer = null;
				gameDelegate.update(0);
			}
		}, 1000 );
	}

	private void selectedPlayerHasExited() {
		if (!running) {
			return;
		}

		if ( currentLevel.getTotalAvailableKnights() > exitedKnights) {
			selectDefaultKnight();
		}
	}

	private void selectedPlayerHasDied() {

		if (!running) {
			return;
		}

		if (currentLevel.getTotalAvailableKnights() == 0) {
			GL2JNILib.fadeOut();
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					Intent intent = new Intent();
					intent.putExtra(KnightsOfAlentejoSplashActivity.MAPKEY_SUCCESSFUL_LEVEL_COMPLETION, 2);
					GameActivity activity = ((GameActivity) getContext());
					activity.setResult(Activity.RESULT_OK, intent);
					activity.finish();
				}
			}, 1000 );
		} else {
			Toast.makeText(getContext(), R.string.knight_dead,
					Toast.LENGTH_SHORT).show();
			selectedPlayer = null;
		}
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		boolean handled = false;

		if (!running) {
			return false;
		}

		synchronized (renderingLock) {

			if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
				keyMap[KB.UP.ordinal()] = false;
				handled = true;
			}

			if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
				keyMap[KB.DOWN.ordinal()] = false;
				handled = true;
			}
			if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
				keyMap[KB.LEFT.ordinal()] = false;
				handled = true;
			}
			if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
				keyMap[KB.RIGHT.ordinal()] = false;
				handled = true;
			}

			if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER ) {
				keyMap[KB.CENTER.ordinal()] = false;
				handled = true;
			}
		}
		needsUpdate = true;

		return handled;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean handled = false;
		if (!running) {
			return false;
		}

		synchronized (renderingLock) {
			Knight[] knights = currentLevel.getKnights();
			int index = 0;

			if (keyCode == KeyEvent.KEYCODE_X || keyCode == KeyEvent.KEYCODE_BUTTON_X) {
				for (Knight k : knights) {
					if (selectedPlayer == k) {
						selectedPlayer = knights[((index + 1) % (knights.length))];
						handled = true;
					} else {
						++index;
					}
				}
			}

			if (keyCode == KeyEvent.KEYCODE_Y || keyCode == KeyEvent.KEYCODE_BUTTON_Y) {
				GameActivity activity = ((GameActivity) getContext());
				activity.toggleCamera();
			}


			if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
				keyMap[KB.UP.ordinal()] = true;
				handled = true;
			}

			if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
				keyMap[KB.DOWN.ordinal()] = true;
				handled = true;
			}
			if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
				keyMap[KB.LEFT.ordinal()] = true;
				handled = true;
			}
			if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
				keyMap[KB.RIGHT.ordinal()] = true;
				handled = true;
			}

			if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER ) {
				keyMap[KB.CENTER.ordinal()] = true;
				handled = true;
			}


			handleKeys(keyMap);
		}

		return handled;
	}

	public ViewManager getParentViewManager() {
		return (ViewManager) getParent();
	}

	public void stopRunning() {
		this.running = false;
	}

	public void setIsPlaying(boolean isPlaying) {
		this.running = isPlaying;
	}

	public GameLevel getCurrentLevel() {
		return currentLevel;
	}

	public int getExitedKnights() {
		return exitedKnights;
	}

	public Knight getSelectedPlayer() {
		return selectedPlayer;
	}

	public void setSelectedPlayer(Knight knight) {

		if ( selectedPlayer == knight ) {
			return;
		}

		if ( selectedPlayer != null ) {
			selectedPlayer.setRestedStance();
		}

		this.selectedPlayer = knight;
		selectedPlayer.setActiveStance();
	}

	public void setNeedsUpdate() {
		needsUpdate = true;
	}
}
