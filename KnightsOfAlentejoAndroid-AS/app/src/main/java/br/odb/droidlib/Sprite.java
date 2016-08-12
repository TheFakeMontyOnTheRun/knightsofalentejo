package br.odb.droidlib;


import br.odb.knights.GameViewGLES2;

public class Sprite implements Renderable {
    private int frameCount;
    int currentFrame = 0;
    private boolean visible = true;

    Sprite() {
        this.setFrameCount(1);
    }

    public void setFrame(int frame) {
        this.currentFrame = frame;
    }

    public int getCurrentFrame() {
        return currentFrame;
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
    }

    @Override
    public GameViewGLES2.ETextures getTextureIndex() {
        return GameViewGLES2.ETextures.None;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean b) {
        visible = b;
    }
}
