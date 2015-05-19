/**
 * 
 */
package br.GlobalGameJam;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

/**
 * @author monty
 *
 */
public class MiniMapWidget {
	
	private Level level;

	public MiniMapWidget( Level level ) {
		this.level = level;
	}
	
	public void draw(Canvas canvas, Paint paint) {
		
		paint = new Paint();
		
		paint.setColor( Color.GREEN );
		
		for ( int x = 0; x < level.getWidth(); ++x )
			for ( int y = 0; y < level.getHeight(); ++y ) {
				if ( !level.mayMoveTo( x, y ) )
					canvas.drawRect( x * 2, y * 2, (x * 2) + 2, (y * 2 ) + 2, paint );
			}
	}
}
