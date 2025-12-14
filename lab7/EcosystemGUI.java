import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class EcosystemGUI {

    private int rows = 10;
    private int cols = 10;

    private JButton[][] buttons;
    private JTextArea sideInfo;
    private JTextArea logArea;
    private JLabel status;

    private Timer playTimer;
    private boolean playing = false;

    private WorldAdapter world;

    public EcosystemGUI() {
        world = new DummyWorld(rows, cols);
        makeWindow();
        refreshGridText();
    }

    private void makeWindow() {
        JFrame f = new JFrame("Ecosystem GUI");
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(600, 400);
        f.setVisible(true);

        JPanel top = new JPanel();
        JButton stepBtn = new JButton("Step");
        JButton playBtn = new JButton("Play");
        JButton loadBtn = new JButton("Load");
        JButton resetBtn = new JButton("Reset");
        JButton saveBtn = new JButton("Save");
        top.add(stepBtn);
        top.add(playBtn);
        top.add(loadBtn);
        top.add(resetBtn);
        top.add(saveBtn);
        f.add(top, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(rows, cols));
        buttons = new JButton[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                JButton b = new JButton();
                b.setMargin(new Insets(1, 1, 1, 1));
                final int rr = r;
                final int cc = c;
                b.addActionListener(e -> showCell(rr, cc));
                buttons[r][c] = b;
                grid.add(b);
            }
        }

        JPanel right = new JPanel(new BorderLayout());
        sideInfo = new JTextArea(8, 18);
        sideInfo.setEditable(false);
        sideInfo.setLineWrap(true);
        sideInfo.setWrapStyleWord(true);
        right.add(new JScrollPane(sideInfo), BorderLayout.NORTH);

        logArea = new JTextArea(14, 18);
        logArea.setEditable(false);
        logArea.setLineWrap(true);
        logArea.setWrapStyleWord(true);
        right.add(new JScrollPane(logArea), BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, grid, right);
        split.setResizeWeight(0.75);
        f.add(split, BorderLayout.CENTER);

        status = new JLabel("ready");
        f.add(status, BorderLayout.SOUTH);

        stepBtn.addActionListener(e -> doStep());
        playBtn.addActionListener(e -> togglePlay(playBtn));
        loadBtn.addActionListener(e -> doLoad());
        resetBtn.addActionListener(e -> doReset());
        saveBtn.addActionListener(e -> doSave());

        playTimer = new Timer(300, e -> doStep());

        f.pack();
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    private void doStep() {
        world.takeTurn();
        appendLog(world.getTurnLog());
        refreshGridText();
        status.setText("turn " + world.getTurnNumber());
    }

    private void togglePlay(JButton playBtn) {
        if (playing) {
            playing = false;
            playTimer.stop();
            playBtn.setText("Play");
            status.setText("paused");
        } else {
            playing = true;
            playTimer.start();
            playBtn.setText("Pause");
            status.setText("playing…");
        }
    }

    private void doLoad() {
        JFileChooser fc = new JFileChooser();
        int res = fc.showOpenDialog(null);
        if (res == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            world.loadFromFile(f);
            appendLog("loaded: " + f.getName());
            refreshGridText();
        }
    }

    private void doReset() {
        world.reset();
        appendLog("reset to initial state");
        refreshGridText();
        status.setText("reset");
    }

    private void doSave() {
        JFileChooser fc = new JFileChooser();
        int res = fc.showSaveDialog(null);
        if (res == JFileChooser.APPROVE_OPTION) {
            File f = fc.getSelectedFile();
            world.saveToFile(f);
            appendLog("saved: " + f.getName());
        }
    }

    private void showCell(int r, int c) {
        String a = world.getCellSummary(r, c);
        String b = world.getCreaturesInfo(r, c);
        sideInfo.setText("Cell (" + r + "," + c + ")\n" + a + "\n\n" + b);
    }

    private void refreshGridText() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                buttons[r][c].setText(world.getCellShort(r, c));
            }
        }
    }

    private void appendLog(String s) {
        if (s == null || s.isEmpty()) return;
        logArea.append(s + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(EcosystemGUI::new);
    }
}
