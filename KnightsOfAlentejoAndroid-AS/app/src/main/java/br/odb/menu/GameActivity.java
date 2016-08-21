package br.odb.menu;

import android.app.Activity;
import android.app.Presentation;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.MediaRouter;
import android.os.Bundle;
import android.view.Display;
import android.view.InputDevice;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.odb.GL2JNILib;
import br.odb.KnightSelectionAdapter;
import br.odb.droidlib.Updatable;
import br.odb.droidlib.Vector2;
import br.odb.knights.Actor;
import br.odb.knights.BullKnight;
import br.odb.knights.EagleKnight;
import br.odb.knights.GameConfigurations;
import br.odb.knights.GameLevel;
import br.odb.knights.GameViewGLES2;
import br.odb.knights.Knight;
import br.odb.knights.R;
import br.odb.knights.TurtleKnight;

public class GameActivity extends Activity implements Updatable, OnItemSelectedListener, OnClickListener {

	private Bitmap[] directionIcons;

	Map<String, String> localizedKnightsNames = new HashMap<>();
	Map<String, Bitmap> bitmapForKnights = new HashMap<>();

	public enum Direction {
		N( 0, -1 ),
		E( 1 , 0),
		S( 0, 1 ),
		W( -1, 0);

		private final Vector2 offsetVector = new Vector2(0,0);

		Direction( int x, int y ) {
			offsetVector.x = x;
			offsetVector.y = y;
		}

		public Vector2 getOffsetVector() {
			return offsetVector;
		}
	}

	private GameViewGLES2 view;
	private Spinner spinner;
	private AssetManager assets;
	private int level;
	private TextView scoreView;
	boolean mHaveController;
	private Bitmap attackIcon;
	private Bitmap forbiddenIcon;

	@Override
	protected void onPause() {
		super.onPause();
		view.onPause();
			synchronized (view.renderingLock) {
				GL2JNILib.onDestroy();
			}

	}

	@Override
	protected void onResume() {
		super.onResume();

			synchronized (view.renderingLock) {

					loadTextures();


				GL2JNILib.onCreate(assets);
			}

		view.onResume();

		enterImmersiveMode();
	}

