package br.odb.menu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import br.odb.knights.GameConfigurations;
import br.odb.knights.GameLevelLoader;
import br.odb.knights.R;

public class KnightsOfAlentejoSplashActivity extends Activity implements
        OnClickListener {

    public static final String MAPKEY_SUCCESSFUL_LEVEL_OUTCOME = "outcome";
    public static final String MAPKEY_SUCCESSFUL_LEVEL_COMPLETION = "good";
    public static final String MAPKEY_LEVEL_TO_PLAY = "level";
    private volatile int level = 0;

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

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btStart:
                playNextLevel();
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

    private void playNextLevel() {
        GameConfigurations.getInstance().startNewSession();
        Intent intent = new Intent(getBaseContext(), GameActivity.class);
        intent.putExtra(MAPKEY_LEVEL_TO_PLAY, level);
        startActivityForResult(intent, 1);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1 && data != null) {

            int good = data.getIntExtra(MAPKEY_SUCCESSFUL_LEVEL_COMPLETION, 0);


            if (good == 1) {

                ++level;

                if (level > GameLevelLoader.NUMBER_OF_LEVELS) {
                    showGameEnding();
                } else {
                    playNextLevel();
                }
            } else if (good == 2) {
                level = 0;
                showGameOver();
            }
        } else {
            level = 0;
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