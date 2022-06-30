package br.odb.knights;

import br.odb.droidlib.Vector2;

/**
 * @author monty
 */
class Cuco extends Monster {

	public Cuco() {
		super(6, 2);
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

			if (newX >= 0 && newY >= 0 && newX < GameLevel.MAP_SIZE
					&& newY < GameLevel.MAP_SIZE
					&& level.getTile(scan).getOccupant() instanceof Knight && !((Knight) level.getTile(scan).getOccupant()).hasExited) {

				if (dealWith(level, x, 0))
					return;
			}

		}

		for (int y = -10; y < 10; ++y) {

			newX = (int) (myPosition.x);
			newY = (int) (y + myPosition.y);
			scan.x = newX;
			scan.y = newY;

			if (newX >= 0 && newY >= 0 && newX < GameLevel.MAP_SIZE
					&& newY < GameLevel.MAP_SIZE
					&& level.getTile(scan).getOccupant() instanceof Knight && !((Knight) level.getTile(scan).getOccupant()).hasExited) {

				if (dealWith(level, 0, y))
					return;
			}
		}

	}

	@Override
	public String getChar() {
		return String.valueOf(KnightsConstants.SPAWNPOINT_CUCO);
	}

	@Override
	public GameViewGLES2.ETextures getTextureIndex() {
		return GameViewGLES2.ETextures.values()[GameViewGLES2.ETextures.Cuco0.ordinal() + getStateFrame()];
	}
}
