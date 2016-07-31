package br.odb.knights;

import android.content.res.Resources;

public class EagleKnight extends Knight {
    public EagleKnight(Resources res) {
        super(R.drawable.falcon, 25, 10, res);
    }

    @Override
    public String toString() {
        return res.getText( R.string.falcon_knight ) + " - " + super.toString();
    }

    @Override
    public String getChar() {
        return String.valueOf(KnightsConstants.SPAWNPOINT_EAGLE);
    }

    @Override
    public GameScreenView.ETextures getTextureIndex() {
        return GameScreenView.ETextures.values()[ GameScreenView.ETextures.Falcon0.ordinal() + getStateFrame() ];
    }
}
