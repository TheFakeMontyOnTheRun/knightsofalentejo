package br.odb.menu;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Presentation;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaRouter;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import br.odb.GL2JNILib;
import br.odb.droidlib.Updatable;
import br.odb.knights.Actor;
import br.odb.knights.GameScreenView;
import br.odb.knights.GameView;
import br.odb.knights.GameViewGLES2;
import br.odb.knights.Knight;
import br.odb.knights.R;

public class GameActivity extends Activity implements Updatable, OnItemSelectedListener, OnClickListener {

	private GameScreenView view;
	private Spinner spinner;
	private MediaRouter mMediaRouter;
	MediaRouter.RouteInfo mRouteInfo = null;
	private AssetManager assets;
	private int level;

	@Override
	protected void onPause() {
		super.onPause();
		view.onPause();
		if (view instanceof GameViewGLES2) {
			synchronized (((GameViewGLES2) view).renderingLock) {
				GL2JNILib.onDestroy();
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (view instanceof GameViewGLES2) {
			synchronized (((GameViewGLES2) view).renderingLock) {
				if (view instanceof GameViewGLES2) {
					loadTextures();
				}

				GL2JNILib.onCreate(assets);
			}
		}
		view.onResume();
	}

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);


		this.level = getIntent().getIntExtra(KnightsOfAlentejoSplashActivity.MAPKEY_LEVEL_TO_PLAY, 0);
		boolean playIn3D = getIntent().getBooleanExtra(KnightsOfAlentejoSplashActivity.MAPKEY_PLAY_IN_3D, true);

		setContentView(playIn3D ? R.layout.game3d_layout : R.layout.game_layout);

		boolean haveControllerPlugged = getGameControllerIds().size() > 0;

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {


			if (haveControllerPlugged && getActionBar() != null) {
				getActionBar().setDisplayHomeAsUpEnabled(false);
				getActionBar().hide();
			}
		}

		spinner = (Spinner) findViewById(R.id.spinner1);

		findViewById(R.id.btnUp).setOnClickListener(this);
		findViewById(R.id.btnDown).setOnClickListener(this);
		findViewById(R.id.btnLeft).setOnClickListener(this);
		findViewById(R.id.btnRight).setOnClickListener(this);


		findViewById(R.id.btnUp).setSoundEffectsEnabled(false);
		findViewById(R.id.btnDown).setSoundEffectsEnabled(false);
		findViewById(R.id.btnLeft).setSoundEffectsEnabled(false);
		findViewById(R.id.btnRight).setSoundEffectsEnabled(false);


		findViewById(R.id.btnUp).setVisibility(haveControllerPlugged ? View.GONE : View.VISIBLE);
		findViewById(R.id.btnDown).setVisibility(haveControllerPlugged ? View.GONE : View.VISIBLE);
		findViewById(R.id.btnLeft).setVisibility(haveControllerPlugged ? View.GONE : View.VISIBLE);
		findViewById(R.id.btnRight).setVisibility(haveControllerPlugged ? View.GONE : View.VISIBLE);


		spinner.setOnItemSelectedListener(this);
		view = (GameScreenView) findViewById(R.id.gameView1);


		if (level > 0) {
			Toast.makeText(this, getString(R.string.level_greeting_others), Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, getString(R.string.level_greeting_0), Toast.LENGTH_SHORT).show();
		}

		view.init(this, this, level);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
			mMediaRouter = (MediaRouter) getSystemService(Context.MEDIA_ROUTER_SERVICE);

			mRouteInfo = mMediaRouter.getSelectedRoute(MediaRouter.ROUTE_TYPE_LIVE_VIDEO);

			if (mRouteInfo != null) {

				Display presentationDisplay = mRouteInfo.getPresentationDisplay();

				if (presentationDisplay != null) {
					view.getParentViewManager().removeView((View) view);
					Presentation presentation = new GamePresentation(this, presentationDisplay, view);
					presentation.show();
				}
			}
		}

