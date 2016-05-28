package br.odb.menu;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Presentation;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
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
import android.view.ViewManager;
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
import br.odb.knights.GameView;
import br.odb.knights.GameViewGLES2;
import br.odb.knights.Knight;
import br.odb.knights.R;

public class GameActivity extends Activity implements Updatable, OnItemSelectedListener, OnClickListener {

    private GameViewGLES2 view;
    private Spinner spinner;


    private MediaRouter mMediaRouter;
    private GamePresentation mPresentation;
    private DialogInterface.OnDismissListener mOnDismissListener;
    private MediaRouter.Callback mMediaRouterCallback;
    MediaRouter.RouteInfo mRouteInfo = null;
    private AssetManager assets;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        assets = getAssets();
        GL2JNILib.onCreate(assets);

        try {
            GL2JNILib.setTextures( new Bitmap[]{BitmapFactory.decodeStream(assets.open("grass.png")), BitmapFactory.decodeStream(assets.open("bricks.png") ) });
        } catch (IOException e) {
        }



        setContentView(R.layout.game_layout);

        boolean haveControllerPlugged = getGameControllerIds().size() > 0;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {


            if ( haveControllerPlugged && getActionBar() != null ) {
                getActionBar().setDisplayHomeAsUpEnabled( false );
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


        findViewById(R.id.btnUp).setVisibility( haveControllerPlugged ? View.GONE : View.VISIBLE );
        findViewById(R.id.btnDown).setVisibility(haveControllerPlugged ? View.GONE : View.VISIBLE);
        findViewById(R.id.btnLeft).setVisibility(haveControllerPlugged ? View.GONE : View.VISIBLE);
        findViewById(R.id.btnRight).setVisibility(haveControllerPlugged ? View.GONE : View.VISIBLE);


        spinner.setOnItemSelectedListener(this);
        view = (GameViewGLES2) findViewById(R.id.gameView1);

        int level = getIntent().getIntExtra(KnightsOfAlentejoSplashActivity.MAPKEY_LEVEL_TO_PLAY, 0);

        if (level > 0) {
            Toast.makeText(this, getString( R.string.level_greeting_others), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString( R.string.level_greeting_0), Toast.LENGTH_SHORT).show();
        }

        view.init(this, this, level);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            mMediaRouter = (MediaRouter)getSystemService(Context.MEDIA_ROUTER_SERVICE);

            mRouteInfo = mMediaRouter.getSelectedRoute( MediaRouter.ROUTE_TYPE_LIVE_VIDEO );

            if ( mRouteInfo != null ) {

                Display presentationDisplay = mRouteInfo.getPresentationDisplay();

                if ( presentationDisplay != null ) {
                    ((ViewManager) view.getParent()).removeView( view );
                    Presentation presentation = new GamePresentation( this, presentationDisplay, view );
                    presentation.show();
                }
            }
        }
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
    protected void onDestroy() {
        view.running = false;
        super.onDestroy();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {

        view.playing = hasFocus;
    }

    @Override
    public void update() {

        Knight[] knights = view.currentLevel.getKnights();

        if (view.currentLevel.getMonsters() == 0 || (knights.length == 0 && view.exitedKnights > 0)) {
            Intent intent = new Intent();
            intent.putExtra(KnightsOfAlentejoSplashActivity.MAPKEY_SUCCESSFUL_LEVEL_COMPLETION, 1);
            setResult(RESULT_OK, intent);
            finish();
        }

        if (knights.length == 0) {
            Intent intent = new Intent();
            intent.putExtra(KnightsOfAlentejoSplashActivity.MAPKEY_SUCCESSFUL_LEVEL_COMPLETION, 2);
            setResult(RESULT_OK, intent);
            finish();
        }


        spinner.setAdapter(new ArrayAdapter<Knight>(
                this, android.R.layout.simple_spinner_item,
                knights));

        int position = 0;

        for (int c = 0; c < knights.length; ++c) {


            if (knights[c] == view.selectedPlayer) {
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

        if (view.selectedPlayer == null || !view.selectedPlayer.isAlive() || ((Knight) view.selectedPlayer).hasExited) {
            view.selectedPlayer = view.currentLevel.getKnights()[0];
            spinner.setSelection(0);
        } else {
            view.selectedPlayer = (Actor) spinner.getSelectedItem();
        }

        view.selectedTile = view.currentLevel.getTile(view.selectedPlayer.getPosition());
        view.centerOn(view.selectedPlayer);

    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
    }

    @Override
    public void onClick(View v) {

        boolean[] keyMap = view.keyMap;

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

        if (view.selectedPlayer != null && view.selectedPlayer.visual != null) {
            view.selectedPlayer.visual.setFrame(1);
        }

        view.handleKeys(keyMap);

    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return view.onKeyUp(keyCode, event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if ( event.getKeyCode() == KeyEvent.KEYCODE_BACK ) {
            finish();
        }

        return view.onKeyDown(keyCode, event);
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private final static class GamePresentation extends Presentation {

        final GameViewGLES2 canvas;

        public GamePresentation(Context context, Display display, GameViewGLES2 gameView ) {
            super(context, display);

            this.canvas = gameView;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            // Be sure to call the super class.
            super.onCreate(savedInstanceState);

            // Get the resources for the context of the presentation.
            // Notice that we are getting the resources from the context of the presentation.
            Resources r = getContext().getResources();

            // Inflate the layout.
            setContentView(canvas );
        }
    }
}
