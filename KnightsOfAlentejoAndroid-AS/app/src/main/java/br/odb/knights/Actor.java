package br.odb.knights;

import br.odb.droidlib.Renderable;
import br.odb.droidlib.StripSprite;
import br.odb.droidlib.Updatable;
import br.odb.droidlib.Vector2;

public abstract class Actor implements Renderable, Updatable {

	public enum Actions {MOVE_UP, MOVE_RIGHT, MOVE_DOWN, MOVE_LEFT}

	final public StripSprite visual;
	final private Vector2 position;
	int healthPoints;
	final private int attackPoints;
	Vector2 previousPosition;
	final private StripSprite splat;
	private long showSplatTime;

	int getStateFrame() {
		return visual.getCurrentFrame();
	}

	void startSplatAnimation() {
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

	Actor(int healthPoints, int attackPoints) {
		super();
		position = new Vector2();
		visual = new StripSprite();
		splat = new StripSprite();
		splat.setFrameCount(3);
		visual.setFrameCount(3);
		this.healthPoints = healthPoints;
		this.attackPoints = attackPoints;
	}

	public Vector2 getPosition() {
		return position;
	}

	public void setPosition(Vector2 myPos) {
		position.set(myPos);
	}

	public void act(Actions action) {
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

	public abstract String getChar();

	public String getStats() {
		return getChar() + "," + ((int) position.x) + "," + ((int) position.y) + "," + healthPoints + "|";
	}
}
