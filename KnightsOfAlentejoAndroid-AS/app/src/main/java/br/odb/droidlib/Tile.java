package br.odb.droidlib;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import br.odb.knights.Actor;

public class Tile implements Constants, Renderable
{
	private int kind;
	private int myColor;
	private Vector2 myPos;
	private boolean block;
	private Bitmap tileImage;
	private boolean visible;
	private Renderable ocupant;
	
	/**
	 * @return the block
	 */
	public boolean isBlock() {
		return block;
	}
	
	public Bitmap getBitmap() {
		return tileImage;
	}
	
	public void setImage( Bitmap bitmap ) {
		this.tileImage = bitmap;
	}

	/**
	 * @param block the block to set
	 */
	public void setBlock(boolean block) {
		this.block = block;
	}

	/**
	 * @return the kind
	 */
	public int getKind() {
		return kind;
	}

	/**
	 * @param kind the kind to set
	 */
	public void setKind(int kind) {
		this.kind = kind;
		block = (kind != 0) && ( kind != 3 );
	}

	/**
	 * @return the myColor
	 */
	public int getMyColor() {
		return myColor;
	}

	/**
	 * @param myColor the myColor to set
	 */
	public void setMyColor( int myColor) {
		this.myColor = myColor;
	}


	public Tile(int x, int y,int kind)
	{
		if (kind < 0)
			kind = 0;
		
		visible = true;
		
		setKind(kind);
		myPos = new Vector2(x * TILE_SIZE_X, y * TILE_SIZE_Y);
		
//		int[] filenames = { R.drawable.grass, R.drawable.tile2, R.drawable.begin, R.drawable.exit };
//		System.out.println("reading " + filenames[ kind ] );
//		tileImage = BitmapFactory.decodeResource( GameView.getInstance().getResources(), filenames[ kind ] );
		
	}
	
	public void draw( Canvas g, Vector2 camera )
	{
		if ( visible ) {
			g.drawBitmap(tileImage, myPos.x - ( camera.x * TILE_SIZE_X ), myPos.y - ( camera.y * TILE_SIZE_Y ), null);
			
			if ( ocupant != null ) {
//				ocupant.setPosition( myPos );
			}
		}
	}

	public Vector2 getPosition() {
		return myPos;
	}

	public void setPosition(int x, int y) {
		myPos.x = x;
		myPos.y = y;
	}

	public void setPosition(Vector2 p) {
		myPos.x = p.x;
		myPos.y = p.y;		
		
		if ( ocupant != null ) {
			ocupant.setPosition( myPos );
		}
	}
	
	public boolean isVisible() {
		return visible;
	}

	public void setVisible( boolean b ) {
		visible = b;
	}

	@Override
	public void draw(Canvas canvas) {
		draw( canvas, myPos );		
	}

	public Renderable getOcupant() {
		return ocupant;
	}

	public void setOcupant( Actor actor ) {
		ocupant = actor;
	}
}
