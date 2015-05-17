package br.odb.knights;

import android.content.res.Resources;

public class TurleKnight extends Knight {
	public TurleKnight( Resources res ) {
		super( R.drawable.turtle, 30, 6, res );
	}
	
	@Override
	public String toString() {
	
		return "Turtle Knight - " + super.toString();
	}

	@Override
	public String getChar() {
		return String.valueOf( KnightsConstans.SPAWNPOINT_TURTLE );
	}
}
