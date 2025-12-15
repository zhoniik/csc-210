public interface GameListener {
    /** Called after a successful move. */
    void onMove(int index, TicTacToeGame.Mark who);

    /** Called when the game ends. winner==EMPTY means draw. */
    void onGameOver(TicTacToeGame.Mark winner);

    /** Called after reset (starting indicates who's turn). */
    void onReset(TicTacToeGame.Mark starting);
}

