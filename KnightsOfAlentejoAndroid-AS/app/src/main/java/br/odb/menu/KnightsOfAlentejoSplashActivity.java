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

    public static final String MAPKEY_PLAY_IN_3D = "3D";
    public static final String MAPKEY_SUCCESSFUL_LEVEL_OUTCOME = "outcome";
    public static final String MAPKEY_SUCCESSFUL_LEVEL_COMPLETION = "good";
    public static final String MAPKEY_LEVEL_TO_PLAY = "level";
	private static final int PLAY_GAME_REQUEST_CODE = 1;

	public enum GameOutcome { UNDEFINED, VICTORY, DEFEAT };

    SoundManager mSoundManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

	    mSoundManager = new SoundManager( getApplicationContext() );

        findViewById(R.id.btStart).setOnClickListener(this);
        findViewById(R.id.btnCredits).setOnClickListener(this);
        findViewById(R.id.btnHowToPlay).setOnClickListener(this);

	    mSoundManager.playMusic( R.raw.canto_rg );
    }

    @Override
    protected void onPause() {
	    mSoundManager.stop();
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
        startActivityForResult(intent, PLAY_GAME_REQUEST_CODE);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PLAY_GAME_REQUEST_CODE && data != null) {

            GameOutcome outcome = GameOutcome.valueOf(data.getStringExtra(MAPKEY_SUCCESSFUL_LEVEL_COMPLETION));
            int levelPlayed = data.getIntExtra(MAPKEY_LEVEL_TO_PLAY, 0);

            if (outcome == GameOutcome.VICTORY) {

                ++levelPlayed;

                if (levelPlayed > GameLevelLoader.NUMBER_OF_LEVELS) {
                    showGameEnding();
                } else {
                    playNextLevel( levelPlayed );
                }
            } else if (outcome == GameOutcome.DEFEAT) {
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