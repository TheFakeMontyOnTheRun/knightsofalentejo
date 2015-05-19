package br.DroidLib;

import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

public class Bitmap
{
	android.graphics.Bitmap sprite;
	private float x;
	private float y;
	
	public android.graphics.Bitmap getAndroidBitmap()
	{
		return sprite;
	}
	
	public Bitmap(Resources resources, int baseTypeId) 
	{
		/*
		Resources r = context.getResources();
		Drawable sprite= r.getDrawable(resId);
		int width = sprite.getIntrinsicWidth();
		int height = sprite.getIntrinsicHeight();
		bitmap = android.graphics.Bitmap.createBitmap(width, height, android.graphics.Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		sprite.setBounds(0, 0, width, height);
		sprite.draw(canvas);
	   */	
	
		sprite=BitmapFactory.decodeResource(resources, baseTypeId);
	}



	public Bitmap( android.graphics.Bitmap androidBitmap)
	{
		// TODO Auto-generated constructor stub
		sprite=androidBitmap;
	}

	/**
	 * @param x the x to set
	 */
	public void setX(float x)
	{
//		Rect rect=sprite.getBounds();
//		rect.left=(int) x;
//		sprite.setBounds(rect);
		this.x=x;
	}

	/**
	 * @return the x
	 */
	public float getX()
	{
	//	return sprite.getBounds().left;
		return x;
	}

	/**
	 * @param y the y to set
	 */
	public void setY(float y)
	{
//		Rect rect=sprite.getBounds();
//		rect.top=(int) y;
//		sprite.setBounds(rect);
		this.y=y;
	}

	/**
	 * @return the y
	 */
	public float getY()
	{
//		return sprite.getBounds().top;
		return this.y;
	}
	
	public void draw(Canvas canvas,Paint paint)
	{
		// TODO Auto-generated method stub
		
		canvas.drawBitmap(sprite, x, y, paint);
	}

	public void setAndroidBitmap(android.graphics.Bitmap tile)
	{
		// TODO Auto-generated method stub
		sprite=tile;
	}

	public void prepareForGC() {
		sprite = null;
	}	
}
