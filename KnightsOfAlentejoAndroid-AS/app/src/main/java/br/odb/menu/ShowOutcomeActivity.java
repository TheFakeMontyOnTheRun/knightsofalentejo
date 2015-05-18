package br.odb.menu;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import br.odb.knights.R;

public class ShowOutcomeActivity extends Activity {


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.outcome_layout);


        boolean good = getIntent().getBooleanExtra( KnightsOfAlentejoSplashActivity.MAPKEY_SUCCESSFUL_LEVEL_OUTCOME, false);
        ((TextView) findViewById(R.id.tvOutcome)).setText( getString( good ? R.string.outcome_good : R.string.outcome_bad ) );
        ((TextView) findViewById(R.id.tvOutcome)).setTextColor(good ? 0xFF00FF00 : 0xFFFF0000);
    }
}