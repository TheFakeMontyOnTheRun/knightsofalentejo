package br.odb.knights;

import android.content.res.Resources;

import java.io.InputStream;
import java.util.Scanner;

import br.odb.menu.GameActivity;

public class GameLevelLoader {

    public static final int NUMBER_OF_LEVELS = 6;

    public static GameLevel loadLevel(int currentLevel, Resources res, GameActivity.GameDelegate delegate, GameViewGLES2.GameRenderer renderer) {

        InputStream in = switch (currentLevel) {
            case 1 -> res.openRawResource(R.raw.map_tiles1);
            case 2 -> res.openRawResource(R.raw.map_tiles2);
            case 3 -> res.openRawResource(R.raw.map_tiles3);
            case 4 -> res.openRawResource(R.raw.map_tiles4);
            case 5 -> res.openRawResource(R.raw.map_tiles5);
            case 6 -> res.openRawResource(R.raw.map_tiles6);
            default -> res.openRawResource(R.raw.map_tiles0);
        };

        Scanner scanner = new Scanner(in);

        int[][] map = new int[GameLevel.MAP_SIZE][];

        int c = 0;

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();

            int[] mapLine = new int[line.length()];
            int d = 0;

            for (byte b : line.getBytes()) {
                mapLine[d++] = b - '0';
            }
            map[c++] = mapLine;
        }

        return new GameLevel(map, currentLevel, delegate, renderer);
    }
}
