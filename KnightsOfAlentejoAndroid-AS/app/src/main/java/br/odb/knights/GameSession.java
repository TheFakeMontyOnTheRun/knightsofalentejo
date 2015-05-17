/**
 * 
 */
package br.odb.knights;

import android.content.res.Resources;

/**
 * @author monty
 *
 */
public class GameSession {
	byte currentLevel;
	byte dificulty;
	
	public GameSession() {
		currentLevel = 0;
		dificulty = 0;
	}
	
	public void close() {
		
	}

	public GameLevel obtainCurrentLevel( Resources res, byte level ) {
		// obtem ou cria o level corrente
		GameLevel toReturn = null;
		currentLevel = level;
		toReturn = GameLevelLoader.loadLevel( currentLevel, res );
		toReturn.setDificulty( dificulty );
		toReturn.reset( res );
		
		return toReturn;
	}

	public GameLevel obtainCurrentLevel(Resources res, String data, GameLevel level ) {
		// obtem ou cria o level corrente
		GameLevel toReturn = null;
		toReturn = GameLevelLoader.loadLevel( data, res, level );
		toReturn.reset( res );
		
		return toReturn;
	}
}
