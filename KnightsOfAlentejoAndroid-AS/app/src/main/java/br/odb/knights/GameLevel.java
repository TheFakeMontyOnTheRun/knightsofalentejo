package br.odb.knights;

import java.util.ArrayList;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Region;
import br.odb.droidlib.Constants;
import br.odb.droidlib.Layer;
import br.odb.droidlib.Renderable;
import br.odb.droidlib.Tile;
import br.odb.droidlib.Updatable;
import br.odb.droidlib.Vector2;

public class GameLevel extends Layer {

	private Tile[][] tileMap;
	ArrayList<Actor> entities;
	private ArrayList<Actor> dead;
	private int remainingKnights;
	int remainingMonsters;

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

	public GameLevel() {
		super();

		entities = new ArrayList<Actor>();
		dead = new ArrayList<Actor>();
		tileMap = new Tile[getGameWidth()][getGameHeight()];
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
		remainingKnights = 3;
		tileMap = new Tile[getGameWidth()][getGameHeight()];
		entities = new ArrayList<Actor>();
		dead = new ArrayList<Actor>();
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
				case KnightsConstans.BRICKS:
					tile.setMyColor(Color.GRAY);
					tile.setBlock(true);
					tile.setImage(bitmaps[1]);
					break;
				case KnightsConstans.DOOR:
					tile.setMyColor(Color.GRAY);
					tile.setBlock(false);
					tile.setImage(bitmaps[10]);
					break;
				case KnightsConstans.BEGIN:
					tile.setMyColor(Color.GRAY);
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

	public void setDificulty(byte dificulty) {

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

		// for ( Actor a : dead ) {
		// if ( entities.contains( a ) ) {
		// entities.remove( a );
		// }
		// }
	}

	public void reset(Resources res) {
		int kind;
		for (int c = 0; c < tileMap.length; ++c) {
			for (int d = 0; d < tileMap[c].length; ++d) {

				kind = tileMap[c][d].getKind();

				switch (kind) {

				case KnightsConstans.SPAWNPOINT_BAPHOMET:
					addEntity(new Baphomet(res), c, d);
					++remainingMonsters;
					break;
				case KnightsConstans.SPAWNPOINT_BULL:
					addEntity(new BullKnight(res), c, d);
					break;
				case KnightsConstans.SPAWNPOINT_TURTLE:
					addEntity(new TurleKnight(res), c, d);
					break;
				case KnightsConstans.SPAWNPOINT_EAGLE:
					addEntity(new EagleKnight(res), c, d);
					break;
				case KnightsConstans.SPAWNPOINT_CUCO:
					addEntity(new Cuco(res), c, d);
					++remainingMonsters;
					break;
				case KnightsConstans.SPAWNPOINT_MOURA:
					addEntity(new Moura(res), c, d);
					++remainingMonsters;
					break;
				case KnightsConstans.SPAWNPOINT_DEVIL:
					addEntity(new Demon(res), c, d);
					++remainingMonsters;
					break;
				}
			}
		}
	}

	private Actor addEntity(Actor actor, int c, int d) {
		add(actor);
		entities.add(actor);
		tileMap[c][d].setOcupant(actor);
		actor.setPosition(new Vector2(c, d));
		return actor;
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

		if ((tileMap[c][d].getOcupant() instanceof Actor)
				&& !((Actor) tileMap[c][d].getOcupant()).isAlive())
			return true;

		if ((tileMap[c][d].getOcupant() instanceof Knight)
				&& ((Knight) tileMap[c][d].getOcupant()).hasExited)
			return true;

		if (tileMap[c][d].getOcupant() instanceof Actor)
			return false;

		return true;
	}

	public Actor getActorAt(int x, int y) {

		if (tileMap[x][y].getOcupant() instanceof Actor)
			return ((Actor) tileMap[x][y].getOcupant());
		else
			return null;
	}

	public void battle(Actor attacker, Actor defendant) {

		Vector2 pos;

		attacker.attack(defendant);
		defendant.attack(attacker);

		if (!attacker.isAlive()) {

			pos = attacker.getPosition();
			dead.add(attacker);
			tileMap[(int) pos.x][(int) pos.y].setOcupant(null);
		}

		if (!defendant.isAlive()) {

			pos = defendant.getPosition();
			dead.add(defendant);
			tileMap[(int) pos.x][(int) pos.y].setOcupant(null);
		}
	}

	public int getGameWidth() {

		return 20;
	}

	public int getGameHeight() {

		return 20;
	}

	public int getScreenWidth() {

		return getGameWidth() * Constants.BASETILEWIDTH;
	}

	public int getScreenHeight() {

		return getGameHeight() * Constants.BASETILEHEIGHT;
	}

	public int getAliveKnigts() {

		return remainingKnights;
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

	public Actor addEntity(Actor a, int c, int d, int h) {
		a.healthPoints = h;		
		return addEntity( a, c, d);
	}

	public void setOcupant(int x, int y, Actor a) {
		tileMap[ y ][ x ].setOcupant( a );		
	}
}
