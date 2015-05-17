package br.odb.droidlib;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Region;



public class Sprite implements Renderable, Constants
{
	private int vx = 0;
	private int vy = 0;
	private int frameHeight;
	private int frameWidth;	
	private int frameCount;
	protected int currentFrame = 0;
	private Bitmap image;	
	private Vector2 pos;
	private int color;
	private boolean visible = true;
	

	/**
	 * @return the color
	 */
	public int getColor() {
		return color;
	}

	/**
	 * @param color the color to set
	 */
	public void setColor( int color) {
		this.color = color;
	}


	public Vector2 getPosition()
	{
		return new Vector2(pos);	
	}
	
	public void setPosition(int x,int y)
	{
		pos.x = x;
		pos.y = y;
	}
	
	public Sprite( Bitmap image) 
	{
		image.setDensity( Bitmap.DENSITY_NONE );
		pos = new Vector2();
		color = Color.BLACK;
		this.image = image;
		this.frameHeight = image.getHeight( );
		this.frameWidth = image.getWidth( );
		this.setFrameCount( 1 );
	}
	
	public Sprite( Bitmap image, int frameWidth)
	{
		this.image = image;
		this.frameHeight = image.getHeight( );
		this.frameWidth = frameWidth;
		pos = new Vector2();
		color = Color.BLACK;
		
		this.setFrameCount( 1 );
	}
	
	public void setFrame(int frame) 
	{
		this.currentFrame = frame;
	}
	
	public void nextFrame()
	{
		if (currentFrame != getFrameCount() - 1)
			currentFrame++;
		else {
			currentFrame = 0;
			setVisible( false );
		}
	}

	
	public int getCurrentFrameIndex() {
		return currentFrame;
	}
	
	public void previousFrame()
	{
		if (currentFrame != 0)
			currentFrame--;
		else
			currentFrame = getFrameCount() - 1;
	}
	
	public void setFrameCount(int frameCount) {
		this.frameCount = frameCount;
	}

	public int getFrameCount() {
		return frameCount;
	}

	@Override
	public void draw( Canvas g, Vector2 camera ) 
	{	
		
		if ( !visible )
			return;
		
		g.save();

		int offset = frameWidth * currentFrame;
		RectF rectf = new RectF();
		
		rectf.left = pos.x - ( camera.x * TILE_SIZE_X );
		rectf.top = pos.y - ( camera.y * TILE_SIZE_Y );
		rectf.right = pos.x + frameWidth - ( camera.x * TILE_SIZE_X );
		rectf.bottom = pos.y + frameHeight - ( camera.y * TILE_SIZE_Y );
		
		g.clipRect( rectf, Region.Op.INTERSECT );
		Paint paint = new Paint();
		g.drawBitmap( image, pos.x+ vx - offset - ( camera.x * TILE_SIZE_X ), pos.y + vy - ( camera.y * TILE_SIZE_Y ), paint );
		
		g.restore();
	}
	
	public float getHeight() {
	
		return frameHeight;
	}

	public float getWidth() {
		
		return frameWidth;
	}

	public void setPosition( Vector2 p) {
		
		if ( p == null )
			return;
		
		pos.x = p.x * frameWidth;
		pos.y = p.y * frameHeight;
	}

	public boolean isVisible() {
		return visible;
	}

	@Override
	public void draw(Canvas canvas) {
		
		if ( !visible )
			return;
		
		draw( canvas, pos );
	}

	@Override
	public void setVisible(boolean b) {
		visible  = b;		
	}	
}
