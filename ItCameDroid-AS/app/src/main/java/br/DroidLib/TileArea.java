package br.DroidLib;

import java.util.ArrayList;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import br.GlobalGameJam.Actor;
import br.GlobalGameJam.Vec2;

public class TileArea {
	
	public Tile[][] map;
	private int dimX;
	private int dimY;
	Tile[] tilePalette;
	Tile[] wallPalette;

	int[] tilePaletteIndexes;
	protected ArrayList<Actor> actors;
	private LevelSnapshot snapshot;
	private int[] wallPalleteIndexes;

	public Tile getTile(int x, int y) {
		return map[x][y];
	}

	public void setTile(int x, int y, Tile tile) {
		map[x][y] = tile;
	}

	public int getWidth() {
		return dimX;
	}

	public int getHeight() {
		return dimY;
	}

	public Tile getTileAt(int x, int y) {
		return map[x][y];
	}

	public TileArea(int i, int j, int baseTypeId, Resources resources,
			int[] tilePaletteIndex, int chancesTile, int[] wallTiles, int chancesWall) {
		setActors(new ArrayList<Actor>());
		tilePaletteIndexes = tilePaletteIndex;
		wallPalleteIndexes = wallTiles;
		int lastx = 0;
		int lasty = 0;
		Tile tile;
		map = new Tile[i][j];

		dimX = i;
		dimY = j;
		int maiorY = 0;
		tilePalette = new Tile[2];
		wallPalette = new Tile[2];
		
		for (int c = 0; c < tilePaletteIndexes.length; c++)
			tilePalette[c] = new Tile(resources, tilePaletteIndexes[c]);
		
		for (int c = 0; c < wallTiles.length; c++)
			wallPalette[c] = new Tile(resources, wallTiles[c]);
		
		

		for (int y = 0; y < j; y++) {
			
			for (int x = 0; x < i; x++) {
				
				tile = new Tile( baseTypeId,
						tilePalette[ Math.round( Math.random() * 45 ) == 0 ? 0 : 1 ]
								.getAndroidBitmap());
				
				map[x][y] = tile;
				
				tile.setX(lastx);
				tile.setY(lasty
						- (tile.getAndroidBitmap().getHeight() - Constants.BASETILEHEIGHT));
				
				tile.setY(lasty);
				
				lastx += tile.getAndroidBitmap().getWidth();
				
				if (tile.getAndroidBitmap().getHeight() > maiorY) {
					
					maiorY = tile.getAndroidBitmap().getHeight();
				}

			}
			lastx = 0;
			lasty += maiorY;
		}

	}

	public void draw(Canvas canvas, Paint paint) {

		Actor[][] actorMap = makeSnapshot();

		for (int x = 0; x < dimX; x++) {

			for (int y = 0; y < dimY; y++) {

				map[x][y].draw(canvas, paint);

				if (actorMap[x][y] != null) {

					actorMap[x][y].draw(canvas, paint);
				}
			}
		}
	}

	private Actor[][] makeSnapshot() {

		Actor[][] toReturn = new Actor[getWidth()][];

		Actor actor;

		for (int x = 0; x < toReturn.length; ++x) {

			toReturn[x] = new Actor[getHeight()];

			for (int y = 0; y < toReturn[x].length; ++y) {
				toReturn[x][y] = null;
			}
		}

		for (int c = 0; c < getActors().size(); c++) {

			actor = getActors().get(c);

			if (actor.killed) {
				continue;
			}

			int currentX = (int) (actor.getPosition().x / Constants.BASETILEWIDTH);
			int currentY = (int) (actor.getPosition().y / Constants.BASETILEHEIGHT);
			toReturn[currentX][currentY] = actor;
		}

		return toReturn;
	}

	public void setTileType(int i, int j, int type ) {

		if (i < 0 || i >= map.length) {
			return;
		}

		if (j < 0 || j >= map[i].length) {
			return;
		}

		Tile tile = map[i][j];
		tile.setX(tile.getX()
				+ (tile.getAndroidBitmap().getWidth() - Constants.BASETILEWIDTH));
		tile.setY(tile.getY()
				+ (tile.getAndroidBitmap().getHeight() - Constants.BASETILEHEIGHT));

		
		
		if ( type != 0 ) {
			
			tile.setTile( type, wallPalette[ Math.round( Math.random() * 6 ) != 0 ? 0 : 1 ].getAndroidBitmap());
		} else {
			tile.setTile( type, tilePalette[ ( Math.random() * 3 ) == 0 ? 0 : 1 ].getAndroidBitmap() );
			
		}
		
		tile.setX(tile.getX()
				- (tile.getAndroidBitmap().getWidth() - Constants.BASETILEWIDTH));
		tile.setY(tile.getY()
				- (tile.getAndroidBitmap().getHeight() - Constants.BASETILEHEIGHT));
	}

	public void move(float x, float y) {
		move((int) x, (int) y);
	}

	public void move(int x, int y) {

		for (int i = 0; i < dimX; i++)
			for (int j = 0; j < dimY; j++) {

				Tile tile = map[i][j];
				tile.setX(tile.getX() - x);
				tile.setY(tile.getY() - y);
			}
	}

	public void addActor(int i, int j, Actor actor) {

		if (i < 0 || i >= map.length) {
			return;
		}

		if (j < 0 || j >= map[i].length) {
			return;
		}

		getActors().add(actor);
		int adjustY = -(Constants.BASETILEHEIGHT - actor.getBounds().height()) / 2;
		int adjustX = (Constants.BASETILEWIDTH - actor.getBounds().width()) / 2;
		Vec2 vec2 = new Vec2(i * Constants.BASETILEWIDTH + adjustX, j
				* Constants.BASETILEHEIGHT + adjustY);
		actor.setPosition(vec2);
	}

	public void tick(long timeInMS) {

		
		for (Actor a : getActors()) {

			if (a.killed) {
				continue;
			}

			assert (a.getPosition() != null);

			a.tick(timeInMS);

			for (Actor b : getActors()) {

				if (b.killed) {
					continue;
				}

				if (a == b) {
					continue;
				}

				assert (b.getPosition() != null);

				if (a.getPosition().isCloseEnoughToConsiderEqualTo(
						b.getPosition())) {
					a.touched(b);
					b.touched(a);
				}
			}
		}
	}

	public ArrayList<Actor> getActors() {
		return actors;
	}

	public void setActors(ArrayList<Actor> actors) {
		this.actors = actors;
	}

	public boolean outsideMap(Actor actor) {
		Vec2 pos = actor.getPosition();
		
		int x = (int) (pos.y / Constants.BASETILEHEIGHT);
		int y = (int) (pos.x / Constants.BASETILEWIDTH);
		
		return x <= 0 || y <= 0 || ( y >= map.length -1 ) || ( x >= map[ x ].length - 1 );
	}
}
