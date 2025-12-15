import java.util.Scanner;

public class CLIUI implements TicTacToeUI {
    private TicTacToeGame game;
    private boolean vsAI;
    private Opponent ai;
    private TicTacToeGame.Mark aiAs;
    private final Scanner sc = new Scanner(System.in);

    @Override
    public void start(TicTacToeGame game, boolean vsAI, Opponent ai, TicTacToeGame.Mark aiPlaysAs) {
        this.game = game;
        this.vsAI = vsAI;
        this.ai = ai;
        this.aiAs = aiPlaysAs;

        game.addListener(this);
        draw(game.getBoard());

        while (!game.isGameOver()) {
            if (vsAI && game.getCurrent() == aiAs) {
                int move = ai.chooseMove(game, aiAs);
                game.play(move);
            } else {
                int move = promptMove();
                if (!game.play(move)) {
                    System.out.println("Illegal move. Try again.");
                }
            }
        }
        // gameOver message handled by onGameOver
    }

    private int promptMove() {
        while (true) {
            System.out.print("Player " + game.getCurrent() + " move (1-9): ");
            String s = sc.nextLine().trim();
            try {
                int v = Integer.parseInt(s) - 1;
                if (v >= 0 && v <= 8) return v;
            } catch (NumberFormatException ignored) {}
            System.out.println("Enter a number 1..9.");
        }
    }

    private void draw(TicTacToeGame.Mark[] b) {
        System.out.println();
        for (int r = 0; r < 3; r++) {
            int i = r * 3;
            System.out.printf(" %s | %s | %s %n", sym(b[i]), sym(b[i+1]), sym(b[i+2]));
            if (r < 2) System.out.println("---+---+---");
        }
        System.out.println();
    }

    private String sym(TicTacToeGame.Mark m) {
        return switch (m) { case X -> "X"; case O -> "O"; default -> " "; };
    }

    // GameListener
    @Override public void onMove(int index, TicTacToeGame.Mark who) { draw(game.getBoard()); }
    @Override public void onGameOver(TicTacToeGame.Mark winner) {
        if (winner == TicTacToeGame.Mark.EMPTY) System.out.println("Draw!");
        else System.out.println("Winner: " + winner);
    }
    @Override public void onReset(TicTacToeGame.Mark starting) {
        System.out.println("=== New Game. " + starting + " starts. ===");
    }
}

