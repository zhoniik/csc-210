import java.util.*;

/**
 * Console Tic-Tac-Toe
 * - Modes: Human vs Human, Human vs AI (unbeatable)
 * - Input squares 1..9 (left-to-right, top-to-bottom)
 */
public class ProceduralTicTacToe {
    private static final char EMPTY = ' ';
    private static final char X = 'X';
    private static final char O = 'O';
    private static final int[][] LINES = {
            {0,1,2},{3,4,5},{6,7,8}, // rows
            {0,3,6},{1,4,7},{2,5,8}, // cols
            {0,4,8},{2,4,6}          // diagonals
    };

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("=== Tic-Tac-Toe ===");
        System.out.println("1) Human vs Human");
        System.out.println("2) Human vs AI (unbeatable)");
        int mode = promptInt(sc, "Choose mode (1-2): ", 1, 2);

        char[] board = new char[9];
        Arrays.fill(board, EMPTY);

        boolean vsAI = (mode == 2);
        char human = X; // human always goes first as X
        char ai = O;

        printBoard(board);
        char turn = X;

        while (true) {
            if (turn == human || !vsAI) {
                // Human move (or both human in HvH)
                int move = promptMove(sc, board, turn);
                board[move] = turn;
            } else {
                // AI move
                int move = bestMove(board, ai, human);
                board[move] = ai;
                System.out.println("AI plays at " + (move + 1));
            }

            printBoard(board);

            Character winner = checkWinner(board);
            if (winner != null) {
                if (winner == X || winner == O) {
                    System.out.println("Winner: " + winner);
                } else {
                    System.out.println("It's a draw!");
                }
                break;
            }

            turn = (turn == X ? O : X);
        }
        sc.close();
    }

    private static int promptMove(Scanner sc, char[] board, char player) {
        while (true) {
            int move = promptInt(sc, "Player " + player + " move (1-9): ", 1, 9) - 1;
            if (board[move] == EMPTY) return move;
            System.out.println("Square taken. Try again.");
        }
    }

    private static int promptInt(Scanner sc, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String line = sc.nextLine().trim();
            try {
                int v = Integer.parseInt(line);
                if (v >= min && v <= max) return v;
            } catch (NumberFormatException ignored) {}
            System.out.println("Enter a number between " + min + " and " + max + ".");
        }
    }

    private static void printBoard(char[] b) {
        System.out.println();
        for (int r = 0; r < 3; r++) {
            int i = r * 3;
            System.out.printf(" %s | %s | %s %n",
                    disp(b[i]), disp(b[i+1]), disp(b[i+2]));
            if (r < 2) System.out.println("---+---+---");
        }
        System.out.println();
        System.out.println("Positions:   1 | 2 | 3");
        System.out.println("             4 | 5 | 6");
        System.out.println("             7 | 8 | 9");
        System.out.println();
    }

    private static String disp(char c) {
        return c == EMPTY ? " " : String.valueOf(c);
    }

    /** Returns X, O, 'D' for draw, or null if game continues */
    private static Character checkWinner(char[] b) {
        for (int[] line : LINES) {
            if (b[line[0]] != EMPTY &&
                b[line[0]] == b[line[1]] &&
                b[line[1]] == b[line[2]]) {
                return b[line[0]];
            }
        }
        if (isFull(b)) return 'D';
        return null;
    }

    private static boolean isFull(char[] b) {
        for (char c : b) if (c == EMPTY) return false;
        return true;
    }

    // ====== Unbeatable AI via Minimax with simple pruning ======
    private static int bestMove(char[] board, char ai, char human) {
        // If first AI move, prefer center, else a corner, to speed up
        if (countEmpty(board) == 9 && board[4] == EMPTY) return 4;
        if (countEmpty(board) == 8 && board[4] == EMPTY) return 4;

        int bestScore = Integer.MIN_VALUE;
        int move = -1;

        for (int i = 0; i < 9; i++) {
            if (board[i] == EMPTY) {
                board[i] = ai;
                int score = minimax(board, false, ai, human, Integer.MIN_VALUE, Integer.MAX_VALUE);
                board[i] = EMPTY;
                if (score > bestScore) {
                    bestScore = score;
                    move = i;
                }
            }
        }
        return move;
    }

    private static int minimax(char[] b, boolean isMax, char ai, char human, int alpha, int beta) {
        Character result = checkWinner(b);
        if (result != null) {
            if (result == ai) return +10;
            if (result == human) return -10;
            return 0; // draw
        }

        if (isMax) {
            int best = Integer.MIN_VALUE;
            for (int i = 0; i < 9; i++) {
                if (b[i] == EMPTY) {
                    b[i] = ai;
                    int val = minimax(b, false, ai, human, alpha, beta);
                    b[i] = EMPTY;
                    best = Math.max(best, val);
                    alpha = Math.max(alpha, val);
                    if (beta <= alpha) break; // prune
                }
            }
            return best;
        } else {
            int best = Integer.MAX_VALUE;
            for (int i = 0; i < 9; i++) {
                if (b[i] == EMPTY) {
                    b[i] = human;
                    int val = minimax(b, true, ai, human, alpha, beta);
                    b[i] = EMPTY;
                    best = Math.min(best, val);
                    beta = Math.min(beta, val);
                    if (beta <= alpha) break; // prune
                }
            }
            return best;
        }
    }

    private static int countEmpty(char[] b) {
        int c = 0;
        for (char v : b) if (v == EMPTY) c++;
        return c;
    }
}

