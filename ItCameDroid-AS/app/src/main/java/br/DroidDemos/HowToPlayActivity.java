package br.DroidDemos;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class HowToPlayActivity extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_how_to_play);
		
		findViewById( R.id.btnBack ).setOnClickListener( this );
	}

	@Override
	public void onClick(View arg0) {
		finish();		
	}
}
