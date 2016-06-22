package br.odb.knights;

import android.content.Context;
import android.view.KeyEvent;
import android.view.ViewManager;

import br.odb.droidlib.Tile;
import br.odb.droidlib.Updatable;

/**
 * Created by monty on 18/06/16.
 */
public interface GameScreenView {

	public abstract void init(Context context, Updatable updateDelegate, int level);

	public abstract void centerOn(Actor selectedPlayer);

	public abstract void handleKeys(boolean[] keyMap);

	ViewManager getParentViewManager();

	void stopRunning();

	void setIsPlaying(boolean isPlaying);

	GameLevel getCurrentLevel();

	int getExitedKnights();

	Actor getSelectedPlayer();

	void setSelectedPlayer(Actor knight);

	void setSelectedTile(Tile tile);

	boolean[] getKeyMap();

	boolean onKeyUp(int keyCode, KeyEvent event);

	boolean onKeyDown(int keyCode, KeyEvent event);

	void onPause();

	void onResume();
}
