/**
 * 
 */
package br.odb.knights;

import android.content.res.Resources;
import br.odb.droidlib.Tile;

/**
 * @author monty
 *
 */
public abstract class Monster extends Actor 
{
	public Monster(int resId, int healthPonts, int attackPoints, Resources res ) {
		super(resId, healthPonts, attackPoints, res );
	}


	void updateTarget( GameLevel level ) {
		
	}

	
	boolean dealWith( Knight knight, GameLevel level, int relX, int relY ) {
		
		boolean moved = false;
		
		checkpointPosition();

		if (relY > 0) {
			act(Actions.MOVE_DOWN);
			moved = true;
		} else if (relY < 0) {
			act(Actions.MOVE_UP);
			moved = true;
		} else if (relX > 0) {
			act(Actions.MOVE_RIGHT);
			moved = true;
		} else if (relX < 0) {
			act(Actions.MOVE_LEFT);
			moved = true;
		}

		if (moved) {

			Tile loco = level.getTile(getPosition());

			if (!level.validPositionFor(this)) {

				if (!isAlive()) {
					loco.setOcupant(this);
					return false;
				} else if ( loco.getOcupant() instanceof Knight ) {
					Knight k = (Knight) loco.getOcupant();
					if ( !k.hasExited && k.isAlive() ) {
						k.attack( this );
					}
				}
				this.undoMove();
			} else {
				loco = level.getTile( previousPosition );
				loco.setOcupant(null);
				loco = level.getTile(getPosition());
				loco.setOcupant(this);
			}

		}
		
		return moved;
	}
}
