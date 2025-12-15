public interface TicTacToeUI extends GameListener {
    /** Start interacting with the given game. Implementations should register themselves as listeners. */
    void start(TicTacToeGame game, boolean vsAI, Opponent ai, TicTacToeGame.Mark aiPlaysAs);
}

