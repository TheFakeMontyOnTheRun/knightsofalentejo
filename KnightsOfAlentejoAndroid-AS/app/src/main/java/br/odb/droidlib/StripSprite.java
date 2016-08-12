package br.odb.droidlib;

/**
 * @author monty
 */
public class StripSprite extends Sprite {

    public StripSprite() {
        super();
    }

    public void play() {
        this.setVisible(true);
        currentFrame = 0;
    }
}
