/**
 *
 */
package br.odb.droidlib;

import android.graphics.Canvas;

import br.odb.knights.GameScreenView;

/**
 * @author monty
 */
public interface Renderable {
    void draw(Canvas canvas, Vector2 camera);
    GameScreenView.ETextures getTextureIndex();
}
