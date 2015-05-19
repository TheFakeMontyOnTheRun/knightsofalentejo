package br.GlobalGameJam;

import android.content.res.Resources;
import android.media.MediaPlayer;
import br.DroidDemos.R;
import br.DroidLib.Animation;
import br.DroidLib.Bitmap;
import br.DroidLib.Constants;

public class Monster extends Actor {
	Actor target;
	long timeToMove;
	private MediaPlayer kill;
	private MediaPlayer walk1;
	int turnToPlaySound;

	public Monster(Resources resources, MediaPlayer walk1, MediaPlayer walk2, MediaPlayer kill ) {
		super();

		animation = new Animation();
		animation.addFrame(new Bitmap(resources, R.drawable.monster_3));
		animation.addFrame(new Bitmap(resources, R.drawable.monster_4));

		currentFrame = animation.getFrameReference(0).getBitmap();
		this.kill = kill;
		this.walk1 = walk1;
		
		this.setDirection(3);
	}


	@Override
	public void tick(long timeInMS) {
		super.tick(timeInMS);

		timeToMove -= timeInMS;

		if (timeToMove < 0) {
			timeToMove = 300;
		} else {
			return;
		}

		float currentX = (getPosition().x / Constants.BASETILEWIDTH);
		float currentY = (getPosition().y / Constants.BASETILEHEIGHT);
		float targetX = (target.getPosition().x / Constants.BASETILEWIDTH);
		float targetY = (target.getPosition().y / Constants.BASETILEHEIGHT);
		float dirX;
		float dirY;

		if (targetX < currentX) {

			dirX = -0.5f;
		} else if (targetX > currentX) {
			dirX = 0.5f;
		} else {
			dirX = 0;
		}

		if (targetY < currentY) {

			dirY = -0.5f;
		} else if (targetY > currentY) {
			dirY = 0.5f;
		} else {
			dirY = 0;
		}

		if (level.mayMoveTo(currentX + dirX, currentY + dirY)) {

			this.move(Constants.BASETILEWIDTH * (dirX),
					Constants.BASETILEHEIGHT * (dirY));
			
			//tenta se desvencilhar do obstáculo.
		} else if (level.mayMoveTo( currentX, currentY + 0.5f ) ) {
			this.move( 0, Constants.BASETILEHEIGHT * ( 0.5f ) );
		} else if (level.mayMoveTo( currentX + 0.5f, currentY ) ) {
			this.move( Constants.BASETILEWIDTH * ( 0.5f ), 0.0f );
		} else if ( level.mayMoveTo( currentX - 0.5f, currentY ) ) {
			this.move( Constants.BASETILEWIDTH * ( - 0.5f ), 0.0f );
		} else if (level.mayMoveTo( currentX, currentY - 0.5f ) ) {
			this.move( 0, Constants.BASETILEHEIGHT * ( 0.5f ) );
		}
	}

	public void setTarget(Miner actor, Level level) {
		this.target = actor;
		this.level = level;
	}

	@Override
	public void touched(Actor actor) {
		if (actor instanceof Monster) {
			
			if ( kill != null ) {
				
				kill.start();
			}
				
			kill();
		}
	}

	@Override
	public void didMove() {
		
//		if ( turnToPlaySound == 0 && walk1 != null && !walk1.isPlaying() ) {
//			
//			if ( walk1 != null ) {
//				
//				walk1.start();
//			}
//			turnToPlaySound = 5;
//		} else {
//			turnToPlaySound--;
//		}
	}
}
