import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class MultispeciesSimulationUI extends JFrame {
    private SimulationPanel simPanel;

    public MultispeciesSimulationUI() {
        setTitle("Multispecies Continuous Simulation with Sliders and Phase Plot");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        simPanel = new SimulationPanel();
        add(simPanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));

        // --- Growth Rates ---
        JPanel growthPanel = new JPanel();
        growthPanel.setLayout(new BoxLayout(growthPanel, BoxLayout.Y_AXIS));
        growthPanel.setBorder(BorderFactory.createTitledBorder("Growth Rates"));

        growthPanel.add(simPanel.makeSlider("r_C", 10, 300, 150, val -> simPanel.rC = val / 100.0));
        growthPanel.add(simPanel.makeSlider("r_R", 10, 300, 100, val -> simPanel.rR = val / 100.0));
        growthPanel.add(simPanel.makeSlider("r_W", 10, 200, 50, val -> simPanel.rW = val / 100.0));

        controlPanel.add(growthPanel);

        // --- Interaction Terms ---
        JPanel interactionPanel = new JPanel();
        interactionPanel.setLayout(new BoxLayout(interactionPanel, BoxLayout.Y_AXIS));
        interactionPanel.setBorder(BorderFactory.createTitledBorder("Interaction Terms"));

        interactionPanel.add(simPanel.makeSlider("a (R eats C)", 10, 300, 120, val -> simPanel.a = val / 100.0));
        interactionPanel.add(simPanel.makeSlider("b (R gains from C)", 10, 300, 60, val -> simPanel.b = val / 100.0));
        interactionPanel.add(simPanel.makeSlider("c (W eats R)", 10, 300, 70, val -> simPanel.c = val / 100.0));
        interactionPanel.add(simPanel.makeSlider("d (W gains from R)", 10, 300, 40, val -> simPanel.d = val / 100.0));

        controlPanel.add(interactionPanel);

        // --- Buttons ---
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        JButton rerun = new JButton("Run Simulation");
        rerun.addActionListener(e -> {
            simPanel.simulate();
            simPanel.repaint();
        });
        buttonPanel.add(rerun);

        JButton plotPhase = new JButton("Show 3D Phase Plot");
        plotPhase.addActionListener(e -> {
            simPanel.simulate();
            simPanel.repaint();
            PhasePanel phase = new PhasePanel(simPanel.R, simPanel.W, simPanel.C);
            JFrame pf = new JFrame("3D Phase Plot");
            pf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            pf.setContentPane(phase);
            pf.pack();
            pf.setLocationRelativeTo(null);
            pf.setVisible(true);
        });
        buttonPanel.add(plotPhase);

        JButton reset = new JButton("Reset to Defaults");
        reset.addActionListener(e -> {
            dispose();
            new MultispeciesSimulationUI();
        });
        buttonPanel.add(reset);

        controlPanel.add(buttonPanel);

        add(controlPanel, BorderLayout.EAST);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MultispeciesSimulationUI::new);
    }
}

class SimulationPanel extends JPanel {
    ArrayList<Double> C = new ArrayList<>();
    ArrayList<Double> R = new ArrayList<>();
    ArrayList<Double> W = new ArrayList<>();

    double rC = 1.5, KC = 1.0;
    double rR = 1.0, KR = 1.0;
    double rW = 0.5, KW = 1.0;

    double a = 1.2, b = 0.6, c = 0.7, d = 0.4;
    double DT = 0.01, TMAX = 50;

    public SimulationPanel() {
        setPreferredSize(new Dimension(900, 500));
        simulate();
    }

    public void simulate() {
        C.clear(); R.clear(); W.clear();
        double c = 0.9, r = 0.5, w = 0.2;
        double t = 0;

        while (t <= TMAX) {
            C.add(c);
            R.add(r);
            W.add(w);

            double dc = rC * c * (1 - c / KC) - a * c * r;
            double dr = rR * r * (1 - r / KR) + b * c * r - this.c * r * w;
            double dw = rW * w * (1 - w / KW) + d * r * w;

            c = Math.max(0, c + dc * DT);
            r = Math.max(0, r + dr * DT);
            w = Math.max(0, w + dw * DT);
            t += DT;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int margin = 50;
        int graphWidth = getWidth() - 2 * margin;
        int graphHeight = getHeight() - 2 * margin;

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.LIGHT_GRAY);
        g.drawRect(margin, margin, graphWidth, graphHeight);
        g.setColor(Color.BLACK);
        g.drawString("Carrots (Green), Rabbits (Blue), Wolves (Red)", margin, 20);

        drawLine(g, C, margin, margin, graphWidth, graphHeight, Color.GREEN);
        drawLine(g, R, margin, margin, graphWidth, graphHeight, Color.BLUE);
        drawLine(g, W, margin, margin, graphWidth, graphHeight, Color.RED);
    }

    private void drawLine(Graphics g, ArrayList<Double> data, int x, int y, int w, int h, Color color) {
        g.setColor(color);
        double max = 2.0;
        int prevX = x;
        int prevY = y + h - (int)(data.get(0) / max * h);
        for (int i = 1; i < data.size(); i++) {
            int curX = x + i * w / data.size();
            int curY = y + h - (int)(data.get(i) / max * h);
            g.drawLine(prevX, prevY, curX, curY);
            prevX = curX;
            prevY = curY;
        }
    }

    public JPanel makeSlider(String label, int min, int max, int init, java.util.function.IntConsumer update) {
        JPanel panel = new JPanel(new BorderLayout());

        JLabel valueLabel = new JLabel(label + " = " + (init / 100.0));
        panel.add(valueLabel, BorderLayout.NORTH);

        JSlider slider = new JSlider(min, max, init);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setMajorTickSpacing((max - min) / 5);
        slider.setMinorTickSpacing((max - min) / 10);

        slider.addChangeListener(e -> {
            double val = slider.getValue() / 100.0;
            valueLabel.setText(label + " = " + val);
            update.accept(slider.getValue());
        });

        panel.add(slider, BorderLayout.CENTER);
        return panel;
    }
}

class PhasePanel extends JPanel {
    ArrayList<Double> rabbits, wolves, carrots;

    public PhasePanel(ArrayList<Double> R, ArrayList<Double> W, ArrayList<Double> C) {
        this.rabbits = R;
        this.wolves = W;
        this.carrots = C;
        setPreferredSize(new Dimension(500, 500));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int margin = 50;
        int size = getWidth() - 2 * margin;

        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(Color.BLACK);
        g.drawRect(margin, margin, size, size);
        g.drawString("Rabbits (X), Wolves (Y), Carrots (Color)", margin, 20);

        for (int i = 0; i < rabbits.size(); i++) {
            double r = Math.min(rabbits.get(i), 2.0);
            double w = Math.min(wolves.get(i), 2.0);
            double c = Math.min(carrots.get(i), 2.0);

            int x = margin + (int) (r / 2.0 * size);
            int y = margin + size - (int) (w / 2.0 * size);

            float hue = (float) (0.33 * (1.0 - c / 2.0));
            g.setColor(Color.getHSBColor(hue, 1f, 1f));
            g.fillRect(x, y, 2, 2);
        }
    }
}

