package br.odb.knights;

import java.io.Serializable;

import br.odb.droidlib.Renderable;
import br.odb.droidlib.StripSprite;
import br.odb.droidlib.Updatable;
import br.odb.droidlib.Vector2;

public abstract class Actor implements Renderable, Updatable, Serializable {

	public enum Actions {MOVE_UP, MOVE_RIGHT, MOVE_DOWN, MOVE_LEFT}

	final public StripSprite visual;
	final private Vector2 position;
	int healthPoints;
	final private int attackPoints;
	Vector2 previousPosition;

	int getStateFrame() {
		return visual.getCurrentFrame();
	}

	public void attack(Actor actor) {

		this.healthPoints -= actor.attackPoints;

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
	public synchronized void update(long ms) {
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
