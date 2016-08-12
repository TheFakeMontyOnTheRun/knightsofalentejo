package br.odb.knights;

import java.util.ArrayList;
import java.util.List;

import br.odb.droidlib.Tile;
import br.odb.droidlib.Updatable;
import br.odb.droidlib.Vector2;
import br.odb.menu.GameActivity;

public class GameLevel {

    public static final int BASE_SQUARE_SIDE = 20;
    final private Tile[][] tileMap;
    final private ArrayList<Actor> entities;
    private int remainingMonsters;

    @Override
    public String toString() {

        String toReturn = "";

        for (Actor a : entities) {
            if (a.isAlive()) {

                toReturn += a.getStats();
            }
        }

        return toReturn;
    }

	public GameLevel(int[][] map) {
        tileMap = new Tile[BASE_SQUARE_SIDE][BASE_SQUARE_SIDE];
        entities = new ArrayList<>();
        int[] row;
        Tile tile;

        for (int c = 0; c < map.length; ++c) {
            row = map[c];
            for (int d = 0; d < row.length; ++d) {

                switch (row[d]) {

                    case KnightsConstants.BARS:
                        tile = new Tile(row[d], GameViewGLES2.ETextures.Bars );
                        tile.setKind(row[d]);
                        tile.setBlock(true);
                        break;

                    case KnightsConstants.ARCH:
                        tile = new Tile(row[d], GameViewGLES2.ETextures.Arch );
                        tile.setBlock(false);
                        break;

                    case KnightsConstants.BRICKS_BLOOD:
                        tile = new Tile(row[d], GameViewGLES2.ETextures.BricksBlood );
                        tile.setBlock(true);
                        break;

                    case KnightsConstants.BRICKS_CANDLES:
                        tile = new Tile(row[d], GameViewGLES2.ETextures.BricksCandles );
                        tile.setBlock(true);
                        break;

                    case KnightsConstants.BRICKS:
                        tile = new Tile(row[d], GameViewGLES2.ETextures.Bricks );
                        tile.setBlock(true);
                        break;

                    case KnightsConstants.DOOR:
                        tile = new Tile(row[d], GameViewGLES2.ETextures.Exit );
                        tile.setBlock(false);
                        break;
                    case KnightsConstants.BEGIN:
                        tile = new Tile(row[d], GameViewGLES2.ETextures.Begin );
                        tile.setBlock(true);
                        break;
                    default:
                        tile = new Tile(row[d], GameViewGLES2.ETextures.Grass );
                }
                this.tileMap[c][d] = tile;
            }
        }
    }

    public void tick() {
        Monster m;
	    int monstersBefore = remainingMonsters;

        remainingMonsters = 0;
        for (Actor a : entities) {
            if (a instanceof Monster && a.isAlive()) {
                m = (Monster) a;
                m.updateTarget(this);
                ++remainingMonsters;
            }
        }
	    GameConfigurations.getInstance().getCurrentGameSession().addtoScore( monstersBefore - remainingMonsters);
    }

    public void reset() {
        int kind;
        for (int c = 0; c < tileMap.length; ++c) {
            for (int d = 0; d < tileMap[c].length; ++d) {

                kind = tileMap[c][d].getKind();

                switch (kind) {

                    case KnightsConstants.SPAWNPOINT_BAPHOMET:
                        addEntity(new Baphomet(), c, d);
                        ++remainingMonsters;
                        break;
                    case KnightsConstants.SPAWNPOINT_BULL:
                        addEntity(new BullKnight(), c, d);
                        break;
                    case KnightsConstants.SPAWNPOINT_TURTLE:
                        addEntity(new TurtleKnight(), c, d);
                        break;
                    case KnightsConstants.SPAWNPOINT_EAGLE:
                        addEntity(new EagleKnight(), c, d);
                        break;
                    case KnightsConstants.SPAWNPOINT_CUCO:
                        addEntity(new Cuco(), c, d);
                        ++remainingMonsters;
                        break;
                    case KnightsConstants.SPAWNPOINT_MOURA:
                        addEntity(new Moura(), c, d);
                        ++remainingMonsters;
                        break;
                    case KnightsConstants.SPAWNPOINT_DEVIL:
                        addEntity(new Demon(), c, d);
                        ++remainingMonsters;
                        break;
                }
            }
        }
    }

    private void addEntity(Actor actor, int c, int d) {
        entities.add(actor);
        tileMap[c][d].setOccupant(actor);
        actor.setPosition(new Vector2(c, d));
    }

    public Tile getTile(Vector2 position) {
        return this.tileMap[(int) position.x][(int) position.y];
    }

    public int getTotalActors() {
        return entities.size();
    }

    public Updatable getActor(int c) {
        return entities.get(c);
    }

    public boolean validPositionFor(Actor actor) {

        int c, d;
        c = (int) actor.getPosition().x;
        d = (int) actor.getPosition().y;

        if (tileMap[c][d].isBlock()) {
	        return false;
        }

        if ((tileMap[c][d].getOccupant() instanceof Actor)
                && !((Actor) tileMap[c][d].getOccupant()).isAlive()) {
	        return true;
        }

        if ((tileMap[c][d].getOccupant() instanceof Knight)
                && ((Knight) tileMap[c][d].getOccupant()).hasExited) {
	        return true;
        }

        return !(tileMap[c][d].getOccupant() instanceof Actor);
    }

    private Actor getActorAt(int x, int y) {

        if (tileMap[x][y].getOccupant() instanceof Actor)
            return ((Actor) tileMap[x][y].getOccupant());
        else
            return null;
    }

    public void battle(Actor attacker, Actor defendant) {

        Vector2 pos;

        attacker.attack(defendant);
        defendant.attack(attacker);

        if (!attacker.isAlive()) {

            pos = attacker.getPosition();
            tileMap[(int) pos.x][(int) pos.y].setOccupant(null);
        }

        if (!defendant.isAlive()) {

            pos = defendant.getPosition();
            tileMap[(int) pos.x][(int) pos.y].setOccupant(null);
        }
    }

    public Actor getActorAt(Vector2 position) {

        return getActorAt((int) position.x, (int) position.y);
    }

    public Knight[] getKnights() {
        List<Knight> knights_filtered = new ArrayList<>();

        for (Actor a : entities) {
            if (a instanceof Knight && a.isAlive() && !((Knight) a).hasExited) {
                knights_filtered.add((Knight) a);
            }
        }

        Knight[] knights = new Knight[knights_filtered.size()];
        return knights_filtered.toArray(knights);
    }

    public int getMonsters() {
        return remainingMonsters;
    }

    private boolean isBlockAt(int x, int y) {
        return tileMap[x][y].isBlock();
    }

    public boolean canMove(Actor actor, GameActivity.Direction direction) {
        Vector2 position = actor.getPosition().add( direction.getOffsetVector());

        return !isBlockAt( (int)position.x, (int)position.y );
    }

    public boolean canAttack(Actor actor, GameActivity.Direction direction) {
        Vector2 position = actor.getPosition().add( direction.getOffsetVector());
        return getActorAt( (int)position.x, (int)position.y ) instanceof Monster;
    }
}
