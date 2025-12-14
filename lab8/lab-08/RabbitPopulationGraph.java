import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.event.*;

public class RabbitPopulationGraph extends JFrame {
    private double r = 3.5;        // Growth rate
    private double x0 = 0.5;       // Initial population
    private int iterations = 200;  // Number of iterations

    private JTextField rField;
    private JTextField x0Field;
    private PopulationPanel graphPanel;

    public RabbitPopulationGraph() {
        setTitle("Rabbit Population - Logistic Map");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top input panel with FlowLayout for controls.
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new FlowLayout());

        // Growth Rate input
        inputPanel.add(new JLabel("Growth Rate (r):"));
        rField = new JTextField(String.valueOf(r), 5);
        inputPanel.add(rField);

        // Initial Population input
        inputPanel.add(new JLabel("Initial Population (x0):"));
        x0Field = new JTextField(String.valueOf(x0), 5);
        inputPanel.add(x0Field);

        // Draw button to update parameters and refresh the graph.
        JButton drawButton = new JButton("Draw");
        drawButton.addActionListener(e -> {
            try {
                r = Double.parseDouble(rField.getText());
                x0 = Double.parseDouble(x0Field.getText());
                graphPanel.repaint();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Please enter valid numbers.");
            }
        });
        inputPanel.add(drawButton);

        // Iterations slider: from 50 to 1000 iterations.
        inputPanel.add(new JLabel("Iterations:"));
        JSlider iterationSlider = new JSlider(JSlider.HORIZONTAL, 50, 1000, iterations);
        iterationSlider.setMajorTickSpacing(150);
        iterationSlider.setMinorTickSpacing(50);
        iterationSlider.setPaintTicks(true);
        iterationSlider.setPaintLabels(true);
        iterationSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                // Only update when the slider is not being dragged.
                if (!iterationSlider.getValueIsAdjusting()) {
                    iterations = iterationSlider.getValue();
                    graphPanel.repaint();
                }
            }
        });
        inputPanel.add(iterationSlider);

        add(inputPanel, BorderLayout.NORTH);

        // Graph panel for plotting the logistic map.
        graphPanel = new PopulationPanel();
        add(graphPanel, BorderLayout.CENTER);
    }

    private class PopulationPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            drawGraph(g);
        }

        private void drawGraph(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            int width = getWidth();
            int height = getHeight();

            // Clear background.
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, width, height);

            // Draw title.
            g2.setColor(Color.BLACK);
            g2.drawString("Rabbit Population (x) over Iterations", 10, 20);

            // Draw axes.
            g2.drawLine(40, height - 40, width - 20, height - 40); // x-axis
            g2.drawLine(40, height - 40, 40, 20);                  // y-axis

            // Compute logistic map values.
            double[] population = new double[iterations];
            population[0] = x0;
            for (int i = 1; i < iterations; i++) {
                population[i] = r * population[i - 1] * (1 - population[i - 1]);
            }

            // Plot the graph by connecting successive points.
            g2.setColor(Color.BLUE);
            for (int i = 1; i < iterations; i++) {
                int x1 = map(i - 1, 0, iterations, 40, width - 20);
                int y1 = map(population[i - 1], 0, 1, height - 40, 20);
                int x2 = map(i, 0, iterations, 40, width - 20);
                int y2 = map(population[i], 0, 1, height - 40, 20);
                g2.drawLine(x1, y1, x2, y2);
            }
        }

        // Map a value from one range to another.
        private int map(double value, double srcMin, double srcMax, double dstMin, double dstMax) {
            return (int) (dstMin + (value - srcMin) * (dstMax - dstMin) / (srcMax - srcMin));
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            RabbitPopulationGraph frame = new RabbitPopulationGraph();
            frame.setVisible(true);
        });
    }
}

