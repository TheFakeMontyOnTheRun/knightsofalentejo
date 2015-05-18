package br.odb.droidlib;

import android.graphics.Bitmap;
import android.graphics.Canvas;

import br.odb.knights.Actor;

public class Tile implements Constants, Renderable {
    private int kind;
    final private Vector2 myPos;
    private boolean block;
    private Bitmap tileImage;
    private Renderable occupant;

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

    public Tile(int x, int y, int kind) {
        if (kind < 0)
            kind = 0;

        setKind(kind);
        myPos = new Vector2(x * TILE_SIZE_X, y * TILE_SIZE_Y);
    }

    public void draw(Canvas g, Vector2 camera) {
        g.drawBitmap(tileImage, myPos.x - (camera.x * TILE_SIZE_X), myPos.y - (camera.y * TILE_SIZE_Y), null);
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
}
