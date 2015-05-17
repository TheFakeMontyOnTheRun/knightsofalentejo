package br.odb.knights;

public class GameConfigurations {

	public static GameConfigurations getInstance() {

		if (instance == null) {

			instance = new GameConfigurations();
		}

		return instance;
	}

	private GameSession currentGameSession;
	private static GameConfigurations instance = null;

	public GameConfigurations() {

	}

	public GameSession getCurrentGameSession() {
		return currentGameSession;
	}

	public void startNewSession() {

		if (currentGameSession != null) {

			currentGameSession.close();
		}

		currentGameSession = new GameSession();
	}

}
