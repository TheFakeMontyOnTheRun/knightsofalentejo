package br.odb.menu;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;

import br.odb.knights.GameConfigurations;
import br.odb.knights.GameLevelLoader;
import br.odb.knights.R;

public class KnightsOfAlentejoSplashActivity extends Activity implements
        OnClickListener {

    MediaPlayer music;
    public static final String MAPKEY_PLAY_IN_3D = "3D";
    public static final String MAPKEY_SUCCESSFUL_LEVEL_OUTCOME = "outcome";
    public static final String MAPKEY_SUCCESSFUL_LEVEL_COMPLETION = "good";
    public static final String MAPKEY_LEVEL_TO_PLAY = "level";

    public boolean mayEnableSound() {
        android.media.AudioManager am = (android.media.AudioManager) getSystemService(Context.AUDIO_SERVICE);

        switch (am.getRingerMode()) {
            case android.media.AudioManager.RINGER_MODE_SILENT:
            case android.media.AudioManager.RINGER_MODE_VIBRATE:
                return false;
            case android.media.AudioManager.RINGER_MODE_NORMAL:
                return true;
            default:
                return false;
        }
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        findViewById(R.id.btStart).setOnClickListener(this);
        findViewById(R.id.btnCredits).setOnClickListener(this);
        findViewById(R.id.btnHowToPlay).setOnClickListener(this);

        if ( mayEnableSound() ) {
            music = MediaPlayer.create( this, R.raw.canto_rg );
            music.start();
        }
    }

    @Override
    protected void onPause() {
        if ( music != null ) {
            music.stop();
        }

        super.onPause();


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btStart:
                playNextLevel(0);
                break;
            case R.id.btnCredits:
                showCredits();
                break;
            case R.id.btnHowToPlay:
                showHowToPlay();
                break;
        }
    }

    private void showHowToPlay() {
        Intent intent = new Intent(this, ShowHowToPlayActivity.class);
        startActivity(intent);
    }

    private void showCredits() {
        Intent intent = new Intent(this, ShowCreditsActivity.class);
        startActivity(intent);
    }

    private void playNextLevel(int levelToPlay) {
        boolean playIn3D = ((CheckBox)findViewById(R.id.chkPlayIn3D)).isChecked();
        GameConfigurations.getInstance().startNewSession();
        Intent intent = new Intent(getBaseContext(), GameActivity.class);
        intent.putExtra(MAPKEY_LEVEL_TO_PLAY, levelToPlay);
        intent.putExtra(MAPKEY_PLAY_IN_3D, playIn3D );
        startActivityForResult(intent, 1);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1 && data != null) {

            int good = data.getIntExtra(MAPKEY_SUCCESSFUL_LEVEL_COMPLETION, 0);
            int levelPlayed = data.getIntExtra(MAPKEY_LEVEL_TO_PLAY, 0);

            if (good == 1) {

                ++levelPlayed;

                if (levelPlayed > GameLevelLoader.NUMBER_OF_LEVELS) {
                    showGameEnding();
                } else {
                    playNextLevel( levelPlayed );
                }
            } else if (good == 2) {
                showGameOver();
            }
        }
    }

    private void showGameOver() {
        Intent intent = new Intent(this, ShowOutcomeActivity.class);
        intent.putExtra(MAPKEY_SUCCESSFUL_LEVEL_OUTCOME, false);
        this.startActivity(intent);
    }

    private void showGameEnding() {
        Intent intent = new Intent(this, ShowOutcomeActivity.class);
        intent.putExtra(MAPKEY_SUCCESSFUL_LEVEL_OUTCOME, true);
        this.startActivity(intent);
    }
}