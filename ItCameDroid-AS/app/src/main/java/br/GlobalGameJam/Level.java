package br.GlobalGameJam;

import java.util.ArrayList;

import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import br.DroidDemos.ItCameView;
import br.DroidLib.Constants;
import br.DroidLib.TileArea;

public class Level extends TileArea {
	private static final int LIMIT_MONSTERS_ALIVE = 16;
	public static Vec2 camera;
	Miner miner;
	boolean mayExit;
	MonsterMother[] motherMonster;
	public Dynamite dynamite;
	public boolean gameShouldEnd;
	public int dead;
	ArrayList< Actor > recycler = new ArrayList< Actor >();

	public Level(int i, int j, Resources resources, int[] tilePaletteIndexes, int chancesTile, int[] wallTiles, int chancesWall ) {
		super(i, j, br.GlobalGameJam.Constants.BASETERRAINRESID, resources,
				tilePaletteIndexes, chancesTile, wallTiles, chancesWall );
	}

	public void draw(Canvas canvas, Paint paint) {

		super.move(camera.x - (ItCameView.viewport.right / 2), camera.y
				- (ItCameView.viewport.bottom / 2));
		
		super.draw(canvas, paint);

		super.move( -camera.x + (ItCameView.viewport.right / 2), -camera.y
				+ (ItCameView.viewport.bottom / 2));

	}

	private void drawBackdrop(Canvas canvas, Paint paint) {
//
//		float ratioY;
//		float ratioX;
//		float ratioC;
//		int current = 0xFF000000;
		paint.setAlpha( 255 );
		paint.setColor( Color.BLACK );
		paint.setStyle( Style.FILL );
		canvas.drawRect( 0, 0, getWidth(), getHeight(), paint);
		
//		ratioX = Constants.BASETILEWIDTH;
//		ratioY = Constants.BASETILEHEIGHT;
//		ratioC = 255 / br.GlobalGameJam.Constants.SIZEY;
//
//		for (int x = 0; x < br.GlobalGameJam.Constants.SIZEX; ++x) {
//
//			current = 0xFF000000;
//
//			current += (x * 0xFFFF);
//
//			for (int y = 0; y < br.GlobalGameJam.Constants.SIZEY; ++y) {
//				paint.setColor(current);
//				canvas.drawCircle((x * ratioX) - (camera.x / 4), (y * ratioY)
//						- (camera.y / 4), 5, paint);
//				current += ratioC;
//			}
//		}
	}

	public void setCurrentCamera(Vec2 Camera) {
		camera = Camera;
	}

	public boolean mayMoveTo(float x, float y) {
		return mayMoveTo((int) x, (int) y);
	}

	public boolean mayMoveTo(int x, int y) {

		if (x < 0 || y < 0 || x >= br.GlobalGameJam.Constants.SIZEX
				|| y >= br.GlobalGameJam.Constants.SIZEY)
			return false;

		return super.getTileAt(x, y).getTipo() != br.GlobalGameJam.Constants.WALLRESID;
	}

	@Override
	public void tick(long timeInMS) {
		
		super.tick(timeInMS);
		
		recycler.clear();

		int alive = 0;
		
//		dead = 0;
		
		for ( Actor a : getActors() ) {
			if ( a instanceof Monster ) {
				if ( a.killed ) {
					++dead;
					recycler.add( a );
				} else {
					++alive;
				}
			}
		}
		
		for ( MonsterMother mm : motherMonster ) {
			
			Monster generated = mm.generate(10);
			
			if (generated != null && alive < LIMIT_MONSTERS_ALIVE ) {
				int x = (int) ( mm.getPosition().x / Constants.BASETILEWIDTH);
				int y = (int) ( mm.getPosition().y / Constants.BASETILEHEIGHT);
				addActor(x, y, generated);
				generated.target = miner;
			}
		}

		if (miner.killed || ( outsideMap( miner ) && dynamite.killed ) ) {			
			gameShouldEnd = true;
		}
		
		for ( Actor a : recycler ) {
			actors.remove( a );
			a.prepareForGC();
		}
	}


	@Override
	public void addActor(int i, int j, Actor actor) {
		super.addActor(i, j, actor);
		actor.level = this;
	}

	public Miner getMiner() {

		return miner;
	}

	public boolean mayMoveTo(Vec2 np) {

		return mayMoveTo( np.x, np.y);
	}
}
