package br.GlobalGameJam;

import android.graphics.Rect;
import br.DroidDemos.ItCameView;
import br.DroidLib.Animation;
import br.DroidLib.Bitmap;

public abstract class Actor {

	private Vec2 position;
	private int direction;
	private Rect bounds;
	private states state;
	Bitmap currentFrame;
	protected Animation animation;
	public Level level;
	public boolean killed;
	public boolean visible = true;

	public void tick( long timeInMS ) {
		animation.tick( timeInMS );
		currentFrame = animation.getCurrentFrameReference().getBitmap();
	}
	
	public Vec2 getScreenPosition() {
		Vec2 toReturn = new Vec2();
		
		toReturn.x = (-Level.camera.x + ( ItCameView.viewport.right / 2 ) + getPosition().x - currentFrame.getAndroidBitmap().getWidth() / 2 );
		toReturn.y = (-Level.camera.y + ( ItCameView.viewport.bottom / 2 )	+ getPosition().y - currentFrame.getAndroidBitmap().getHeight() + ( br.DroidLib.Constants.BASETILEHEIGHT  / 2 ) );
		
		return toReturn;
		
	}
	
	
	
	public void draw(android.graphics.Canvas canvas,
			android.graphics.Paint paint) {
		
		if ( !visible ) {
			return;
		}
		
		try {
			Vec2 screenPos = getScreenPosition();
			currentFrame.setX( screenPos.x );
			currentFrame.setY( screenPos.y );
			// currentFrame.setX(-Level.camera.X + super.getPosition().X);
			// currentFrame.setY(-Level.camera.Y + super.getPosition().Y);
			currentFrame.draw(canvas, paint);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public enum states {
		STILL, MOVING, DYING
	};

	public Actor() {
		bounds = new Rect();
		setDirection(0);
		setPosition(new Vec2(0, 0));
		setState(states.STILL);
	}

	public void move(float x, float y) {
		move((int) x, (int) y);
		didMove();
	}

	public void moveTo(float x, float y) {
		moveTo((int) x, (int) y);
		didMove();
	}

	public void move(int x, int y) {
		position.x += x;
		position.y += y;
		didMove();
	}

	public void moveTo(int x, int y) {
		position.x = x;
		position.y = y;
		didMove();
	}

	/**
	 * @param direction
	 *            the direction to set
	 */
	public void setDirection(int direction) {
		this.direction = direction;
	}

	/**
	 * @return the direction
	 */
	public int getDirection() {
		return direction;
	}

	/**
	 * @param position
	 *            the position to set
	 */
	public void setPosition(Vec2 Position) {
		position = Position;
	}

	/**
	 * @return the position
	 */
	public Vec2 getPosition() {
		return position;
	}

	public Rect getBounds() {
		// TODO Auto-generated method stub
		return bounds;
	}

	/**
	 * @param state
	 *            the state to set
	 */
	public void setState(states state) {
		this.state = state;
	}

	/**
	 * @return the state
	 */
	public states getState() {
		return state;
	}

	public void kill() {
//		position = null;
//		bounds = null;
//		state = null;
//		currentFrame = null;
//		animation = null;
//		level = null;
		killed = true;
		visible = false;
	}

	public abstract void touched(Actor actor);

	public abstract void didMove();

	public void prepareForGC() {
		position = null;
		bounds = null;
		state = null;
		currentFrame.prepareForGC();
		currentFrame = null;
		animation.prepareForGC();
		animation = null;
		level = null;		
	}
}
