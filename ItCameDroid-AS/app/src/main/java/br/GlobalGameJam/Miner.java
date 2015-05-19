package br.GlobalGameJam;

import android.content.Context;
import android.content.res.Resources;
import android.media.MediaPlayer;
import br.DroidDemos.ItCameFromTheCaveActivity;
import br.DroidDemos.ItCameView;
import br.DroidDemos.R;
import br.DroidLib.Animation;
import br.DroidLib.Bitmap;

public class Miner extends Actor {

	Animation[] animation;
	MediaPlayer steps = null;
	
	public Miner(Resources resources, Context context ) {
		super();
		animation = new Animation[4];
		animation[0] = new Animation();
		animation[0].addFrame(new Bitmap(resources, R.drawable.hero0_1));
		animation[0].addFrame(new Bitmap(resources, R.drawable.hero0_2));
		animation[0].addFrame(new Bitmap(resources, R.drawable.hero0_3));
		animation[0].addFrame(new Bitmap(resources, R.drawable.hero0_4));

		animation[1] = new Animation();
		animation[1].addFrame(new Bitmap(resources, R.drawable.hero1_1));
		animation[1].addFrame(new Bitmap(resources, R.drawable.hero1_2));
		animation[1].addFrame(new Bitmap(resources, R.drawable.hero1_3));
		animation[1].addFrame(new Bitmap(resources, R.drawable.hero1_4));

		animation[2] = new Animation();
		animation[2].addFrame(new Bitmap(resources, R.drawable.hero2_1));
		animation[2].addFrame(new Bitmap(resources, R.drawable.hero2_2));
		animation[2].addFrame(new Bitmap(resources, R.drawable.hero2_3));
		animation[2].addFrame(new Bitmap(resources, R.drawable.hero2_4));

		animation[3] = new Animation();
		animation[3].addFrame(new Bitmap(resources, R.drawable.hero3_1));
		animation[3].addFrame(new Bitmap(resources, R.drawable.hero3_2));
		animation[3].addFrame(new Bitmap(resources, R.drawable.hero3_3));
		animation[3].addFrame(new Bitmap(resources, R.drawable.hero3_4));

		currentFrame = animation[super.getDirection()].getFrameReference(0)
				.getBitmap();
		
		if ( ItCameView.playSounds ) {
			
			steps = MediaPlayer.create( context, R.raw.steps );
		}
		// animation.start();

	}

	
	@Override
	public void didMove() {
	
		if ( steps != null ) {
			
			steps.start();
		}
	}

	public void setState(states state) {
		super.setState(state);
		if (state == states.STILL)
			try {
				animation[super.getDirection()].setCurrentFrame(0);
				currentFrame = animation[super.getDirection()]
						.getCurrentFrameReference().getBitmap();
			} catch (Exception e) {

			}

	}

	@Override
	public void tick( long timeInMS ) {
		if (getState() == states.MOVING) {
			
			animation[super.getDirection()].tick( timeInMS );
			
			currentFrame = animation[super.getDirection()]
					.getCurrentFrameReference().getBitmap();
		}
	}

	@Override
	public void touched(Actor actor) {

		if ( actor instanceof Monster ) {
			
			kill();		
		}
	}
}
