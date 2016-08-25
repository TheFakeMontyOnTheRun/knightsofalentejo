/**
 *
 */
package br.odb.knights;

import android.content.res.Resources;

/**
 * @author monty
 */
public class GameSession {

    private int mCurrentLevel;
    private int mScore;
	private GameLevel mRestoredLevel = null;

	public GameSession() {
        mCurrentLevel = 0;
	    mScore = 0;
    }

    public GameLevel obtainCurrentLevel(Resources res, int level) {

        mCurrentLevel = level;
	    GameLevel toReturn;

	    if ( mRestoredLevel != null && mRestoredLevel.getLevelNumber() == level ) {
		    toReturn = mRestoredLevel;
		    mRestoredLevel = null;
	    } else {
		    toReturn = GameLevelLoader.loadLevel(mCurrentLevel, res);
		    toReturn.reset();
	    }

        return toReturn;
    }

    public int getScore() {
        return mScore;
    }

    public void resetScore() {
        this.mScore = 0;
    }

	public void addtoScore( int extra ) {
		mScore += extra;
	}

	public void restoreFromLevel(GameLevel level) {
		mRestoredLevel = level;
	}
}
