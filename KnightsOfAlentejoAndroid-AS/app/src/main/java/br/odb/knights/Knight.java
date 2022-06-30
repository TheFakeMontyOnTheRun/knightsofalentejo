package br.odb.knights;

import androidx.annotation.NonNull;

public abstract class Knight extends Actor {

	public boolean hasExited;

	Knight(int healthPoints, int attackPoints) {
		super(healthPoints, attackPoints);
	}

	@NonNull
	@Override
	public String toString() {
		return Math.max(super.healthPoints, 0) + " HP";
	}

	public void setAsExited() {
		hasExited = true;
	}
}
