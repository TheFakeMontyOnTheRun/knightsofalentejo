//package br.odb.droidlib;
//
//import java.util.ArrayList;
//
//import android.content.res.Resources;
//import android.graphics.Canvas;
//import android.graphics.Paint;
////import br.GlobalGameJam.Actor;
////import br.GlobalGameJam.Vector2;
//public class TileArea
//{
//	Tile[][] map;
//	private int dimX;
//	private int dimY;
//	Tile[] tilePalette;
//	int[] tilePaletteIndexes;
////	ArrayList<Actor> actors;
//	public Tile getTile(int x, int y)
//	{
//		return map[x][y];
//	}
//
//	public void setTile(int x, int y, Tile tile)
//	{
//		map[x][y] = tile;
//	}
//
//	public TileArea(int i, int j, int baseTypeId, Resources resources,
//			int[] tilePaletteIndex)
//	{
////		actors=new ArrayList<Actor>();
//		tilePaletteIndexes = tilePaletteIndex;
//		int lastx = 0;
//		int lasty = 0;
//		Tile tile;
//		map = new Tile[i][j];
//		dimX = i;
//		dimY = j;
//		int maiorY = 0;
//		tilePalette = new Tile[2];
//		for (int c = 0; c < tilePaletteIndexes.length; c++)
//			tilePalette[c] = new Tile( resources, tilePaletteIndexes[c] );
//
//		for (int y = 0; y < j; y++)
//		{
//			for (int x = 0; x < i; x++)
//			{
//				tile = new Tile(baseTypeId,
//						tilePalette[getPaletteIndex(baseTypeId)]
//								.getAndroidBitmap());
//				map[x][y] = tile;
//				tile.setX(lastx);
//				tile
//						.setY(lasty
//								- (tile.getAndroidBitmap().getHeight() - Constants.BASETILEHEIGHT));
//				tile.setY(lasty);
//				lastx += tile.getAndroidBitmap().getWidth();
//				if (tile.getAndroidBitmap().getHeight() > maiorY)
//					maiorY = tile.getAndroidBitmap().getHeight();
//
//			}
//			lastx = 0;
//			lasty += maiorY;
//		}
//
//	}
//
//	public void draw(Canvas canvas, Paint paint)
//	{
//		// TODO Auto-generated method stub
//		for (int x = 0; x < dimX; x++)
//			for (int y = 0; y < dimY; y++)
//				map[x][y].draw(canvas, paint);
//		
////			for (int c=0;c<actors.size();c++)
////				actors.get(c).draw(canvas,paint);
//
//	}
//
//	public void setTileType(int i, int j, int wALLRESID)
//	{
//		// TODO Auto-generated method stub
//		Tile tile = map[i][j];
//		tile
//				.setX(tile.getX()
//						+ (tile.getAndroidBitmap().getWidth() - Constants.BASETILEWIDTH));
//		tile
//				.setY(tile.getY()
//						+ (tile.getAndroidBitmap().getHeight() - Constants.BASETILEHEIGHT));
//
//		tile.setTile(wALLRESID, tilePalette[getPaletteIndex(wALLRESID)]
//				.getAndroidBitmap());
//		tile
//				.setX(tile.getX()
//						- (tile.getAndroidBitmap().getWidth() - Constants.BASETILEWIDTH));
//		tile
//				.setY(tile.getY()
//						- (tile.getAndroidBitmap().getHeight() - Constants.BASETILEHEIGHT));
//	}
//
//	private int getPaletteIndex(int resID)
//	{
//		for (int c = 0; c < this.tilePalette.length; c++)
//			if (tilePaletteIndexes[c] == resID)
//				return c;
//		return 0;
//	}
//
//	public void move(int x, int y)
//	{
//		// TODO Auto-generated method stub
//		for (int i = 0; i < dimX; i++)
//			for (int j = 0; j < dimY; j++)
//			{
//				Tile tile = map[i][j];
//				tile.setX(tile.getX() - x);
//				tile.setY(tile.getY() - y);
//			}
//	}
//
////	public void addActor(int i, int j, Actor actor)
////	{
////		// TODO Auto-generated method stub
////		actors.add(actor);
////		int adjustY=-(Constants.BASETILEHEIGHT - actor.getBounds().height())/2;
////		int adjustX=(Constants.BASETILEWIDTH - actor.getBounds().width())/2;
////		Vector2 vec2=new Vector2(i*Constants.BASETILEWIDTH+adjustX,j*Constants.BASETILEHEIGHT+adjustY);
////		actor.setPosition(vec2);
////	}
////
////	public void tick()
////	{
////		for (int c=0;c<actors.size();c++)
////			actors.get(c).tick();
////		
////	}
//
//}
