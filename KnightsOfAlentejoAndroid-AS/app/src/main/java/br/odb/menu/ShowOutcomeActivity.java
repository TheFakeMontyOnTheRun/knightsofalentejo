package br.odb.menu;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import br.odb.knights.R;

public class ShowOutcomeActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.outcome_layout);

        boolean outcomeIsGood = KnightsOfAlentejoSplashActivity.GameOutcome.valueOf( getIntent().getStringExtra( KnightsOfAlentejoSplashActivity.MAPKEY_SUCCESSFUL_LEVEL_OUTCOME) ) == KnightsOfAlentejoSplashActivity.GameOutcome.VICTORY;
        ((TextView) findViewById(R.id.tvOutcome)).setText( getString( outcomeIsGood ? R.string.outcome_good : R.string.outcome_bad ) );
        ((TextView) findViewById(R.id.tvOutcome)).setTextColor(outcomeIsGood ? 0xFF00FF00 : 0xFFFF0000);
    }
}