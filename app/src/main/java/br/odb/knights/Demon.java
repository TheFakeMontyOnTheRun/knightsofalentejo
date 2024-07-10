package br.odb.knights;

import br.odb.droidlib.Vector2;

/**
 * @author monty
 */
class Demon extends Monster {

    public Demon() {
        super(10, 2);
    }

    @Override
    public void updateTarget(GameLevel level) {
        Vector2 myPosition = getPosition();
        Vector2 scan = new Vector2();
        int newX;
        int newY;

        for (int x = -10; x < 10; ++x) {
            for (int y = -10; y < 10; ++y) {

                newX = (int) (x + myPosition.x);
                newY = (int) (y + myPosition.y);
                scan.x = newX;
                scan.y = newY;

                if (newX >= 0 && newY >= 0 && newX < GameLevel.MAP_SIZE
                        && newY < GameLevel.MAP_SIZE
                        && level.getTile(scan).getOccupant() instanceof Knight && !((Knight) level.getTile(scan).getOccupant()).hasExited) {

                    if (dealWith(level, x, y))
                        return;
                }
            }
        }
    }


    @Override
    public String getChar() {
        return String.valueOf(KnightsConstants.SPAWNPOINT_DEVIL);
    }

    @Override
    public GameViewGLES2.ETextures getTextureIndex() {
        return GameViewGLES2.ETextures.values()[GameViewGLES2.ETextures.Demon0.ordinal() + getStateFrame()];
    }
}
