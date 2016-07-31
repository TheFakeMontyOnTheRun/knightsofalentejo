package br.odb.knights;

import android.content.res.Resources;

public class TurtleKnight extends Knight {

    public TurtleKnight(Resources res) {
        super(R.drawable.turtle, 30, 6, res);
    }

    @Override
    public String toString() {
        return res.getText( R.string.turtle_knight ) + " - " + super.toString();
    }

    @Override
    public String getChar() {
        return String.valueOf(KnightsConstants.SPAWNPOINT_TURTLE);
    }

    @Override
    public GameScreenView.ETextures getTextureIndex() {
        return GameScreenView.ETextures.values()[ GameScreenView.ETextures.Turtle0.ordinal() + getStateFrame() ];
    }
}
