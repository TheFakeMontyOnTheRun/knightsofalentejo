package br.odb.droidlib;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.graphics.Canvas;

/**
 * 
 * @author monty
 *
 */
public class StripSprite extends Sprite {
	
	/**
	 * 
	 * @author monty
	 *
	 */
	public class ActorStateFrame {
		public int xOffset;
		public int state;
		public Bitmap sourceBitmapStrip;
	}
	
	@Override
	public void draw(Canvas g, Vector2 camera) {
		
		super.draw(g, camera);
	}
	
	/**
	 * 
	 */
	public ArrayList< ActorStateFrame > frames;
	/**
	 * 
	 */
	public Vector2 lastValidPosition;
	/**
	 * 
	 */	
	public ActorStateFrame currentStateFrame;
	
	
	/**
	 * 
	 * @param state
	 * @return
	 */
	public ActorStateFrame getFrameForState( int state ) {
		
		ActorStateFrame frame = null;
		
		for ( int c = 0; c < frames.size(); ++c ) {
			
			frame = frames.get( c );
			
			if ( frame.state == state )
				return frame;
		}
		
		return null;
	}
	
	/**
	 * 
	 * @param source
	 * @param stride
	 */
	public void autoMakeSpriteStripFixedOffset( Bitmap source, int stride ) {
		
		int amount = source.getWidth() / stride;
		
		ActorStateFrame frame;
		
		for ( int c = 0; c < amount; ++c ) {
			frame = new ActorStateFrame();
			frame.sourceBitmapStrip = source;
			frame.xOffset = c * stride;
			frame.state = c;
			frames.add( frame );
		}
	}

	/**
	 * 
	 */
	public void undo() {
		if (lastValidPosition != null) {
			setMapPosition((int)lastValidPosition.x, (int)lastValidPosition.y);
		}
	}

	/**
	 * 
	 * @param source
	 * @param i
	 */
	public StripSprite(Bitmap source, int i) {
		super(source, i);
	}

	/**
	 * 
	 * @return
	 */
	public Vector2 getMapPosition() {
		Vector2 p = getPosition();
		p.x /= TILE_SIZE_X;
		p.y /= TILE_SIZE_Y;
		return p;
	}

	/**
	 * 
	 * @param i
	 * @param j
	 */
	public void setMapPosition(int i, int j) {
		lastValidPosition = new Vector2(getMapPosition());
		// System.out.println("last valid position:"+ lastValidPosition.x +","+
		// lastValidPosition.y);
		// System.out.println("moving to:"+ i +","+ j);
		setPosition(i * TILE_SIZE_X, j * TILE_SIZE_Y);
	}

	/**
	 * 
	 * @param relx
	 * @param rely
	 */
	public void move(int relx, int rely) {
		Vector2 pos = getMapPosition();
		pos.x += relx;
		pos.y += rely;
		if (pos.x < 0 || pos.y < 0 || pos.x >= TOTAL_TILES_X
				|| pos.y >= TOTAL_TILES_Y) {
			lastValidPosition = new Vector2(getMapPosition());
			undo();
			return;
		}
		setMapPosition( (int) pos.x, (int)pos.y);
	}


	public void play() {
		this.setVisible( true );
		currentFrame = 0;
	}
}
