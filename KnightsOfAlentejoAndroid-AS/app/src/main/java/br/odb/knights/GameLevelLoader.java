package br.odb.knights;

import android.content.res.Resources;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class GameLevelLoader {

    public static final int NUMBER_OF_LEVELS = 6;

    public static GameLevel loadLevel(int currentLevel, Resources res) {
        int[][] map = null;
        InputStream in;

        switch (currentLevel) {
            case 1:
                in = res.openRawResource(R.raw.map_tiles1);
                break;
            case 2:
                in = res.openRawResource(R.raw.map_tiles2);
                break;
            case 3:
                in = res.openRawResource(R.raw.map_tiles3);
                break;
            case 4:
                in = res.openRawResource(R.raw.map_tiles4);
                break;
            case 5:
                in = res.openRawResource(R.raw.map_tiles5);
                break;
            case 6:
                in = res.openRawResource(R.raw.map_tiles6);
                break;

            default:
                in = res.openRawResource(R.raw.map_tiles0);
        }

        DataInputStream dis = new DataInputStream(in);

        int buffer;
        int lenX;
        int lenY;

        try {
            lenX = 20;
            lenY = 20;

            map = new int[lenY][lenX];

            for (int c = 0; c < lenX; ++c) {
                for (int d = 0; d < lenY; ++d) {
                    buffer = dis.read();
                    map[d][c] = buffer - '0';
                }

                in.skip(1); // skip the \n
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new GameLevel(map, res);
    }
}
