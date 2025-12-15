import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class SwingUI implements TicTacToeUI {
    private TicTacToeGame game;
    private boolean vsAI;
    private Opponent ai;
    private TicTacToeGame.Mark aiAs;

    private final JButton[] cells = new JButton[9];
    private final JLabel status = new JLabel(" ");
    private final JFrame frame = new JFrame("Tic-Tac-Toe");

    @Override
    public void start(TicTacToeGame game, boolean vsAI, Opponent ai, TicTacToeGame.Mark aiPlaysAs) {
        this.game = game;
        this.vsAI = vsAI;
        this.ai = ai;
        this.aiAs = aiPlaysAs;

        SwingUtilities.invokeLater(() -> {
            initUi();
            game.addListener(this);
            updateFromBoard();
            frame.setVisible(true);
            maybeAIMove(); // in case AI starts
        });
    }

    private void initUi() {
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout(8,8));

        JPanel grid = new JPanel(new GridLayout(3,3,6,6));
        Font f = new Font(Font.SANS_SERIF, Font.BOLD, 42);

        for (int i = 0; i < 9; i++) {
            final int idx = i;
            JButton btn = new JButton(" ");
            btn.setFont(f);
            btn.setFocusPainted(false);
            btn.addActionListener(e -> onCell(idx));
            cells[i] = btn;
            grid.add(btn);
        }

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        status.setFont(status.getFont().deriveFont(Font.BOLD));
        top.add(status);

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton reset = new JButton(new AbstractAction("Reset") {
            @Override public void actionPerformed(ActionEvent e) { game.reset(); maybeAIMove(); }
        });
        bottom.add(reset);

        frame.add(top, BorderLayout.NORTH);
        frame.add(grid, BorderLayout.CENTER);
        frame.add(bottom, BorderLayout.SOUTH);
        frame.setSize(360, 420);
        frame.setLocationByPlatform(true);
    }

    private void onCell(int idx) {
        if (game.isGameOver()) return;
        if (vsAI && game.getCurrent() == aiAs) return; // wait for AI
        if (game.play(idx)) { maybeAIMove(); }
    }

    private void maybeAIMove() {
        if (!vsAI || game.isGameOver()) return;
        if (game.getCurrent() != aiAs) return;
        // small delay for UX
        Timer t = new Timer(200, e -> {
            int move = ai.chooseMove(game, aiAs);
            game.play(move);
        });
        t.setRepeats(false);
        t.start();
    }

    private void updateFromBoard() {
        var b = game.getBoard();
        for (int i = 0; i < 9; i++) {
            String text = switch (b[i]) { case X -> "X"; case O -> "O"; default -> " "; };
            cells[i].setText(text);
            cells[i].setEnabled(!game.isGameOver() && b[i] == TicTacToeGame.Mark.EMPTY);
        }
        if (game.isGameOver()) {
            if (game.getWinner() == TicTacToeGame.Mark.EMPTY) status.setText("Draw. Click Reset.");
            else status.setText("Winner: " + game.getWinner() + ". Click Reset.");
        } else {
            status.setText("Turn: " + game.getCurrent() + (vsAI ? (", AI as " + aiAs) : ""));
        }
    }

    // GameListener
    @Override public void onMove(int index, TicTacToeGame.Mark who) { updateFromBoard(); }
    @Override public void onGameOver(TicTacToeGame.Mark winner) { updateFromBoard(); }
    @Override public void onReset(TicTacToeGame.Mark starting) { updateFromBoard(); }
}

