import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class TicTacToeGame {
    public enum Mark { X, O, EMPTY }

    private final Mark[] board = new Mark[9];
    private Mark current = Mark.X;
    private boolean gameOver = false;
    private Mark winner = Mark.EMPTY;
    private final List<GameListener> listeners = new ArrayList<>();

    public TicTacToeGame() { reset(); }

    public void reset() {
        Arrays.fill(board, Mark.EMPTY);
        current = Mark.X;
        winner = Mark.EMPTY;
        gameOver = false;
        fireReset(current);
    }

    /** Returns defensive copy of the board (index 0..8). */
    public Mark[] getBoard() { return board.clone(); }

    public Mark getCurrent() { return current; }

    public boolean isGameOver() { return gameOver; }

    public Mark getWinner() { return winner; } // EMPTY means no winner or draw during play; on gameOver & EMPTY = draw

    /** Attempt to play at index 0..8. Returns true if accepted. */
    public boolean play(int index) {
        if (gameOver || index < 0 || index > 8 || board[index] != Mark.EMPTY) return false;

        board[index] = current;
        fireMove(index, current);

        Mark w = computeWinner();
        if (w != Mark.EMPTY) {
            winner = w;
            gameOver = true;
            fireGameOver(winner);
        } else if (isFull()) {
            gameOver = true;
            winner = Mark.EMPTY; // draw
            fireGameOver(Mark.EMPTY);
        } else {
            current = (current == Mark.X ? Mark.O : Mark.X);
        }
        return true;
    }

    public List<Integer> legalMoves() {
        List<Integer> moves = new ArrayList<>();
        if (gameOver) return moves;
        for (int i = 0; i < 9; i++) if (board[i] == Mark.EMPTY) moves.add(i);
        return moves;
    }

    public void addListener(GameListener l) { listeners.add(Objects.requireNonNull(l)); }
    public void removeListener(GameListener l) { listeners.remove(l); }

    private void fireMove(int idx, Mark who) { for (var l : listeners) l.onMove(idx, who); }
    private void fireGameOver(Mark winner) { for (var l : listeners) l.onGameOver(winner); }
    private void fireReset(Mark starting) { for (var l : listeners) l.onReset(starting); }

    private boolean isFull() { for (Mark m : board) if (m == Mark.EMPTY) return false; return true; }

    private static final int[][] LINES = {
            {0,1,2},{3,4,5},{6,7,8},
            {0,3,6},{1,4,7},{2,5,8},
            {0,4,8},{2,4,6}
    };

    private Mark computeWinner() {
        for (int[] line : LINES) {
            Mark a = board[line[0]], b = board[line[1]], c = board[line[2]];
            if (a != Mark.EMPTY && a == b && b == c) return a;
        }
        return Mark.EMPTY;
    }
}

