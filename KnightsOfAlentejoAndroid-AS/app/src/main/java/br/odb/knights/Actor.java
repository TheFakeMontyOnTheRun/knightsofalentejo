package br.odb.knights;

import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

import br.odb.GL2JNILib;
import br.odb.droidlib.Renderable;
import br.odb.droidlib.StripSprite;
import br.odb.droidlib.Updatable;
import br.odb.droidlib.Vector2;

public abstract class Actor implements Renderable, Updatable {

	public static final boolean shouldDrawHealthBar = false;

	public enum Actions {MOVE_UP, MOVE_RIGHT, MOVE_DOWN, MOVE_LEFT}

	final public StripSprite visual;
	final private Vector2 position;
	int healthPoints;
	final private int attackPoints;
	Vector2 previousPosition;
	final private StripSprite splat;
	private long showSplatTime;
	final Resources res;

	public int getStateFrame() {
		return visual.getCurrentFrame();
	}

	public void startSplatAnimation() {
		showSplatTime = 300;
		splat.play();
	}


	public int getSplatFrame() {
		if (showSplatTime > 0) {
			return splat.getCurrentFrame();
		} else {
			return -1;
		}
	}

	public void attack(Actor actor) {

		this.healthPoints -= actor.attackPoints;
		splat.play();
		showSplatTime = 300;

		visual.setFrame(1);

		if (healthPoints <= 0) {

			kill();
		}

	}

	private void kill() {
		visual.setFrame(2);
	}

	public boolean isAlive() {
		return (healthPoints > 0);
	}

	Actor(int resId, int healthPoints, int attackPoints, Resources res) {
		super();
		this.res = res;
		position = new Vector2();
		visual = new StripSprite(BitmapFactory.decodeResource(res, resId));
		splat = new StripSprite(BitmapFactory.decodeResource(res, R.drawable.splat));
		splat.setFrameCount(3);
		visual.setFrameCount(3);
		this.healthPoints = healthPoints;
		this.attackPoints = attackPoints;
	}

	public Vector2 getPosition() {
		return position;
	}

	@Override
	public void draw(Canvas canvas, Vector2 camera) {
		if (shouldDrawHealthBar) {
			if (healthPoints > 0) {
				RectF rectf = new RectF();
				Paint paint = new Paint();
				Vector2 pos = visual.getPosition();
				int frameWidth = visual.getFrameWidth();
				int frameHeight = visual.getFrameHeight();
				rectf.left = pos.x - (camera.x * frameWidth);
				rectf.top = pos.y - (camera.y * frameHeight) - 5;
				rectf.right = rectf.left + this.healthPoints;
				rectf.bottom = rectf.top + 5;
				paint.setColor(Color.RED);
				canvas.drawRect(rectf, paint);
			}

		}

		if (showSplatTime > 0) {
			splat.setVisible(true);
			splat.draw(canvas, camera);
		} else {

			visual.draw(canvas, camera);
		}
	}

	public void setPosition(Vector2 myPos) {
		position.set(myPos);
		visual.setPosition(myPos);
		splat.setPosition(myPos);
	}

	public void act(Actions action) {
		int x0;
		int y0;
		int x1;
		int y1;

		Vector2 v = getPosition();
		x0 = (int) v.x;
		y0 = (int) v.y;

		switch (action) {
			case MOVE_UP:

				this.setPosition(getPosition().add(new Vector2(0, -1)));

				break;

			case MOVE_DOWN:
				this.setPosition(getPosition().add(new Vector2(0, 1)));
				break;

			case MOVE_LEFT:
				this.setPosition(getPosition().add(new Vector2(-1, 0)));
				break;

			case MOVE_RIGHT:
				this.setPosition(getPosition().add(new Vector2(1, 0)));
				break;

		}
		x1 = (int) v.x;
		y1 = (int) v.y;

		GL2JNILib.moveAbout(x0, y0, x1, y1);
		visual.setFrame(1);
	}

	@Override
	public synchronized void update() {

		if (showSplatTime > 0) {
			showSplatTime -= 100;
		}


		if (splat.isVisible()) {
			splat.nextFrame();
		}
	}

	public void checkpointPosition() {
		previousPosition = new Vector2(getPosition());

	}

	public void undoMove() {
		setPosition(previousPosition);
	}

	abstract String getChar();

	public String getStats() {
		return getChar() + "," + ((int) position.x) + "," + ((int) position.y) + "," + healthPoints + "|";
	}
}