		view.setIsPlaying(true);
	}

	private void loadTextures() {
		try {
			assets = getAssets();

			Bitmap[] bitmaps = loadBitmaps(assets, new String[]{
					"grass.png", //0
					"bricks.png", //1
					"arch.png", //2
					"bars.png", //3
					"begin.png", //4
					"exit.png", //5
					"bricks_blood.png", //6
					"bricks_candles.png", //7
					"boss.png", //8
					"bull.png", //9
					"cuco.png", //10
					"demon.png", //11
					"falcon.png", //12
					"lady.png", //13
					"turtle.png",//14
					"cursor.png", //15
					"top.png"}); //16
			GL2JNILib.setTextures(bitmaps);
		} catch (IOException e) {
		}
	}

	private Bitmap[] loadBitmaps(AssetManager assets, String[] filenames) throws IOException {
		Bitmap[] toReturn = new Bitmap[filenames.length];

		for (int i = 0; i < filenames.length; i++) {
			toReturn[i] = BitmapFactory.decodeStream(assets.open(filenames[i]));
		}

		return toReturn;
	}

	private List<Integer> getGameControllerIds() {
		List<Integer> gameControllerDeviceIds = new ArrayList<Integer>();

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
	public void onDetachedFromWindow() {
		view.stopRunning();
		super.onDetachedFromWindow();
	}


	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		view.setIsPlaying(hasFocus);
	}

	@Override
	public void update() {

		Knight[] knights = view.getCurrentLevel().getKnights();

		if (view.getCurrentLevel().getMonsters() == 0 || (knights.length == 0 && view.getExitedKnights() > 0)) {
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putInt(KnightsOfAlentejoSplashActivity.MAPKEY_SUCCESSFUL_LEVEL_COMPLETION, KnightsOfAlentejoSplashActivity.GameOutcome.VICTORY.ordinal());
			bundle.putInt(KnightsOfAlentejoSplashActivity.MAPKEY_LEVEL_TO_PLAY, this.level);
			intent.putExtras(bundle);
			setResult(RESULT_OK, intent);
			view.stopRunning();
			finish();
			return;
		} else if (knights.length == 0) {
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putInt(KnightsOfAlentejoSplashActivity.MAPKEY_SUCCESSFUL_LEVEL_COMPLETION, KnightsOfAlentejoSplashActivity.GameOutcome.DEFEAT.ordinal());
			bundle.putInt( KnightsOfAlentejoSplashActivity.MAPKEY_LEVEL_TO_PLAY, this.level);
			intent.putExtras(bundle);
			setResult(RESULT_OK, intent);
			view.stopRunning();
			finish();
			return;
		}


		spinner.setAdapter(new ArrayAdapter<>(
				this, android.R.layout.simple_spinner_item,
				knights));

		int position = 0;

		for (int c = 0; c < knights.length; ++c) {


			if (knights[c] == view.getSelectedPlayer()) {
				position = c;
				knights[c].visual.setFrame(1);
			} else {
				knights[c].visual.setFrame(knights[c].isAlive() ? 0 : 2);
			}
		}

		spinner.setSelection(position);
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
	                           long arg3) {

		if (view.getSelectedPlayer() == null || !view.getSelectedPlayer().isAlive() || ((Knight) view.getSelectedPlayer()).hasExited) {
			view.setSelectedPlayer(view.getCurrentLevel().getKnights()[0]);
			spinner.setSelection(0);
		} else {
			view.setSelectedPlayer((Actor) spinner.getSelectedItem());
		}

		view.setSelectedTile(view.getCurrentLevel().getTile(view.getSelectedPlayer().getPosition()));
		view.centerOn(view.getSelectedPlayer());

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}

	@Override
	public void onClick(View v) {

		boolean[] keyMap = view.getKeyMap();

		for (int c = 0; c < keyMap.length; ++c) {
			keyMap[c] = false;
		}

		switch (v.getId()) {
			case R.id.btnUp:
				keyMap[GameView.KB.UP.ordinal()] = true;
				break;
			case R.id.btnDown:
				keyMap[GameView.KB.DOWN.ordinal()] = true;
				break;
			case R.id.btnLeft:
				keyMap[GameView.KB.LEFT.ordinal()] = true;
				break;
			case R.id.btnRight:
				keyMap[GameView.KB.RIGHT.ordinal()] = true;
				break;
		}

		if (view.getSelectedPlayer() != null && view.getSelectedPlayer().visual != null) {
			view.getSelectedPlayer().visual.setFrame(1);
		}

		view.handleKeys(keyMap);

	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		boolean handled = super.onKeyUp(keyCode, event);
		return handled || view.onKeyUp(keyCode, event);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean handled = super.onKeyDown(keyCode, event );

		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		}

		return handled || view.onKeyDown(keyCode, event );
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	private final static class GamePresentation extends Presentation {

		final GameScreenView canvas;

		public GamePresentation(Context context, Display display, GameScreenView gameView) {
			super(context, display);

			this.canvas = gameView;
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView((View) canvas);
		}
	}
}
