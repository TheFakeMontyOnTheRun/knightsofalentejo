package br.odb.knights;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Region;

import java.util.ArrayList;

import br.odb.droidlib.Constants;
import br.odb.droidlib.Layer;
import br.odb.droidlib.Renderable;
import br.odb.droidlib.Tile;
import br.odb.droidlib.Updatable;
import br.odb.droidlib.Vector2;

public class GameLevel extends Layer {

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

	@Override
	public void draw(Canvas canvas, Vector2 camera) {

		for (Renderable r : children) {
			canvas.clipRect(position.x, position.y, position.x + size.x,
					position.y + size.y, Region.Op.UNION);

			if ((r instanceof Actor)) {
				continue;
			}
			r.draw(canvas, position.add(camera));
		}

		for (Renderable r : children) {
			canvas.clipRect(position.x, position.y, position.x + size.x,
					position.y + size.y, Region.Op.UNION);

			if (r instanceof Actor) {

				if (((Actor) r).isAlive()) {
					continue;
				}

				r.draw(canvas, position.add(camera));
			}
		}

		for (Renderable r : children) {
			canvas.clipRect(position.x, position.y, position.x + size.x,
					position.y + size.y, Region.Op.UNION);

			if (r instanceof Actor) {

				if (((Actor) r).isAlive()) {
					r.draw(canvas, position.add(camera));
				}
			}
		}
	}

	public GameLevel(int[][] map, Resources res) {
		tileMap = new Tile[BASE_SQUARE_SIDE][BASE_SQUARE_SIDE];
		entities = new ArrayList<Actor>();
		int[] row;
		Tile tile;

		Bitmap[] bitmaps = {
				BitmapFactory.decodeResource(res, R.drawable.grass),
				BitmapFactory.decodeResource(res, R.drawable.bricks),
				BitmapFactory.decodeResource(res, R.drawable.bricks),
				BitmapFactory.decodeResource(res, R.drawable.falcon),
				BitmapFactory.decodeResource(res, R.drawable.turtle),
				BitmapFactory.decodeResource(res, R.drawable.cuco),
				BitmapFactory.decodeResource(res, R.drawable.lady),
				BitmapFactory.decodeResource(res, R.drawable.demon),
				BitmapFactory.decodeResource(res, R.drawable.boss),
				BitmapFactory.decodeResource(res, R.drawable.begin),
				BitmapFactory.decodeResource(res, R.drawable.exit), };

		for (int c = 0; c < map.length; ++c) {
			row = map[c];
			for (int d = 0; d < row.length; ++d) {
				tile = new Tile(c, d, row[d]);
				tile.setKind(row[d]);

				switch (row[d]) {
				case KnightsConstants.BRICKS:
					tile.setBlock(true);
					tile.setImage(bitmaps[1]);
					break;
				case KnightsConstants.DOOR:
					tile.setBlock(false);
					tile.setImage(bitmaps[10]);
					break;
				case KnightsConstants.BEGIN:
					tile.setBlock(true);
					tile.setImage(bitmaps[9]);
					break;
				default:
					tile.setBlock(false);
					tile.setImage(bitmaps[0]);
				}
				this.add(tile);
				this.tileMap[c][d] = tile;
			}
		}
	}

	public void tick() {
		Monster m;

		remainingMonsters = 0;
		for (Actor a : entities) {
			if (a instanceof Monster && a.isAlive()) {
				m = (Monster) a;
				m.updateTarget(this);
				++remainingMonsters;
			}
		}
	}

	public void reset(Resources res) {
		int kind;
		for (int c = 0; c < tileMap.length; ++c) {
			for (int d = 0; d < tileMap[c].length; ++d) {

				kind = tileMap[c][d].getKind();

				switch (kind) {

				case KnightsConstants.SPAWNPOINT_BAPHOMET:
					addEntity(new Baphomet(res), c, d);
					++remainingMonsters;
					break;
				case KnightsConstants.SPAWNPOINT_BULL:
					addEntity(new BullKnight(res), c, d);
					break;
				case KnightsConstants.SPAWNPOINT_TURTLE:
					addEntity(new TurtleKnight(res), c, d);
					break;
				case KnightsConstants.SPAWNPOINT_EAGLE:
					addEntity(new EagleKnight(res), c, d);
					break;
				case KnightsConstants.SPAWNPOINT_CUCO:
					addEntity(new Cuco(res), c, d);
					++remainingMonsters;
					break;
				case KnightsConstants.SPAWNPOINT_MOURA:
					addEntity(new Moura(res), c, d);
					++remainingMonsters;
					break;
				case KnightsConstants.SPAWNPOINT_DEVIL:
					addEntity(new Demon(res), c, d);
					++remainingMonsters;
					break;
				}
			}
		}
	}

	private void addEntity(Actor actor, int c, int d) {
		add(actor);
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

		if (tileMap[c][d].isBlock())
			return false;

		if ((tileMap[c][d].getOccupant() instanceof Actor)
				&& !((Actor) tileMap[c][d].getOccupant()).isAlive())
			return true;

		if ((tileMap[c][d].getOccupant() instanceof Knight)
				&& ((Knight) tileMap[c][d].getOccupant()).hasExited)
			return true;

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

	public int getScreenWidth() {

		return BASE_SQUARE_SIDE * Constants.BASETILEWIDTH;
	}

	public int getScreenHeight() {

		return BASE_SQUARE_SIDE * Constants.BASETILEHEIGHT;
	}

	public Actor getActorAt(Vector2 position) {

		return getActorAt((int) position.x, (int) position.y);
	}

	public Knight[] getKnights() {
		ArrayList<Knight> knights_filtered = new ArrayList<Knight>();

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
}