	private void enterImmersiveMode() {
		getWindow().getDecorView().setSystemUiVisibility(
				 View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
						| View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
						| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
						| View.SYSTEM_UI_FLAG_FULLSCREEN
						| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
	}

	/**
	 * Called when the activity is first created.
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);


		this.level = getIntent().getIntExtra(KnightsOfAlentejoSplashActivity.MAPKEY_LEVEL_TO_PLAY, 0);

		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);




		mHaveController = getGameControllerIds().size() > 0;

		if (mHaveController ) {
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.game3dcontroller_layout);
		} else {
			setContentView(R.layout.game3d_layout);
		}


		spinner = (Spinner) findViewById(R.id.spinner1);

		if ( !mHaveController ) {
			findViewById(R.id.btnUp).setOnClickListener(this);
			findViewById(R.id.btnDown).setOnClickListener(this);
			findViewById(R.id.btnLeft).setOnClickListener(this);
			findViewById(R.id.btnRight).setOnClickListener(this);


			findViewById(R.id.btnUp).setSoundEffectsEnabled(false);
			findViewById(R.id.btnDown).setSoundEffectsEnabled(false);
			findViewById(R.id.btnLeft).setSoundEffectsEnabled(false);
			findViewById(R.id.btnRight).setSoundEffectsEnabled(false);
		}

		spinner.setOnItemSelectedListener(this);
		view = (GameViewGLES2) findViewById(R.id.gameView1);
		scoreView = (TextView) findViewById(R.id.tvScore);


		if (level > 0) {
			Toast.makeText(this, getString(R.string.level_greeting_others), Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(this, getString(R.string.level_greeting_0), Toast.LENGTH_SHORT).show();
		}

		view.init(this, this, level);


		MediaRouter mMediaRouter = (MediaRouter) getSystemService(Context.MEDIA_ROUTER_SERVICE);
		MediaRouter.RouteInfo mRouteInfo = mMediaRouter.getSelectedRoute(MediaRouter.ROUTE_TYPE_LIVE_VIDEO);

		if (mRouteInfo != null) {

			Display presentationDisplay = mRouteInfo.getPresentationDisplay();

			if (presentationDisplay != null) {
				view.getParentViewManager().removeView(view);
				Presentation presentation = new GamePresentation(this, presentationDisplay, view);
				presentation.show();
			}
		}


		attackIcon = BitmapFactory.decodeResource(getResources(), R.drawable.attack );
		forbiddenIcon = BitmapFactory.decodeResource(getResources(), R.drawable.noway);

		directionIcons = new Bitmap[] {
				BitmapFactory.decodeResource(getResources(), R.drawable.down),
				BitmapFactory.decodeResource(getResources(), R.drawable.left),
				BitmapFactory.decodeResource(getResources(), R.drawable.up),
				BitmapFactory.decodeResource(getResources(), R.drawable.right)
		};

		localizedKnightsNames.put( new BullKnight().getChar(), getResources().getText( R.string.bull_knight ).toString() );
		localizedKnightsNames.put( new TurtleKnight().getChar(), getResources().getText( R.string.turtle_knight ).toString() );
		localizedKnightsNames.put( new EagleKnight().getChar(), getResources().getText( R.string.falcon_knight ).toString() );

		bitmapForKnights.put( new BullKnight().getChar(),  BitmapFactory.decodeResource(getResources(), R.drawable.bull0));
		bitmapForKnights.put( new TurtleKnight().getChar(),BitmapFactory.decodeResource(getResources(), R.drawable.turtle0));
		bitmapForKnights.put( new EagleKnight().getChar(),BitmapFactory.decodeResource(getResources(), R.drawable.falcon0));


		view.setIsPlaying(true);
		view.selectDefaultKnight();

		update();
	}

	private void loadTextures() {
		try {
			assets = getAssets();

			Bitmap[] bitmaps;

			bitmaps = loadBitmaps(assets, new String[]{
					"grass.png", //none
					(this.level > 0 ? "stonefloor.png" : "grass.png"),
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
					(this.level > 0 ? "stoneshadow.png" : "shadow.png"),
					(this.level > 0 ? "stonecursorgood.png" : "cursorgood0.png"),
					"cursorgood1.png",
					"cursorgood2.png",
					(this.level > 0 ? "stonecursorbad.png" : "cursorbad0.png"),
					"cursorbad1.png",
					"cursorbad2.png",
					(this.level > 0 ? "stoneceiling.png" : "ceiling.png"),
					"ceilingdoor.png",
					"ceilingbegin.png",
					"ceilingend.png",
					"splat0.png",
					"splat1.png",
					"splat2.png",
					"ceilingbars.png",
				});
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
		List<Integer> gameControllerDeviceIds = new ArrayList<>();



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

		for (int c = 0; c < knights.length; ++c) {
			if (knights[c] == view.getSelectedPlayer()) {
				knights[c].visual.setFrame(1);
			} else {
				knights[c].visual.setFrame(knights[c].isAlive() ? 0 : 2);
			}
		}

		updateSpinner(knights);

		if ( !mHaveController ) {
			updateArrowKeys();
		}


		scoreView.setText("Score: " + GameConfigurations.getInstance().getCurrentGameSession().getScore());
	}

	private void updateSpinner(Knight[] knights) {
		
		int position = 0;

		for (int c = 0; c < knights.length; ++c) {
			if (knights[c] == view.getSelectedPlayer()) {
				position = c;
			}
		}

		spinner.setAdapter(new KnightSelectionAdapter(
				this, R.layout.knightitem,
				knights, localizedKnightsNames, bitmapForKnights));
		spinner.setSelection(position);
	}

	private void updateArrowKeys() {
		Actor actor = view.getSelectedPlayer();
		GameLevel level = view.getCurrentLevel();

		if ( actor != null ) {
			( (ImageButton)findViewById(R.id.btnUp) ).setImageBitmap( getIconFor( actor, level, Direction.N ) );
			( (ImageButton)findViewById(R.id.btnRight) ).setImageBitmap( getIconFor( actor, level, Direction.E ) );
			( (ImageButton)findViewById(R.id.btnDown) ).setImageBitmap( getIconFor( actor, level, Direction.S ) );
			( (ImageButton)findViewById(R.id.btnLeft) ).setImageBitmap( getIconFor( actor, level, Direction.W ) );

			findViewById(R.id.btnUp).setEnabled(level.canMove( actor, Direction.N ));
			findViewById(R.id.btnRight).setEnabled(level.canMove( actor, Direction.E ));
			findViewById(R.id.btnDown).setEnabled(level.canMove( actor, Direction.S ));
			findViewById(R.id.btnLeft).setEnabled(level.canMove( actor, Direction.W ));

			findViewById(R.id.btnUp).setAlpha(level.canMove( actor, Direction.N ) ? 1.0f : 0.25f );
			findViewById(R.id.btnRight).setAlpha(level.canMove( actor, Direction.E ) ? 1.0f : 0.25f );
			findViewById(R.id.btnDown).setAlpha(level.canMove( actor, Direction.S ) ? 1.0f : 0.25f );
			findViewById(R.id.btnLeft).setAlpha(level.canMove( actor, Direction.W ) ? 1.0f : 0.25f );

			if ( level.canAttack( actor, Direction.N ) ) {
				((ImageButton) findViewById(R.id.btnUp)).getDrawable().setColorFilter( Color.argb(255, 225, 0, 0), PorterDuff.Mode.SRC_ATOP );
			}

			if ( level.canAttack( actor, Direction.S ) ) {
				((ImageButton) findViewById(R.id.btnDown)).getDrawable().setColorFilter( Color.argb(255, 225, 0, 0), PorterDuff.Mode.SRC_ATOP );
			}

			if ( level.canAttack( actor, Direction.W ) ) {
				((ImageButton) findViewById(R.id.btnLeft)).getDrawable().setColorFilter( Color.argb(255, 225, 0, 0), PorterDuff.Mode.SRC_ATOP );
			}

			if ( level.canAttack( actor, Direction.E ) ) {
				((ImageButton) findViewById(R.id.btnRight)).getDrawable().setColorFilter( Color.argb(255, 225, 0, 0), PorterDuff.Mode.SRC_ATOP );
			}

		}
	}

	private Bitmap getIconFor(Actor actor, GameLevel level, Direction d) {
		Bitmap toReturn = directionIcons[ d.ordinal() ];
		return toReturn;
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
	                           long arg3) {

		if ( view.getCurrentLevel().getKnights().length <= 0 ) {
			return;
		}

		if (view.getSelectedPlayer() == null || !view.getSelectedPlayer().isAlive() || ((Knight) view.getSelectedPlayer()).hasExited) {
			view.setSelectedPlayer(view.getCurrentLevel().getKnights()[0]);
			spinner.setSelection(0);
		} else {
			view.setSelectedPlayer((Actor) spinner.getSelectedItem());
		}

		view.centerOn(view.getSelectedPlayer());
		update();
		enterImmersiveMode();
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
				keyMap[GameViewGLES2.KB.UP.ordinal()] = true;
				break;
			case R.id.btnDown:
				keyMap[GameViewGLES2.KB.DOWN.ordinal()] = true;
				break;
			case R.id.btnLeft:
				keyMap[GameViewGLES2.KB.LEFT.ordinal()] = true;
				break;
			case R.id.btnRight:
				keyMap[GameViewGLES2.KB.RIGHT.ordinal()] = true;
				break;
			case R.id.btnCenter:
				keyMap[GameViewGLES2.KB.CENTER.ordinal()] = true;
				break;

		}

		if (view.getSelectedPlayer() != null ) {
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
}
