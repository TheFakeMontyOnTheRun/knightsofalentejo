package br.odb.droidlib;

import android.graphics.Canvas;
import android.graphics.Region;

import java.util.ArrayList;

public class Layer implements Renderable {
    final protected ArrayList<Renderable> children = new ArrayList<Renderable>();
    public final Vector2 position = new Vector2();

    protected void add(Renderable d) {
        children.add(d);
    }

    public final Vector2 size = new Vector2();

    public void draw(Canvas canvas, Vector2 camera) {

        for (Renderable r : children) {
            canvas.clipRect(position.x, position.y, position.x + size.x, position.y + size.y, Region.Op.UNION);
            r.draw(canvas, position.add(camera));
        }
    }
}
