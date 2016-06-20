package br.odb.droidlib;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Region;


public class Sprite implements Renderable {
    final private int frameHeight;
    private int frameWidth;
    private int frameCount;
    int currentFrame = 0;
    final private Bitmap image;
    final private Vector2 pos;
    private boolean visible = true;

    Sprite(Bitmap image) {
        this.image = image;
        this.frameHeight = image.getHeight();
        this.frameWidth = image.getWidth();
        this.pos = new Vector2();
        this.setFrameCount(1);
    }

    public void setFrame(int frame) {
        this.currentFrame = frame;
    }

    public void nextFrame() {
        if (currentFrame != frameCount - 1)
            currentFrame++;
        else {
            currentFrame = 0;
            setVisible(false);
        }
    }

    public void setFrameCount(int frameCount) {
        this.frameCount = frameCount;
        this.frameWidth = image.getWidth() / frameCount;
    }


    @Override
    public void draw(Canvas g, Vector2 camera) {

        if (!visible)
            return;

        g.save();

        int vx = 0;
        int vy = 0;
        int offset = frameWidth * currentFrame;

        RectF rectf = new RectF();

        rectf.left = pos.x - (camera.x * frameWidth);
        rectf.top = pos.y - (camera.y * frameHeight);
        rectf.right = pos.x + frameWidth - (camera.x * frameWidth);
        rectf.bottom = pos.y + frameHeight - (camera.y * frameHeight);

        g.clipRect(rectf, Region.Op.INTERSECT);
        Paint paint = new Paint();
        g.drawBitmap(image, pos.x + vx - offset - (camera.x * frameWidth), pos.y + vy - (camera.y * frameHeight), paint);

        g.restore();
    }

    @Override
    public int getTextureIndex() {
        return 0;
    }

    public void setPosition(Vector2 p) {

        if (p == null)
            return;

        pos.x = p.x * frameWidth;
        pos.y = p.y * frameHeight;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean b) {
        visible = b;
    }

    public Vector2 getPosition() {
        return pos;
    }

    public int getFrameWidth() {
        return frameWidth;
    }

    public int getFrameHeight() {
        return frameHeight;
    }
}
