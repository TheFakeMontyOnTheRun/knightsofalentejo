/**
 *
 */
package br.odb.droidlib;

import android.graphics.Canvas;

/**
 * @author monty
 */
public interface Renderable {
    void draw(Canvas canvas, Vector2 camera);
    int getTextureIndex();
}
