package br.GlobalGameJam;

import android.util.FloatMath;

public class Vec2 {
	public float x;
	public float y;

	/** Creates a new instance of Vec2 */
	public Vec2(int aX, int aY) {
		x = aX;
		y = aY;
	}

	@Override
	public boolean equals(Object o) {
		if ( o instanceof Vec2) {
			Vec2 v = (Vec2) o;
			return v.x == x && v.y == y;
		} else {
			return false;
		}		
	}
	
	public Vec2(Vec2 position) {
		x = position.x;
		y = position.y;
	}

	public Vec2() {
		x = 0;
		y = 0;
	}
	
	public Vec2(float x, float y) {
		set( x, y );
	}

	private void set(float x, float y) {
		this.x = x;
		this.y = y;		
	}

	public void negate() {
		x = -x;
		y = -y;
	}

	public Vec2 sub( Vec2 other ) {
		return new Vec2( x - other.x, y - other.y );
	}

	public void set(Vec2 myPos) {
		set( myPos.x, myPos.y );
	}

	public Vec2 add(Vec2 other ) {
		return new Vec2( x + other.x, y + other.y );
	}

	public Vec2 normalize() {
		Vec2 normalized = new Vec2( this );
		normalized.normalizeInPlace();
		
		return normalized;
	}

	private void normalizeInPlace() {
		float length = this.getLength();
		x = x / length;
		y = y / length;
		
	}

	private float getLength() {
		
		return (float)Math.sqrt( ( ( x * x ) + ( y * y ) ) );
	}

	public Vec2 scale( int factor ) {
		Vec2 scaled = new Vec2( this );
		scaled.scaleInPlace( factor );
		
		return scaled;
	}

	private void scaleInPlace(int factor) {
		x = x * factor;
		y = y * factor;	
	}

	public boolean isCloseEnoughToConsiderEqualTo(Vec2 position) {
		
		return ( Math.abs( x - position.x ) < 1.0f ) && ( Math.abs( y - position.y ) < 1.0f );
	}
}