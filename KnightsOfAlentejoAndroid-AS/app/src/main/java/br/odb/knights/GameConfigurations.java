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
		GameSession session = getCurrentGameSession();
		if ( session != null ) {
			session.resetScore();
		}
	}

    public GameSession getCurrentGameSession() {
        return currentGameSession;
    }

    public void startNewSession() {
        startNewSession(0);
    }

    public void startNewSession( int commulatedScore) {
        currentGameSession = new GameSession();
        currentGameSession.addtoScore(commulatedScore);
    }
}
