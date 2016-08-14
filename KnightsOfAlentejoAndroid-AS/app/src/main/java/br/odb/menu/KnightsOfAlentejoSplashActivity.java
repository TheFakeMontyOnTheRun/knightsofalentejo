package br.odb.menu;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import br.odb.knights.GameConfigurations;
import br.odb.knights.GameLevelLoader;
import br.odb.knights.GameSession;
import br.odb.knights.R;

public class KnightsOfAlentejoSplashActivity extends Activity {

    public static final String MAPKEY_SUCCESSFUL_LEVEL_OUTCOME = "outcome";
    public static final String MAPKEY_SUCCESSFUL_LEVEL_COMPLETION = "good";
    public static final String MAPKEY_LEVEL_TO_PLAY = "level";
	private static final int PLAY_GAME_REQUEST_CODE = 1;

	public enum GameOutcome { UNDEFINED, VICTORY, DEFEAT }

	private SoundManager mSoundManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
	    Typeface font = Typeface.createFromAsset(getAssets(), "fonts/MedievalSharp.ttf");
	    ( (TextView)findViewById(R.id.tvTitle) ).setTypeface( font );

	    mSoundManager = new SoundManager( getApplicationContext() );

        findViewById(R.id.btStart).setOnClickListener(new OnClickListener() {
	        @Override
	        public void onClick(View v) {
		        playNextLevel(0);
	        }
        });
        findViewById(R.id.btnCredits).setOnClickListener(new OnClickListener() {
	        @Override
	        public void onClick(View v) {
		        showCredits();
	        }
        });
        findViewById(R.id.btnHowToPlay).setOnClickListener(new OnClickListener() {
	        @Override
	        public void onClick(View v) {
		        showHowToPlay();
	        }
        });

	    mSoundManager.playMusic( R.raw.canto_rg );
    }

	private void onLevelEnded(int levelPlayed, GameOutcome outcome) {
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

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == PLAY_GAME_REQUEST_CODE && data != null) {
			Bundle bundle = data.getExtras();
			GameOutcome outcome = GameOutcome.values()[bundle.getInt(MAPKEY_SUCCESSFUL_LEVEL_COMPLETION)];
			int levelPlayed = data.getIntExtra(MAPKEY_LEVEL_TO_PLAY, 0);
			onLevelEnded( levelPlayed, outcome );
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

		int score = 0;
		GameSession session = GameConfigurations.getInstance().getCurrentGameSession();
		if ( session != null ) {
			score = session.getScore();
		}

		if ( levelToPlay == 0 ) {
			score = 0;
		}

		GameConfigurations.getInstance().startNewSession(score);
		Intent intent = new Intent(getBaseContext(), GameActivity.class);
		intent.putExtra(MAPKEY_LEVEL_TO_PLAY, levelToPlay);
		startActivityForResult(intent, PLAY_GAME_REQUEST_CODE);
	}

    private void showGameOver() {
        Intent intent = new Intent(this, ShowOutcomeActivity.class);
        intent.putExtra(MAPKEY_SUCCESSFUL_LEVEL_OUTCOME, GameOutcome.DEFEAT.toString());
        this.startActivity(intent);
    }

    private void showGameEnding() {
        Intent intent = new Intent(this, ShowOutcomeActivity.class);
        intent.putExtra(MAPKEY_SUCCESSFUL_LEVEL_OUTCOME, GameOutcome.VICTORY.toString());
        this.startActivity(intent);
    }

	@Override
	protected void onPause() {
		mSoundManager.stop();
		super.onPause();
	}
}