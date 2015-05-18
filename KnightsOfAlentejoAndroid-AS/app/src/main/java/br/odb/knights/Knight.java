package br.odb.knights;

import android.content.res.Resources;

public abstract class Knight extends Actor {
	
	public boolean hasExited;

	Knight( int resId, int healthPoints, int attackPoints, Resources res ) {
		super( resId, healthPoints, attackPoints, res );
	}

	@Override
	public String toString() {
	
		return super.healthPoints + " HP";
	}

	public void setAsExited() {
		visual.setVisible( false );
		hasExited = true;		
	}
}
