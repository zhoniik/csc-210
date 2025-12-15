public class Main {
    public static void main(String[] args) {
        // Config (you can change these or make them flags)
        boolean useSwing = argsContain(args, "--swing");
        boolean useCLI   = argsContain(args, "--cli") || !useSwing;
        boolean vsAI     = argsContain(args, "--ai");          // human vs AI if present
        boolean aiFirst  = argsContain(args, "--ai-first");     // AI plays X if present

        TicTacToeGame game = new TicTacToeGame();
        Opponent ai = new MinimaxOpponent();
        TicTacToeGame.Mark aiAs = aiFirst ? TicTacToeGame.Mark.X : TicTacToeGame.Mark.O;

        if (useSwing) {
            TicTacToeUI ui = new SwingUI();
            ui.start(game, vsAI, ai, aiAs);
        } else if (useCLI) {
            TicTacToeUI ui = new CLIUI();
            ui.start(game, vsAI, ai, aiAs);
        }
    }

    private static boolean argsContain(String[] args, String flag) {
        if (args == null) return false;
        for (String a : args) if (flag.equalsIgnoreCase(a)) return true;
        return false;
    }
}

