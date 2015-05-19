package br.DroidDemos;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainMenuActivity extends Activity implements OnClickListener {

	static boolean needsReset = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main_menu);
		findViewById( R.id.btnStartGame ).setOnClickListener( this );
		findViewById( R.id.btnHelp ).setOnClickListener( this );
		( ( Button ) findViewById( R.id.btnStartGame ) ).requestFocus();
	}

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
	
	
	@Override
	public void onClick(View v) {
		Intent intent = null;
		
		switch ( v.getId() ) {
		case R.id.btnStartGame:
			needsReset = true;
			intent = new Intent( this, ItCameFromTheCaveActivity.class );
			break;
			
		case R.id.btnHelp:
			intent = new Intent( this, HowToPlayActivity.class );
			break;
//		case R.id.btnLeaderboard:
//			intent = new Intent( this, ViewLeaderboardActivity.class );
//			break;
		}
		this.startActivityForResult( intent, 1 );
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (requestCode == 1) {

		     if(resultCode == RESULT_OK) {
		    	 
				String result = data.getStringExtra("result");
				Intent intent = new Intent( this, ShowGameOutcomeActivity.class );
				
				Bundle bundle = new Bundle();
				bundle.putString( "result", result );
				intent.putExtras( bundle );				
				
				this.startActivity( intent );
		     }
		}

		if (resultCode == RESULT_CANCELED) {

		     //Write your code on no result return 

		}
	}	
}
