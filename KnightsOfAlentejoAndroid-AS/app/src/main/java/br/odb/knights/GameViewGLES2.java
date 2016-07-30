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
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewManager;
import android.widget.Toast;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLContext;
import javax.microedition.khronos.egl.EGLDisplay;
import javax.microedition.khronos.opengles.GL10;

import br.odb.GL2JNILib;
import br.odb.droidlib.Tile;
import br.odb.droidlib.Updatable;
import br.odb.droidlib.Vector2;
import br.odb.menu.GameActivity;
import br.odb.menu.KnightsOfAlentejoSplashActivity;

/**
 * @author monty
 */
public class GameViewGLES2 extends GLSurfaceView implements GLSurfaceView.Renderer, GameScreenView {

	final public Object renderingLock = new Object();
	private boolean needsUpdate = true;

	public enum KB {
		UP, RIGHT, DOWN, LEFT
	}

	private GameSession gameSession;
	private Vector2 cameraPosition;
	public GameLevel currentLevel;
	public Actor selectedPlayer;
	public Tile selectedTile;
	private ArrayList<Updatable> updatables;

	final public boolean[] keyMap = new boolean[8];
	final int[] map = new int[20 * 20];
	final int[] snapshot = new int[20 * 20];
	final int[] splats = new int[20 * 20];
	final Vector2 v = new Vector2();
	private int aliveKnightsInCurrentLevel;
	volatile public boolean running = true;

	private Updatable gameDelegate;
	public int exitedKnights;

	long timeUntilTick;
	long t0;

	public void tick() {

		timeUntilTick -= (System.currentTimeMillis() - t0);

		if (timeUntilTick < 0) {
			for (int c = 0; c < updatables.size(); ++c) {
				updatables.get(c).update();
			}
			needsUpdate = true;
			timeUntilTick = 100;
			t0 = System.currentTimeMillis();
		}
	}

	public GameViewGLES2(Context context, AttributeSet attrs) {
		super(context, attrs);

		setEGLContextClientVersion(2);
		setEGLContextFactory(new ContextFactory());
		setRenderer(this);

		t0 = System.currentTimeMillis();

		setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				GL2JNILib.toggleCloseupCamera();
			}
		});
	}


	@Override
	public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
		Log.d("Monty", "surface created");
	}

	@Override
	public void onSurfaceChanged(GL10 gl10, int width, int height) {
		Log.d("Monty", "surface changed");
		GL2JNILib.init(width, height);
	}

	@Override
	public void onDrawFrame(GL10 gl10) {

		if (!running) {
			return;
		}

		synchronized (renderingLock) {
			tick();
			if (needsUpdate) {
				needsUpdate = false;
				Log.d("Monty", "updating snapshot");


				int position;
				GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
				GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
				GL2JNILib.tick();

				for (int y = 0; y < 20; ++y) {
					for (int x = 0; x < 20; ++x) {
						v.x = x;
						v.y = y;
						position = (y * 20) + x;
						int index = this.currentLevel.getTile(v).getTextureIndex();

						map[position] = this.currentLevel.getTile(v).getMapTextureIndex();
						splats[position] = this.currentLevel.getTile(v).getSplats();

						if (index > 7) {
							snapshot[position] = index;
						} else {
							snapshot[position] = -1;
						}
					}
				}

				GL2JNILib.setMapWithSplatsAndActors(map, snapshot, splats);
			}
			GL2JNILib.setCurrentCursorPosition( cameraPosition.x, cameraPosition.y);
			GL2JNILib.setCameraPosition(cameraPosition.x, cameraPosition.y);
			GL2JNILib.step();
		}
	}

	private static class ContextFactory implements GLSurfaceView.EGLContextFactory {
		private static int EGL_CONTEXT_CLIENT_VERSION = 0x3098;

		public EGLContext createContext(EGL10 egl, EGLDisplay display, EGLConfig eglConfig) {
			int[] attrib_list = {EGL_CONTEXT_CLIENT_VERSION, 2, EGL10.EGL_NONE};
			EGLContext context = egl.eglCreateContext(display, eglConfig, EGL10.EGL_NO_CONTEXT, attrib_list);
			return context;
		}

		public void destroyContext(EGL10 egl, EGLDisplay display, EGLContext context) {
			egl.eglDestroyContext(display, context);
		}
	}

	public void init(Context context, Updatable updateDelegate, int level) {


		aliveKnightsInCurrentLevel = 3;
		updatables = new ArrayList<Updatable>();
		selectedPlayer = null;
		cameraPosition = new Vector2();

		this.gameSession = GameConfigurations.getInstance()
				.getCurrentGameSession();

		buildPresentation(context.getResources(), level);
		this.gameDelegate = updateDelegate;

		gameDelegate.update();
	}


	private void buildPresentation(Resources res, int level) {

		currentLevel = gameSession.obtainCurrentLevel(res, level);

		for (int c = 0; c < currentLevel.getTotalActors(); ++c) {
			updatables.add(currentLevel.getActor(c));
		}
	}

	public void centerOn(Actor actor) {

		cameraPosition = actor.getPosition();
	}

	public void handleKeys(boolean[] keymap) {

		if (!running) {
			return;
		}

		if (selectedPlayer == null)
			return;

		if (!selectedPlayer.isAlive()) {
			selectedPlayer = null;
			gameDelegate.update();
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
		}

		if (!this.currentLevel.validPositionFor(selectedPlayer)) {

			if (currentLevel.getActorAt(selectedPlayer.getPosition()) != null
					&& !(currentLevel.getActorAt(selectedPlayer.getPosition()) instanceof Knight)) {
				currentLevel.battle(selectedPlayer,
						currentLevel.getActorAt(selectedPlayer.getPosition()));
			}

			if (!selectedPlayer.isAlive()) {
				selectedPlayerHasDied();
				gameDelegate.update();
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

			if ((aliveKnightsInCurrentLevel - exitedKnights) > 1) {

				Toast.makeText(this.getContext(), R.string.knight_escaped, Toast.LENGTH_SHORT).show();
			}

			((Knight) selectedPlayer).setAsExited();
			++exitedKnights;
		}

		gameDelegate.update();
	}

	private void selectedPlayerHasDied() {

		if (!running) {
			return;
		}

		aliveKnightsInCurrentLevel--;

		if (aliveKnightsInCurrentLevel == 0) {

			Intent intent = new Intent();
			intent.putExtra(KnightsOfAlentejoSplashActivity.MAPKEY_SUCCESSFUL_LEVEL_COMPLETION, 2);
			GameActivity activity = ((GameActivity) this.getContext());
			activity.setResult(Activity.RESULT_OK, intent);
			activity.finish();
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

			handleKeys(keyMap);
		}

		return handled;
	}

	@Override
	public ViewManager getParentViewManager() {
		return (ViewManager) getParent();
	}

	@Override
	public void stopRunning() {
		this.running = false;
	}

	@Override
	public void setIsPlaying(boolean isPlaying) {
		this.running = isPlaying;
	}

	@Override
	public GameLevel getCurrentLevel() {
		return currentLevel;
	}

	@Override
	public int getExitedKnights() {
		return exitedKnights;
	}

	@Override
	public Actor getSelectedPlayer() {
		return selectedPlayer;
	}

	@Override
	public void setSelectedPlayer(Actor knight) {
		this.selectedPlayer = knight;
	}

	@Override
	public void setSelectedTile(Tile tile) {
		this.selectedTile = tile;
	}

	@Override
	public boolean[] getKeyMap() {
		return this.keyMap;
	}
}
