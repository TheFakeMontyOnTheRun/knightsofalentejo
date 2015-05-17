package br.odb.knights;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import br.odb.droidlib.Renderable;
import android.content.res.Resources;

public class GameLevelLoader {

	public static final byte LIMIT = 4;

	public static GameLevel loadLevel(byte currentLevel, Resources res) {
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
		default:
			in = res.openRawResource(R.raw.map_tiles0);
		}

		DataInputStream dis = new DataInputStream(in);
		int buffer = 0;
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
				in.skip(1); // pula o \n
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		GameLevel toReturn = new GameLevel(map, res);

		return toReturn;
	}

	public static GameLevel loadLevel(String data, Resources res, GameLevel level ) {

		level.entities.clear();
		level.remainingMonsters = 0;
		String[] entries = data.split("\\|");
		String[] datum;
		int character;
		int c;
		int d;
		int h;
		Actor a;
		
		ArrayList< Renderable > toDelete = new ArrayList<Renderable>();
		
		for ( int x = 0; x < level.getGameWidth(); ++x ) {
			for ( int y = 0; y < level.getGameHeight(); ++y ) {
				level.setOcupant( x, y, null );
			}
		}
		
		for ( Renderable r : level.children ) {
			if ( r instanceof Actor ) {
				toDelete.add( r );
			}
		}
		

		level.children.removeAll( toDelete );

		
		for ( String entry : entries ) {
			
			datum = entry.split("\\,");
			
			character = Integer.parseInt( datum[ 0 ] );
			a = null;;
			c = Integer.parseInt( datum[ 1 ] );
			d = Integer.parseInt( datum[ 2 ] );
			h = Integer.parseInt( datum[ 3 ] );

			switch (character) {
			case KnightsConstans.SPAWNPOINT_BAPHOMET:
				a = level.addEntity(new Baphomet(res), c, d, h );
				++level.remainingMonsters;
				break;
			case KnightsConstans.SPAWNPOINT_BULL:
				a = level.addEntity(new BullKnight(res), c, d, h );
				break;
			case KnightsConstans.SPAWNPOINT_TURTLE:
				a = level.addEntity(new TurleKnight(res), c, d, h );
				break;
			case KnightsConstans.SPAWNPOINT_EAGLE:
				a = level.addEntity(new EagleKnight(res), c, d, h );
				break;
			case KnightsConstans.SPAWNPOINT_CUCO:
				a = level.addEntity(new Cuco(res), c, d, h );
				++level.remainingMonsters;
				break;
			case KnightsConstans.SPAWNPOINT_MOURA:
				a = level.addEntity(new Moura(res), c, d, h );
				++level.remainingMonsters;
				break;
			case KnightsConstans.SPAWNPOINT_DEVIL:
				a = level.addEntity(new Demon(res), c, d, h );
				++level.remainingMonsters;
				break;
			}
			
			if ( h <= 0 ) {
				a.kill();
			}			
		}
		


		

		return level;
	}
}
