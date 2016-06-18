/**
 *
 */
package br.odb.knights;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Paint;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ViewManager;
import android.widget.Toast;

import java.util.ArrayList;

import javax.microedition.khronos.egl.EGLConfig;
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

	final Object renderingLock = new Object();
	private boolean needsUpdate = true;

	public GameViewGLES2(Context context, AttributeSet attrs) {
		super(context, attrs);

		setEGLContextClientVersion(2);
		setRenderer(this);
	}


	@Override
	public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {

	}

	@Override
	public void onSurfaceChanged(GL10 gl10, int width, int height) {
		Log.d("Monty", "surface changed");
		GL2JNILib.init(width, height);
	}

	@Override
	public void onDrawFrame(GL10 gl10) {

		if (needsUpdate) {
			needsUpdate = false;

			synchronized (renderingLock) {

				GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
				GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
				GL2JNILib.tick();

				int[][] map;

				map = new int[20][];

				for (int y = 0; y < 20; ++y) {
					map[y] = new int[20];

					for (int x = 0; x < 20; ++x) {
						map[y][x] = this.currentLevel.getTile(new Vector2(x, y)).getTextureIndex();
					}
				}

				GL2JNILib.setSnapshot(map);
			}
		}


		GL2JNILib.setCameraPosition(cameraPosition.x, cameraPosition.y);
		GL2JNILib.step();
	}

	public enum KB {
		UP, RIGHT, DOWN, LEFT
	}

	private GameSession gameSession;
	private Vector2 cameraPosition;
	private Vector2 cameraScroll;
	private Vector2 lastTouchPosition;
	public GameLevel currentLevel;
	private Vector2 accScroll;
	public Actor selectedPlayer;
	public Tile selectedTile;
	final private Paint paint = new Paint();
	private ArrayList<Updatable> updatables;

	final public boolean[] keyMap = new boolean[8];
	private int aliveKnightsInCurrentLevel;
	volatile public boolean running = true;
	public boolean playing = false;

	private Updatable gameDelegate;
	public int exitedKnights;

	public void init(Context context, Updatable updateDelegate, int level) {

		aliveKnightsInCurrentLevel = 3;
		updatables = new ArrayList<Updatable>();
		selectedPlayer = null;
//        accScroll = new Vector2();
		cameraPosition = new Vector2();
//        cameraScroll = new Vector2();
//        lastTouchPosition = new Vector2();

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

//    public void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//
//        if (gameSession == null) {
//            return;
//        }
//
//
//        paint.setColor(Color.RED);
//
//        if (currentLevel != null) {
//            currentLevel.position.x = 0.0f;
//            currentLevel.position.y = 0.0f;
//            currentLevel.size.x = 0.0f;
//            currentLevel.size.y = 0.0f;
//            currentLevel.draw(canvas, cameraPosition);
//        }
//
//        paint.setColor(Color.BLUE);
//        paint.setStyle(Style.STROKE);
//
//        if (selectedTile != null) {
//
//            canvas.drawRect(
//                    -(cameraPosition.x * Tile.TILE_SIZE_X)
//                            + selectedTile.getPosition().x,
//                    -(cameraPosition.y * Tile.TILE_SIZE_Y)
//                            + selectedTile.getPosition().y,
//                    -(cameraPosition.x * Tile.TILE_SIZE_X)
//                            + selectedTile.getPosition().x + Tile.TILE_SIZE_X,
//                    -(cameraPosition.y * Tile.TILE_SIZE_Y)
//                            + selectedTile.getPosition().y + Tile.TILE_SIZE_Y,
//                    paint);
//        }
//    }

	@Override
	public boolean onTouchEvent(MotionEvent event) {
//
//        Vector2 touch = new Vector2();
//
//        touch.x = cameraPosition.x + ((event.getX()) / Tile.TILE_SIZE_X);
//        touch.y = cameraPosition.y + ((event.getY()) / Tile.TILE_SIZE_Y);
//
//        if (event.getAction() == MotionEvent.ACTION_MOVE) {
//
//            cameraScroll.x += (event.getX() - lastTouchPosition.x);
//            cameraScroll.y += (event.getY() - lastTouchPosition.y);
//
//            accScroll.x += (event.getX() - lastTouchPosition.x);
//            accScroll.y += (event.getY() - lastTouchPosition.y);
//
//            lastTouchPosition.x = (int) event.getX();
//            lastTouchPosition.y = (int) event.getY();
//
//            cameraPosition.x -= cameraScroll.x / Tile.TILE_SIZE_X;
//            cameraPosition.y -= cameraScroll.y / Tile.TILE_SIZE_Y;
//
//            cameraScroll.x = 0;
//            cameraScroll.y = 0;
//
//        } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
//            lastTouchPosition.x = (int) event.getX();
//            lastTouchPosition.y = (int) event.getY();
//
//            accScroll.x = 0;
//            accScroll.y = 0;
//
//            lastTouchPosition.x = (int) event.getX();
//            lastTouchPosition.y = (int) event.getY();
//
//        }
//
//        if (cameraPosition.x < -(currentLevel.getScreenWidth() * 0.85f / Constants.BASETILEWIDTH))
//            cameraPosition.x = -(currentLevel.getScreenWidth() * 0.85f / Constants.BASETILEWIDTH);
//
//        if (cameraPosition.y < -(currentLevel.getScreenHeight() * 0.85f / Constants.BASETILEHEIGHT))
//            cameraPosition.y = -(currentLevel.getScreenHeight() * 0.85f / Constants.BASETILEHEIGHT);
//
//        if (cameraPosition.x > 0.85f * currentLevel.getScreenWidth()
//                / Constants.BASETILEWIDTH)
//            cameraPosition.x = 0.85f * currentLevel.getScreenWidth()
//                    / Constants.BASETILEWIDTH;
//
//        if (cameraPosition.y > 0.85f * currentLevel.getScreenHeight()
//                / Constants.BASETILEHEIGHT)
//            cameraPosition.y = 0.85f * currentLevel.getScreenHeight()
//                    / Constants.BASETILEHEIGHT;
//
//        postInvalidate();
//
		return true;

	}

	public void centerOn(Actor actor) {

		cameraPosition = actor.getPosition();
	}


//    public void run() {
//
//        while (running) {
//
//            if (playing) {
//
//                try {
//                    Thread.sleep(100);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//                for (int c = 0; c < updatables.size(); ++c) {
//                    updatables.get(c).update();
//                }
//
//                postInvalidate();
//
//            }
//        }
//    }

	public void handleKeys(boolean[] keymap) {

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
		this.playing = isPlaying;
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
