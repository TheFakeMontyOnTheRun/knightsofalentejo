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

	private int currentLevel;
	
	public GameSession() {
		currentLevel = 0;
	}

	public GameLevel obtainCurrentLevel( Resources res, int level ) {

		currentLevel = level;

		GameLevel toReturn = GameLevelLoader.loadLevel( currentLevel, res );
		toReturn.reset( res );
		
		return toReturn;
	}
}
