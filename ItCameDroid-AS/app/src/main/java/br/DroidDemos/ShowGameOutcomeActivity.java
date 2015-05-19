package br.DroidDemos;

import android.app.Activity;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class ShowGameOutcomeActivity extends Activity implements
		OnClickListener {

	Button btnBack;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_show_game_outcome);
		
		String outcome1 = "Parabéns, você fugiu da caverna!";
		String outcome2 = "Que pena! Você foi derrotado. Fim de jogo!";

		TextView tvOutcome;
		boolean victory = getIntent().getExtras().getString("result")
				.equals("victory");
		tvOutcome = (TextView) findViewById(R.id.tvOutcome);
		tvOutcome.setText(victory ? outcome1 : outcome2);
		tvOutcome.setTextColor( victory ? Color.BLACK : Color.WHITE );
		this.setTitle(victory ? outcome1 : outcome2);

		if ( ItCameView.playSounds ) {
			
			MediaPlayer.create(this, victory ? R.raw.win : R.raw.gameover).start();
		}
		
		btnBack = (Button) findViewById(R.id.btnBack);
		btnBack.setOnClickListener(this);
		ImageView iv = (ImageView) findViewById(R.id.ivOutcome);
		iv.setImageResource(victory ? R.drawable.end_victory
				: R.drawable.end_gameover);
		iv.setAlpha(victory ? 255 : 16);
	}

	public void onClick(View v) {
		finish();
	}
}
