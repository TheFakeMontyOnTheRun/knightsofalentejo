package br.odb.knights;

import br.odb.droidlib.Updatable;

/**
 * Created by monty on 23/08/16.
 */
public class Splat implements Updatable {
	public static final int TOTAL_ANIMATION_TIME = 1500;
	private static final int NUMBER_OF_FRAMES = 3;
	private long showSplatTime = 0;

	public Splat() {
		startSplatAnimation();
	}

	public synchronized void update(long ms) {
		showSplatTime -= ms;
	}

	void startSplatAnimation() {
		showSplatTime = TOTAL_ANIMATION_TIME;
	}

	public int getSplatFrame() {
		if (showSplatTime > 0) {
			int timePerFrame = TOTAL_ANIMATION_TIME / NUMBER_OF_FRAMES;
			int frame = (int) ((TOTAL_ANIMATION_TIME - showSplatTime)/ timePerFrame);

			return frame;
		} else {
			return -1;
		}
	}

	public boolean isFinished() {
		return showSplatTime <= -TOTAL_ANIMATION_TIME / NUMBER_OF_FRAMES;
	}
}
