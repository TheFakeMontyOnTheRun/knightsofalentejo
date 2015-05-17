/**
 * 
 */
package br.odb.knights;

import android.content.res.Resources;
import br.odb.droidlib.Vector2;

/**
 * @author monty
 * 
 */
public class Cuco extends Monster {

	/**
	 * @param resId
	 */
	public Cuco( Resources res ) {
		super(R.drawable.cuco, 6, 2, res );
	}

	@Override
	public void updateTarget(GameLevel level) {
		Vector2 myPosition = getPosition();
		Vector2 scan = new Vector2();
		int newX;
		int newY;

		for (int x = -10; x < 10; ++x) {

			newX = (int) (x + myPosition.x);
			newY = (int) (myPosition.y);
			scan.x = newX;
			scan.y = newY;

			if (newX >= 0 && newY >= 0 && newX < level.getGameWidth()
					&& newY < level.getGameHeight()
					&& level.getTile(scan).getOcupant() instanceof Knight) {

				if (dealWith(((Knight) level.getTile(new Vector2(newX, newY))
						.getOcupant()), level, x, 0))
					return;
			}

		}

		for (int y = -10; y < 10; ++y) {

			newX = (int) (myPosition.x);
			newY = (int) (y + myPosition.y);
			scan.x = newX;
			scan.y = newY;

			if (newX >= 0 && newY >= 0 && newX < level.getGameWidth()
					&& newY < level.getGameHeight()
					&& level.getTile(scan).getOcupant() instanceof Knight) {

				if (dealWith(((Knight) level.getTile(new Vector2(newX, newY))
						.getOcupant()), level, 0, y))
					return;
			}
		}

	}

	@Override
	public String getChar() {
		return String.valueOf( KnightsConstans.SPAWNPOINT_CUCO );
	}
}
