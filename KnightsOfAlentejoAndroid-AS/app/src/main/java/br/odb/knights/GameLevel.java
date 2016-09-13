package br.odb.knights;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.odb.droidlib.Tile;
import br.odb.droidlib.Updatable;
import br.odb.droidlib.Vector2;
import br.odb.menu.GameActivity;

public class GameLevel implements Serializable {

	public static final int MAP_SIZE = 20;
	final private Tile[][] tileMap;
	final private ArrayList<Actor> entities;

	final public Map<Vector2, Splat> mSplats = new HashMap<>();
	private int remainingMonsters;
	private int aliveKnightsInCurrentLevel;
	private final int mLevelNumber;
	private int mExitedKnights;

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

	public GameLevel(int[][] map, int levelNumber) {

		this.mLevelNumber = levelNumber;
		this.aliveKnightsInCurrentLevel = 3;
		tileMap = new Tile[MAP_SIZE][MAP_SIZE];
		entities = new ArrayList<>();
		int[] mapRow;
		Tile tile;

		for (int row = 0; row < map.length; ++row) {
			mapRow = map[row];
			for (int column = 0; column < mapRow.length; ++column) {

				switch (mapRow[column]) {

					case KnightsConstants.BARS:
						tile = new Tile(mapRow[column], GameViewGLES2.ETextures.Bars);
						tile.setKind(mapRow[column]);
						tile.setBlock(true);
						break;

					case KnightsConstants.ARCH:
						tile = new Tile(mapRow[column], GameViewGLES2.ETextures.Arch);
						tile.setBlock(false);
						break;

					case KnightsConstants.BRICKS_BLOOD:
						tile = new Tile(mapRow[column], GameViewGLES2.ETextures.BricksBlood);
						tile.setBlock(true);
						break;

					case KnightsConstants.BRICKS_CANDLES:
						tile = new Tile(mapRow[column], GameViewGLES2.ETextures.BricksCandles);
						tile.setBlock(true);
						break;

					case KnightsConstants.BRICKS:
						tile = new Tile(mapRow[column], GameViewGLES2.ETextures.Bricks);
						tile.setBlock(true);
						break;

					case KnightsConstants.DOOR:
						tile = new Tile(mapRow[column], GameViewGLES2.ETextures.Exit);
						tile.setBlock(false);
						break;
					case KnightsConstants.BEGIN:
						tile = new Tile(mapRow[column], GameViewGLES2.ETextures.Begin);
						tile.setBlock(true);
						break;
					default:
						tile = new Tile(mapRow[column], GameViewGLES2.ETextures.Grass);
				}
				this.tileMap[row][column] = tile;
			}
		}
	}

	public void tick() {
		Monster m;
		int monstersBefore = remainingMonsters;

		remainingMonsters = 0;
		aliveKnightsInCurrentLevel = 0;
		mExitedKnights = 0;

		for (Actor a : entities) {

			if (a.isAlive()) {

				a.notifyEndOfTurn();

				if (a instanceof Monster) {
					m = (Monster) a;
					m.updateTarget(this);
					++remainingMonsters;
				} else if (!(((Knight) a).hasExited)) {
					++aliveKnightsInCurrentLevel;
				} else {
					++mExitedKnights;
				}
			}
		}
		GameConfigurations.getInstance().getCurrentGameSession().addtoScore(monstersBefore - remainingMonsters);
	}

	public void updateSplats(long ms) {
		List<Vector2> toRemove = new ArrayList<>();

		for (Vector2 pos : mSplats.keySet()) {
			Splat splat = mSplats.get(pos);
			splat.update(ms);

			if (splat.isFinished()) {
				toRemove.add(pos);
			}
		}

		for (Vector2 pos : toRemove) {
			mSplats.remove(pos);
		}
	}

