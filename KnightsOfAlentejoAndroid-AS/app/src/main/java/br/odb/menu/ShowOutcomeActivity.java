package br.odb.menu;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import br.odb.knights.R;

public class ShowOutcomeActivity extends Activity {


	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.outcome_layout);


		boolean good = getIntent().getBooleanExtra( "good", false );
		( ( TextView ) findViewById( R.id.tvOutcome ) ).setText( good ? "Congratulations! You defeated all demons!" : "Game Over! Too bad, all your knigts were defeated..." );
		( ( TextView ) findViewById( R.id.tvOutcome ) ).setTextColor( good ? 0xFF00FF00 : 0xFFFF0000 );
	}
}