/**
 * 
 */
package br.odb.droidlib;

import android.graphics.Canvas;

/**
 * @author monty
 *
 */
public interface Renderable {
	public void draw( Canvas canvas, Vector2 camera );

	public void setPosition(Vector2 myPos);
	
	public void setVisible(boolean b);
	
	public void draw(Canvas canvas);

	
}
