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

    public GameSession() {
        mCurrentLevel = 0;
	    mScore = 0;
    }

    public GameLevel obtainCurrentLevel(Resources res, int level) {

        mCurrentLevel = level;

        GameLevel toReturn = GameLevelLoader.loadLevel(mCurrentLevel, res);
        toReturn.reset(res);

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
}
