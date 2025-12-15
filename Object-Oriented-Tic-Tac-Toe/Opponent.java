public interface Opponent {
    /** Choose a move (0..8). Assumed to be called only on the opponent's turn and when the game is not over. */
    int chooseMove(TicTacToeGame game, TicTacToeGame.Mark me);
}

