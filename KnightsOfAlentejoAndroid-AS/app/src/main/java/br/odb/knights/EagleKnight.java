package br.odb.knights;

import android.content.res.Resources;

public class EagleKnight extends Knight {
	public EagleKnight( Resources res ) {
		super( R.drawable.falcon, 25, 10, res );
	}
	
	@Override
	public String toString() {
		return "The Shadow - " + super.toString();
	}

	@Override
	public String getChar() {
		return String.valueOf( KnightsConstants.SPAWNPOINT_EAGLE );
	}
}
