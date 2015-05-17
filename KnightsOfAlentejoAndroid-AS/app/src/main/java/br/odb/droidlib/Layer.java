package br.odb.droidlib;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Region;

public class Layer implements Renderable {
	final public ArrayList< Renderable > children = new ArrayList< Renderable >();
	public final Vector2 position = new Vector2();
	public final Vector2 size = new Vector2();
	private boolean visible;
	
	
	
	public Layer() {
		visible = true;
	}
	
	public void add( Renderable d ) {
		children.add( d );
	}
	
	public void clear() {
		children.clear();
	}
	
	public void draw( Canvas canvas, Vector2 camera ) {
		
		if ( !visible )
			return;
		
		for ( Renderable r : children ) {
			canvas.clipRect( position.x, position.y, position.x + size.x, position.y + size.y, Region.Op.UNION );
			r.draw(canvas, position.add( camera ) );
		}
	}

	@Override
	public void setPosition(Vector2 myPos) {
		position.set( myPos );
	}

	@Override
	public void draw(Canvas canvas) {
		if ( visible )
			draw( canvas, position );		
	}

	@Override
	public void setVisible(boolean b) {
		visible = b;
		
	}
}
