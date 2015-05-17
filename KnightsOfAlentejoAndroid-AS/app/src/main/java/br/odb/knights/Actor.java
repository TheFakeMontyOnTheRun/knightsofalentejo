package br.odb.knights;

import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import br.odb.droidlib.Constants;
import br.odb.droidlib.Renderable;
import br.odb.droidlib.StripSprite;
import br.odb.droidlib.Tile;
import br.odb.droidlib.Updatable;
import br.odb.droidlib.Vector2;

public abstract class Actor implements Constants, Renderable, Updatable {

	public static enum Actions{ MOVE_UP, MOVE_RIGHT, MOVE_DOWN, MOVE_LEFT };
	
	public StripSprite visual;
	private Vector2 position;
	int healthPoints;
	private int attackPoints;
	protected Vector2 previousPosition;
	private StripSprite splat;
	long showSplatTime;
	
	public void attack(Actor actor) {
		
		this.healthPoints -= actor.attackPoints;
		splat.play();
		showSplatTime = 300;
		
		visual.setFrame( 1 );
		
		if ( healthPoints <= 0 ) {
			
			kill();
		}
		
	}
	
	public void kill()  {
//		visual.setVisible( false );
		visual.setFrame( 2 );
	}
	
	public boolean isAlive() {
		return ( healthPoints > 0 );
	}
	
	public Actor( int resId, int healthPonts, int attackPoints, Resources res ) {
		super();
		position = new Vector2();
		visual 
		= new StripSprite( BitmapFactory.decodeResource( res, resId ), Tile.TILE_SIZE_X );
		splat 
		= new StripSprite( BitmapFactory.decodeResource( res, R.drawable.splat ), Tile.TILE_SIZE_X );
		splat.setFrameCount( 3 );
		this.healthPoints = healthPonts;
		this.attackPoints = attackPoints;
	}
	
	public float getWidth() {		
		return visual.getWidth();
	}
	
	public float getHeight() {		
		return visual.getHeight();
	}
	
	public Vector2 getPosition() {
		return position;
	}
	
	@Override
	public void draw(Canvas canvas, Vector2 camera) {
		
		
		if ( showSplatTime > 0 ) {
			splat.setVisible( true );
//			splat.setFrame( 1 );
			splat.draw( canvas, camera );
		} else {
			
			visual.draw( canvas, camera );
		}
	}


	@Override
	public void setPosition(Vector2 myPos) {
		position.set( myPos );
		visual.setPosition( myPos );
		splat.setPosition( myPos );
	}

	public void act(Actions action ) {
		
		switch ( action ) {
			case MOVE_UP:
				this.setPosition( getPosition().add( new Vector2( 0, -1 ) ) );
			break;
			
			case MOVE_DOWN:
				this.setPosition( getPosition().add( new Vector2( 0, 1 ) ) );
			break;
			
			case MOVE_LEFT:
				this.setPosition( getPosition().add( new Vector2( -1, 0 ) ) );
			break;
			
			case MOVE_RIGHT:
				this.setPosition( getPosition().add( new Vector2( 1, 0 ) ) );
			break;
			
		}
		
		visual.setFrame( 1 );
	}
	
	
	@Override
	public void draw(Canvas canvas) {		
		
		if ( showSplatTime > 0 ) {
			splat.setVisible( true );
			splat.setFrame( 1 );
			splat.draw( canvas );
		} else {
			
			visual.draw( canvas );
		}
	}

	@Override
	public void setVisible(boolean b) {
		visual.setVisible( b );
		splat.setVisible( b );
	}

	@Override
	public  synchronized void update() {
		
		if ( showSplatTime > 0 ) {
			showSplatTime -= 100;
		}	
		
		
		if ( splat.isVisible() ) {
			splat.nextFrame();
		}
		
//		visual.nextFrame();
//		visual.setFrame( 0 );
	}

	public void checkpointPosition() {
		previousPosition = new Vector2( getPosition() );
		
	}

	public void undoMove() {
		setPosition( previousPosition );
	}

	public abstract String getChar();

	public String getStats() {
		return getChar() + "," + ((int)position.x) + "," + ((int)position.y) + "," + healthPoints + "|";
	}}
