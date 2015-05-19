package br.DroidDemos;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;

public class ViewLeaderboardActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view_leaderboard);
		
		findViewById( R.id.btnBackToMenu ).setOnClickListener( this );
		
		WebView wvScores = (WebView) findViewById( R.id.wvScores );
		wvScores.loadUrl( "http://www.montyprojects.com/scoreboard/leaderboard.php?game=3&link=1" );
	}

	@Override
	public void onClick(View v) {
		finish();		
	}
}
