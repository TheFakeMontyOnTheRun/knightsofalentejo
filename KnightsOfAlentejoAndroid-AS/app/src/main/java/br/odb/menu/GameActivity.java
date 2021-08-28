package br.odb.menu;

import android.app.Activity;
import android.app.Presentation;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.media.MediaRouter;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageButton;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.odb.KnightSelectionAdapter;
import br.odb.droidlib.Vector2;
import br.odb.knights.Actor;
import br.odb.knights.BullKnight;
import br.odb.knights.EagleKnight;
import br.odb.knights.GameConfigurations;
import br.odb.knights.GameLevel;
import br.odb.knights.GameSession;
import br.odb.knights.GameViewGLES2;
import br.odb.knights.Knight;
import br.odb.knights.R;
import br.odb.knights.TurtleKnight;

public class GameActivity extends Activity implements OnItemSelectedListener, OnClickListener {

	public interface GameDelegate {
		void onTurnEnded();

		void onGameOver();

		void onLevelFinished();

		void onGameStarted();

		void onFatalError();

		void onKnightChanged();
	}

	GameDelegate gameDelegate = new GameDelegate() {
		@Override
		public void onTurnEnded() {
			List<Knight> listOfKnightOnTheLevel = getListOfAvailableKnights();

			boolean thereAreNoAliveKnightsOnTheLevel = listOfKnightOnTheLevel.isEmpty();

			if (hasPlayerKilledAllMonsters() || (thereAreNoAliveKnightsOnTheLevel && hasAnyKnightExited())) {
				proceedToNextLevel();
				return;
			} else if (thereAreNoAliveKnightsOnTheLevel && !hasAnyKnightExited()) {
				endGameAsDefeat();
				return;
			}

			updateUI( listOfKnightOnTheLevel );
		}

		@Override
		public void onGameOver() {
			updateUI(getListOfAvailableKnights());
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putInt(KnightsOfAlentejoSplashActivity.MAPKEY_SUCCESSFUL_LEVEL_COMPLETION, KnightsOfAlentejoSplashActivity.GameOutcome.DEFEAT.ordinal());
			bundle.putInt(KnightsOfAlentejoSplashActivity.MAPKEY_LEVEL_TO_PLAY, floorNumber);
			intent.putExtras(bundle);
			final Intent finalIntent = intent;
			view.fadeOut();
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					setResult(RESULT_OK, finalIntent);
					view.stopRunning();
					finish();
					overridePendingTransition(R.anim.hold, R.anim.fade_out);
				}
			}, 1000);
		}

		@Override
		public void onLevelFinished() {
			Intent intent = new Intent();
			Bundle bundle = new Bundle();
			bundle.putInt(KnightsOfAlentejoSplashActivity.MAPKEY_SUCCESSFUL_LEVEL_COMPLETION, KnightsOfAlentejoSplashActivity.GameOutcome.VICTORY.ordinal());
			bundle.putInt(KnightsOfAlentejoSplashActivity.MAPKEY_LEVEL_TO_PLAY, floorNumber);
			intent.putExtras(bundle);
			final Intent finalIntent = intent;
			view.fadeOut();
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					setResult(RESULT_OK, finalIntent);
					view.stopRunning();
					finish();
					overridePendingTransition(R.anim.hold, R.anim.fade_out);
				}
			}, 1000);
		}

		@Override
		public void onGameStarted() {
			view.getCurrentLevel().selectDefaultKnight();
			List<Knight> listOfKnightOnTheLevel = getListOfAvailableKnights();
			updateUI( listOfKnightOnTheLevel );
		}

		@Override
		public void onFatalError() {
			finish();
			overridePendingTransition(R.anim.hold, R.anim.fade_out);
		}

		@Override
		public void onKnightChanged() {
			List<Knight> listOfKnightOnTheLevel = getListOfAvailableKnights();
			updateUI( listOfKnightOnTheLevel );
		}
	};


	public enum Direction {
		N(0, -1),
		E(1, 0),
		S(0, 1),
		W(-1, 0);

		private final Vector2 offsetVector = new Vector2(0, 0);

		Direction(int x, int y) {
			offsetVector.x = x;
			offsetVector.y = y;
		}

		public Vector2 getOffsetVector() {
			return offsetVector;
		}
	}

	private final static class GamePresentation extends Presentation {

		final GameViewGLES2 canvas;

		public GamePresentation(Context context, Display display, GameViewGLES2 gameView) {
			super(context, display);

			this.canvas = gameView;
		}

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(canvas);
		}
	}

	private int floorNumber;

	private final Map<String, String> localizedKnightsNames = new HashMap<>();
	private final Map<String, Bitmap> bitmapForKnights = new HashMap<>();
	private GameViewGLES2 view;
	private Spinner spinner;
	private ImageButton mToggleCameraButton;
	private boolean mHaveController;

	//basic Activity structure
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		setContentView(R.layout.game3d_layout);

		spinner = (Spinner) findViewById(R.id.spinner1);
		view = (GameViewGLES2) findViewById(R.id.gameView1);
		mToggleCameraButton = (ImageButton) findViewById(R.id.btnToggleCamera);
		spinner.setOnItemSelectedListener(this);

		configureUiForInputDevice();
		prepareAssetsForKnightSelectionSpinner();

		this.floorNumber = getIntent().getIntExtra(KnightsOfAlentejoSplashActivity.MAPKEY_LEVEL_TO_PLAY, 0);

		if (hasSavedGameSession(savedInstanceState)) {
			restoreGameSession(savedInstanceState);
		} else {
			greetPlayerOnLevelProgress();
		}

		useBestRouteForGameplayPresentation();
		view.init(this, gameDelegate, floorNumber);
	}

	@Override
	protected void onPause() {
		super.onPause();
		view.onPause();
		synchronized (view.renderingLock) {
			view.onDestroy();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		synchronized (view.renderingLock) {
			view.onCreate(getAssets());
		}

		gameDelegate.onGameStarted();
		view.onResume();
		enterImmersiveMode();
	}

	private boolean hasPlayerKilledAllMonsters() {
		return view.getCurrentLevel().getMonsters() == 0;
	}

	private boolean hasAnyKnightExited() {
		return view.getExitedKnights() > 0;
	}

	private void proceedToNextLevel() {
		gameDelegate.onLevelFinished();
	}

	private void endGameAsDefeat() {
		gameDelegate.onGameOver();
	}

	private boolean hasSavedGameSession(Bundle savedInstanceState) {
		return savedInstanceState != null && savedInstanceState.getSerializable("Level") != null;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {

		outState.putSerializable("Level", view.getCurrentLevel());
		super.onSaveInstanceState(outState);
	}

	private void restoreGameSession(Bundle savedInstanceState) {
		GameLevel level = (GameLevel) savedInstanceState.getSerializable("Level");
		level.setDelegates( gameDelegate, view.getRenderingDelegate() );
		GameSession configuration = GameConfigurations.getInstance().getCurrentGameSession();
		configuration.restoreFromLevel(level);
	}

	//presentation and interaction

	private void enterImmersiveMode() {
//		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//				WindowManager.LayoutParams.FLAG_FULLSCREEN);
//
//		getWindow().getDecorView().setSystemUiVisibility(
//				View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//						| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//						| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//						| View.SYSTEM_UI_FLAG_FULLSCREEN
//						| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
	}

	private void configureUiForInputDevice() {
		mHaveController = hasGamepad() || !hasTouchscreen() || hasPhysicalKeyboard();

		if (!mHaveController) {
			bindVisualKeypadToGame();
			muteVisualKeypad();
		} else {
			hideVisualKeypad();
		}
	}

	public void toggleCamera() {
		view.toggleCamera();
		mToggleCameraButton.setImageResource( view.isOnBirdView() ? R.drawable.anilar : R.drawable.cross);
	}

	private void updateSpinner(List<Knight> knights) {
		Typeface font = Typeface.createFromAsset(getAssets(), "fonts/MedievalSharp.ttf");
		KnightSelectionAdapter adapter = new KnightSelectionAdapter(
				this,
				knights.toArray(new Knight[knights.size()]), localizedKnightsNames, bitmapForKnights, font);
		spinner.setAdapter(adapter);
	}

	private boolean hasGamepad() {
		return getGameControllerIds().size() > 0;
	}

	private void greetPlayerOnLevelProgress() {
		if (floorNumber > 0) {
			view.displayLevelAdvanceMessage();
		} else {
			view.displayGreetingMessage();
		}
	}

	private void useBestRouteForGameplayPresentation() {
		MediaRouter.RouteInfo mRouteInfo = findSecundaryDisplayRouter();

		if (mRouteInfo != null) {
			Display presentationDisplay = mRouteInfo.getPresentationDisplay();
			if (presentationDisplay != null) {
				useSecundaryDisplayForGameplayPresentation(presentationDisplay);
			}
		}
	}

	private void muteVisualKeypad() {
		findViewById(R.id.btnUp).setSoundEffectsEnabled(false);
		findViewById(R.id.btnDown).setSoundEffectsEnabled(false);
		findViewById(R.id.btnLeft).setSoundEffectsEnabled(false);
		findViewById(R.id.btnRight).setSoundEffectsEnabled(false);
		mToggleCameraButton.setSoundEffectsEnabled(false);
	}

	private void bindVisualKeypadToGame() {
		findViewById(R.id.btnUp).setOnClickListener(this);
		findViewById(R.id.btnDown).setOnClickListener(this);
		findViewById(R.id.btnLeft).setOnClickListener(this);
		findViewById(R.id.btnRight).setOnClickListener(this);
		mToggleCameraButton.setOnClickListener(this);
	}

	private void hideVisualKeypad() {
		findViewById(R.id.llScreenControllers).setVisibility(View.GONE);
	}

	private void prepareAssetsForKnightSelectionSpinner() {
		localizedKnightsNames.put(new BullKnight().getChar(), getResources().getText(R.string.bull_knight).toString());
		localizedKnightsNames.put(new TurtleKnight().getChar(), getResources().getText(R.string.turtle_knight).toString());
		localizedKnightsNames.put(new EagleKnight().getChar(), getResources().getText(R.string.falcon_knight).toString());
		bitmapForKnights.put(new BullKnight().getChar(), BitmapFactory.decodeResource(getResources(), R.drawable.bull0));
		bitmapForKnights.put(new TurtleKnight().getChar(), BitmapFactory.decodeResource(getResources(), R.drawable.turtle0));
		bitmapForKnights.put(new EagleKnight().getChar(), BitmapFactory.decodeResource(getResources(), R.drawable.falcon0));
	}

	private MediaRouter.RouteInfo findSecundaryDisplayRouter() {
		MediaRouter mMediaRouter = (MediaRouter) getSystemService(Context.MEDIA_ROUTER_SERVICE);
		return mMediaRouter.getSelectedRoute(MediaRouter.ROUTE_TYPE_LIVE_VIDEO);
	}

	private void useSecundaryDisplayForGameplayPresentation(Display presentationDisplay) {
		view.getParentViewManager().removeView(view);
		Presentation presentation = new GamePresentation(this, presentationDisplay, view);
		presentation.show();
	}

	private void updateArrowKeys() {
		Actor actor = view.getCurrentLevel().getSelectedPlayer();
		GameLevel level = view.getCurrentLevel();

		if (actor != null) {
			updateKeyForDirectionSituation(actor, level, Direction.N, findViewById(R.id.btnUp));
			updateKeyForDirectionSituation(actor, level, Direction.E, findViewById(R.id.btnRight));
			updateKeyForDirectionSituation(actor, level, Direction.S, findViewById(R.id.btnDown));
			updateKeyForDirectionSituation(actor, level, Direction.W, findViewById(R.id.btnLeft));
		}
	}

	private void updateKeyForDirectionSituation(Actor actor, GameLevel level, Direction direction, View uiElement) {

		boolean enabled = view.isFirstPerson() || level.canMove(actor, direction);
		boolean canAttack = level.canAttack(actor, view.transformMovementToCameraRotation(direction));
		uiElement.setEnabled( enabled );
		uiElement.setAlpha( enabled ? 1.0f : 0.25f);
		((ImageButton) uiElement).getDrawable().setColorFilter( canAttack ? Color.argb(255, 225, 0, 0) : Color.argb(255, 0, 0, 255), PorterDuff.Mode.SRC_ATOP);
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
	                           long arg3) {

		view.getCurrentLevel().setSelectedPlayer((Knight) spinner.getSelectedItem());
		view.centerOn(view.getCurrentLevel().getSelectedPlayer());
		view.setNeedsUpdate();
		updateArrowKeys();
		enterImmersiveMode();
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}

	private boolean hasTouchscreen() {
		return getPackageManager().hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN);
	}

	private boolean hasPhysicalKeyboard() {
		return getResources().getConfiguration().keyboard != Configuration.KEYBOARD_NOKEYS;
	}

	private List<Integer> getGameControllerIds() {
		List<Integer> gameControllerDeviceIds = new ArrayList<>();

		int[] deviceIds = InputDevice.getDeviceIds();
		for (int deviceId : deviceIds) {
			InputDevice dev = InputDevice.getDevice(deviceId);
			int sources = dev.getSources();

			if (((sources & InputDevice.SOURCE_GAMEPAD) == InputDevice.SOURCE_GAMEPAD)
					|| ((sources & InputDevice.SOURCE_JOYSTICK)
					== InputDevice.SOURCE_JOYSTICK)) {

				if (!gameControllerDeviceIds.contains(deviceId)) {
					gameControllerDeviceIds.add(deviceId);
				}
			}
		}

		return gameControllerDeviceIds;
	}

	@Override
	public void onClick(View v) {

		GameViewGLES2.KB key = null;

		switch (v.getId()) {
			case R.id.btnUp:
				key = view.transformMovementToCameraRotation(GameViewGLES2.KB.UP);
				break;
			case R.id.btnDown:
				key = view.transformMovementToCameraRotation(GameViewGLES2.KB.DOWN);
				break;
			case R.id.btnLeft:
				if ( view.isFirstPerson() ) {
					key = GameViewGLES2.KB.ROTATE_LEFT;
				} else {
					key = GameViewGLES2.KB.LEFT;
				}
				break;
			case R.id.btnRight:
				if ( view.isFirstPerson() ) {
					key = GameViewGLES2.KB.ROTATE_RIGHT;
				} else {
					key = GameViewGLES2.KB.RIGHT;
				}
				break;
			case R.id.btnCenter:
				key = GameViewGLES2.KB.TOGGLE_CAMERA;
				break;
			case R.id.btnToggleCamera:
				key = GameViewGLES2.KB.TOGGLE_CAMERA;
				break;
		}

		if ( key != null ) {
			view.handleCommand(key);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		boolean handled = super.onKeyDown(keyCode, event);

		if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
			finish();
			return true;
		}

		return handled || view.onKeyDown(keyCode, event);
	}

	List<Knight> getListOfAvailableKnights() {
		List<Knight> listOfKnightOnTheLevel = new ArrayList<>();

		Knight selectedKnight = view.getCurrentLevel().getSelectedPlayer();

		if (selectedKnight != null && selectedKnight.isAlive()) {
			listOfKnightOnTheLevel.add(selectedKnight);
		}

		for (Knight k : view.getCurrentLevel().getKnights()) {
			if (!listOfKnightOnTheLevel.contains(k)) {
				if ( k.isAlive() ) {
					listOfKnightOnTheLevel.add(k);
				}
			}
		}

		return listOfKnightOnTheLevel;
	}

	void updateUI( List<Knight> knights ) {
		updateSpinner(knights);

		if (!mHaveController) {
			updateArrowKeys();
		}
	}
}
