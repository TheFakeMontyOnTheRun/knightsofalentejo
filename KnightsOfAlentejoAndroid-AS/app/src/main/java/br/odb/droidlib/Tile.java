package br.odb.droidlib;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import br.odb.knights.Actor;
import br.odb.knights.Knight;

public class Tile implements Renderable {
    private int kind;
    final private Vector2 myPos;
    private boolean block;
    private Bitmap tileImage;
    private Renderable occupant;
    public int textureId;
    /**
     * @return the block
     */
    public boolean isBlock() {
        return block;
    }

    public void setImage(Bitmap bitmap) {
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
        block = (kind != 0) && (kind != 3);
    }

    public Tile(int x, int y, int kind, Bitmap image ) {
        if (kind < 0) {
            kind = 0;
        }

        tileImage = image;

        setKind(kind);
        myPos = new Vector2(x * tileImage.getWidth(), y * tileImage.getHeight());
    }

    public void draw(Canvas g, Vector2 camera) {
        g.drawBitmap(tileImage, myPos.x - (camera.x * tileImage.getWidth()), myPos.y - (camera.y * tileImage.getHeight()), null);
    }

    @Override
    public int getTextureIndex() {

        if ( occupant != null ) {

	        if ( occupant instanceof Knight && ((Knight)occupant).hasExited  ) {
		        return textureId;
	        }

            return occupant.getTextureIndex();
        } else {
            return textureId;
        }
    }

    public Vector2 getPosition() {
        return myPos;
    }

    public Renderable getOccupant() {
        return occupant;
    }

    public void setOccupant(Actor actor) {
        occupant = actor;
    }

    public int getWidth() {
        return tileImage.getWidth();
    }

    public int getHeight() {
        return tileImage.getHeight();
    }
}
