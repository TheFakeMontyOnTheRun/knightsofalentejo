/**
 *
 */
package br.odb.knights;

import android.content.res.Resources;

import br.odb.droidlib.Tile;

/**
 * @author monty
 */
abstract class Monster extends Actor {
    Monster(int resId, int healthPoints, int attackPoints, Resources res) {
        super(resId, healthPoints, attackPoints, res);
    }


    void updateTarget(GameLevel level) {

    }


    boolean dealWith(GameLevel level, int relX, int relY) {

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
                    loco.setOccupant(this);
                    return false;
                } else if (loco.getOccupant() instanceof Knight) {
                    Knight k = (Knight) loco.getOccupant();
                    if (!k.hasExited && k.isAlive()) {
                        k.attack(this);
	                    startSplatAnimation();
                    }
                }
                this.undoMove();
            } else {
                loco = level.getTile(previousPosition);
                loco.setOccupant(null);
                loco = level.getTile(getPosition());
                loco.setOccupant(this);
            }

        }

        return moved;
    }
}
