import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.util.ArrayList;

public class InteractiveLogisticGrowth extends JFrame {
    private LogisticPanel graphPanel;

    public InteractiveLogisticGrowth() {
        setTitle("Interactive Continuous Logistic Growth");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 650);
        setLayout(new BorderLayout());

        graphPanel = new LogisticPanel();
        add(graphPanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new GridLayout(3, 1));

        // Growth rate r
        JSlider rSlider = new JSlider(10, 200, 100);
        rSlider.setMajorTickSpacing(50);
        rSlider.setMinorTickSpacing(10);
        rSlider.setPaintTicks(true);
        rSlider.setPaintLabels(true);
        rSlider.addChangeListener(e -> {
            graphPanel.setR(rSlider.getValue() / 100.0);
        });
        controlPanel.add(labeled("Growth rate r", rSlider));

        // Carrying capacity K
        JSlider kSlider = new JSlider(10, 300, 100);
        kSlider.setMajorTickSpacing(50);
        kSlider.setMinorTickSpacing(10);
        kSlider.setPaintTicks(true);
        kSlider.setPaintLabels(true);
        kSlider.addChangeListener(e -> {
            graphPanel.setK(kSlider.getValue() / 100.0);
        });
        controlPanel.add(labeled("Carrying capacity K", kSlider));

        // Initial population x0
        JSlider x0Slider = new JSlider(1, 100, 10);
        x0Slider.setMajorTickSpacing(20);
        x0Slider.setMinorTickSpacing(5);
        x0Slider.setPaintTicks(true);
        x0Slider.setPaintLabels(true);
        x0Slider.addChangeListener(e -> {
            graphPanel.setX0(x0Slider.getValue() / 100.0);
        });
        controlPanel.add(labeled("Initial population x₀", x0Slider));

        add(controlPanel, BorderLayout.SOUTH);
    }

    private JPanel labeled(String text, JSlider slider) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel(text), BorderLayout.WEST);
        panel.add(slider, BorderLayout.CENTER);
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            InteractiveLogisticGrowth app = new InteractiveLogisticGrowth();
            app.setVisible(true);
        });
    }

    static class LogisticPanel extends JPanel {
        double r = 1.0;
        double K = 1.0;
        double x0 = 0.1;
        double dt = 0.05;
        double tMax = 20;
        ArrayList<Double> time = new ArrayList<>();
        ArrayList<Double> population = new ArrayList<>();

        public LogisticPanel() {
            setPreferredSize(new Dimension(800, 500));
            simulate();
        }

        public void setR(double newR) {
            this.r = newR;
            simulate();
            repaint();
        }

        public void setK(double newK) {
            this.K = newK;
            simulate();
            repaint();
        }

        public void setX0(double newX0) {
            this.x0 = newX0;
            simulate();
            repaint();
        }

        private void simulate() {
            time.clear();
            population.clear();
            double t = 0;
            double x = x0;

            while (t <= tMax) {
                time.add(t);
                population.add(x);
                double dx = r * x * (1 - x / K);
                x = x + dx * dt;
                t = t + dt;
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int margin = 50;
            int width = getWidth();
            int height = getHeight();
            int graphWidth = width - 2 * margin;
            int graphHeight = height - 2 * margin;

            g.setColor(Color.WHITE);
            g.fillRect(0, 0, width, height);
            g.setColor(Color.LIGHT_GRAY);
            g.drawRect(margin, margin, graphWidth, graphHeight);

            g.setColor(Color.BLACK);
            g.drawString(String.format("r = %.2f   K = %.2f   x₀ = %.2f", r, K, x0), margin, 20);

            g.setColor(Color.BLUE);
            double maxY = K;
            int prevX = margin;
            int prevY = margin + graphHeight - (int) (population.get(0) / maxY * graphHeight);

            for (int i = 1; i < population.size(); i++) {
                int xPixel = margin + (int)(i * graphWidth / (double) population.size());
                int yPixel = margin + graphHeight - (int)(population.get(i) / maxY * graphHeight);
                g.drawLine(prevX, prevY, xPixel, yPixel);
                prevX = xPixel;
                prevY = yPixel;
            }

            // Draw K line
            g.setColor(Color.RED);
            int eqY = margin + graphHeight - (int)(K / maxY * graphHeight);
            g.drawLine(margin, eqY, margin + graphWidth, eqY);
            g.drawString("K", margin + 5, eqY - 5);
        }
    }
}

