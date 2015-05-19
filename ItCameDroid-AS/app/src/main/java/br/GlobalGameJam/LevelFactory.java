package br.GlobalGameJam;

import android.content.Context;
import android.content.res.Resources;
import br.DroidDemos.R;

public class LevelFactory {
	enum STATES {
		FLOOR1, MOM1, WALL1, EXIT1, BOMB1, HERO0_1
	};

	public static Level createRandomLevel(int i, int j, Resources resources, Context context ) {

		int[] tilePaletteIndexes = { R.drawable.cave_floor2, R.drawable.cave_floor };
		int[] wallPaletteIndexes = { R.drawable.cave2, R.drawable.cave };
		Level level = new Level(i, j, resources, tilePaletteIndexes, 5, wallPaletteIndexes,  2 );

		for (int x = 0; x < i; x++) {
			level.setTileType(x, 0, Constants.WALLRESID);
			level.setTileType(x, j - 1, Constants.WALLRESID);
		}

		for (int y = 0; y < j; y++) {
			level.setTileType(0, y, Constants.WALLRESID);
			level.setTileType(i - 1, y, Constants.WALLRESID);
		}

		level.miner = new Miner(resources, context );
		level.motherMonster = new MonsterMother[ 3 ];
		
		level.motherMonster[ 0 ] = new MonsterMother(resources, context );
		level.motherMonster[ 1 ] = new MonsterMother(resources, context );
		level.motherMonster[ 2 ] = new MonsterMother(resources, context );
		
		level.dynamite = new Dynamite(resources, context );
		int x;
		int y;

		for (int c = 0; c < 10; ++c) {

			x = (int) Math.abs(Math.random() * i);
			y = (int) Math.abs(Math.random() * j);

			positPillar(x, y, level, c);
		}

		for (int c = 0; c < 10; ++c) {

			x = (int) Math.abs(Math.random() * (i - 2)) + 1;
			y = (int) Math.abs(Math.random() * (j - 2)) + 1;

			level.setTileType(x, y, Constants.WALLRESID);
		}

		for (int c = 0; c < 7; ++c) {

			x = (int) Math.abs(Math.random() * (i - 2)) + 1;
			y = (int) Math.abs(Math.random() * (j - 2)) + 1;

			level.setTileType(x, y, Constants.BASETERRAINRESID);
		}

		
		x = i / 2;
		y = (j - 3) - 2;

		placeMonsterNest( 5, 5, level, 4, level.motherMonster[ 0 ] );
		placeMonsterNest( i - 5, j -5, level, 4, level.motherMonster[ 1 ] );
		placeMonsterNest( x, j / 2, level, 4, level.motherMonster[ 2 ] );
		
		placeExit(x, y, level, 4);

		return level;
	}

	private static void placeExit(int i, int j, Level level, int radius) {

		for (int a = i - radius; a < i + radius; ++a) {
			for (int b = j - radius; b < j + radius; ++b) {

				level.setTileType(a, b, Constants.BASETERRAINRESID);
			}
		}

		level.setTileType(i - 1, j - 2, Constants.WALLRESID);
		level.setTileType(i, j - 2, Constants.WALLRESID);
		level.setTileType(i + 1, j - 2, Constants.WALLRESID);
		level.setTileType(i + 2, j - 2, Constants.WALLRESID);

		level.addActor(i, j + 1, level.miner);
		level.setTileType( i, j + radius, Constants.BASETERRAINRESID);
		level.addActor(i, j + radius, level.dynamite);
	}

	private static void placeMonsterNest(int i, int j, Level level, int radius, MonsterMother mm ) {

		for (int a = i - radius; a < i + radius; ++a) {
			for (int b = j - radius; b < j + radius; ++b) {

				level.setTileType(a, b, Constants.BASETERRAINRESID);
			}
		}

		for (int y = 1; y < level.getHeight() - 1; ++y) {

			level.setTileType(i, y, Constants.BASETERRAINRESID);
		}

		level.addActor(i, j, mm );
	}

	private static void positPillar(int x, int y, Level level, int radius) {

		for (int i = -radius / 2; i < radius / 2; ++i) {
			for (int j = -radius / 2; j < radius / 2; ++j) {

				level.setTileType(x + i, y + j, Constants.WALLRESID);
			}
		}
	}
}