	public void reset() {
		int kind;
		for (int row = 0; row < tileMap.length; ++row) {
			for (int column = 0; column < tileMap[row].length; ++column) {

				kind = tileMap[row][column].getKind();

				switch (kind) {

					case KnightsConstants.SPAWNPOINT_BAPHOMET:
						addEntity(new Baphomet(), column, row);
						++remainingMonsters;
						break;
					case KnightsConstants.SPAWNPOINT_BULL:
						addEntity(new BullKnight(), column, row);
						break;
					case KnightsConstants.SPAWNPOINT_TURTLE:
						addEntity(new TurtleKnight(), column, row);
						break;
					case KnightsConstants.SPAWNPOINT_EAGLE:
						addEntity(new EagleKnight(), column, row);
						break;
					case KnightsConstants.SPAWNPOINT_CUCO:
						addEntity(new Cuco(), column, row);
						++remainingMonsters;
						break;
					case KnightsConstants.SPAWNPOINT_MOURA:
						addEntity(new Moura(), column, row);
						++remainingMonsters;
						break;
					case KnightsConstants.SPAWNPOINT_DEVIL:
						addEntity(new Demon(), column, row);
						++remainingMonsters;
						break;
				}
			}
		}
	}

	private void addEntity(Actor actor, int x, int y) {
		entities.add(actor);
		tileMap[y][x].setOccupant(actor);
		actor.setPosition(new Vector2(x, y));
	}

	public Tile getTile(Vector2 position) {
		return this.tileMap[(int) position.y][(int) position.x];
	}

	public int getTotalActors() {
		return entities.size();
	}

	public Updatable getActor(int c) {
		return entities.get(c);
	}

	public boolean validPositionFor(Actor actor) {

		int row, column;
		row = (int) actor.getPosition().y;
		column = (int) actor.getPosition().x;

		if (tileMap[row][column].isBlock()) {
			return false;
		}

		if ((tileMap[row][column].getOccupant() instanceof Actor)
				&& !((Actor) tileMap[row][column].getOccupant()).isAlive()) {
			return true;
		}

		if ((tileMap[row][column].getOccupant() instanceof Knight)
				&& ((Knight) tileMap[row][column].getOccupant()).hasExited) {
			return true;
		}

		return !(tileMap[row][column].getOccupant() instanceof Actor);
	}

	private Actor getActorAt(int x, int y) {

		if (tileMap[y][x].getOccupant() instanceof Actor)
			return ((Actor) tileMap[y][x].getOccupant());
		else
			return null;
	}

	public void battle(Actor attacker, Actor defendant) {

		Vector2 pos;

		createSplatAt(attacker.getPosition());
		createSplatAt(defendant.getPosition());

		attacker.attack(defendant);
		defendant.attack(attacker);

		if (!attacker.isAlive()) {
			pos = attacker.getPosition();
			tileMap[(int) pos.y][(int) pos.x].setOccupant(null);
		}


		if (!defendant.isAlive()) {
			pos = defendant.getPosition();
			tileMap[(int) pos.y][(int) pos.x].setOccupant(null);
		}
	}

	void createSplatAt(Vector2 pos) {
		mSplats.put(pos, new Splat());
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

	public int getTotalAvailableKnights() {
		return this.aliveKnightsInCurrentLevel;
	}

	private boolean isBlockAt(int x, int y) {
		return tileMap[y][x].isBlock();
	}

	public boolean canMove(Actor actor, GameActivity.Direction direction) {
		Vector2 position = actor.getPosition().add(direction.getOffsetVector());

		return !isBlockAt((int) position.x, (int) position.y);
	}

	public boolean canAttack(Actor actor, GameActivity.Direction direction) {
		Vector2 position = actor.getPosition().add(direction.getOffsetVector());
		return getActorAt((int) position.x, (int) position.y) instanceof Monster;
	}

	public boolean needsUpdate() {
		return !mSplats.keySet().isEmpty();
	}


	public int getLevelNumber() {
		return this.mLevelNumber;
	}

	public int getTotalExitedKnights() {
		return mExitedKnights;
	}
}
