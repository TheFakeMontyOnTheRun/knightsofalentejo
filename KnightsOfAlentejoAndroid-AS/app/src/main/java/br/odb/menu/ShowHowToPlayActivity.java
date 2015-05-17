package br.odb.menu;

import android.app.Activity;
import android.os.Bundle;
import br.odb.knights.R;

public class ShowHowToPlayActivity extends Activity{

	byte level = 0;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.howtoplay_layout);

	}

}