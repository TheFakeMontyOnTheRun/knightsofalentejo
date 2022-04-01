package br.odb.menu;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import br.odb.knights.R;

public class ShowOutcomeActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.outcome_layout);

        boolean outcomeIsGood = KnightsOfAlentejoSplashActivity.GameOutcome.valueOf( getIntent().getStringExtra( KnightsOfAlentejoSplashActivity.MAPKEY_SUCCESSFUL_LEVEL_OUTCOME) ) == KnightsOfAlentejoSplashActivity.GameOutcome.VICTORY;
        String text = getString( outcomeIsGood ? R.string.outcome_good : R.string.outcome_bad );
        setTitle( text );
        ((TextView) findViewById(R.id.tvOutcome)).setText( text );
        ((TextView) findViewById(R.id.tvOutcome)).setTextColor(outcomeIsGood ? 0xFF00FF00 : 0xFFFF0000);
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/MedievalSharp.ttf");
        ( (TextView)findViewById(R.id.tvOutcome) ).setTypeface( font );

    }
}