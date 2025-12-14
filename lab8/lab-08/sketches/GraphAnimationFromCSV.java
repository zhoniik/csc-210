import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GraphAnimationFromCSV extends JFrame {

    private List<DataPoint> dataPoints;

    public GraphAnimationFromCSV(String csvFile) {
        setTitle("Animated Graph from CSV");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Read CSV data
        dataPoints = readCSV(csvFile);

        // Create and add the animated graph panel.
        GraphPanel panel = new GraphPanel(dataPoints);
        add(panel);
    }

    /**
     * Reads the CSV file and returns a list of DataPoint objects.
     * Expects a header row and then rows in the format "Iteration,Population".
     */
    public static List<DataPoint> readCSV(String fileName) {
        List<DataPoint> points = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String header = br.readLine(); // skip header
            String line;
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(",");
                if (tokens.length >= 2) {
                    int iteration = Integer.parseInt(tokens[0].trim());
                    double population = Double.parseDouble(tokens[1].trim());
                    points.add(new DataPoint(iteration, population));
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
            System.exit(1);
        }
        return points;
    }

    public static void main(String[] args) {
        // Use command-line argument if provided; otherwise default to "simulation_output.csv"
        String inputFile = args.length > 0 ? args[0] : "simulation_output.csv";
        SwingUtilities.invokeLater(() -> {
            GraphAnimationFromCSV frame = new GraphAnimationFromCSV(inputFile);
            frame.setVisible(true);
        });
    }

    /**
     * A simple class to hold a data point.
     */
    static class DataPoint {
        int iteration;
        double population;

        DataPoint(int iteration, double population) {
            this.iteration = iteration;
            this.population = population;
        }
    }

    /**
     * GraphPanel is a custom JPanel that animates drawing the graph.
     */
    class GraphPanel extends JPanel {
        private List<DataPoint> dataPoints;
        private int currentIndex = 0;
        private Timer timer;

        public GraphPanel(List<DataPoint> dataPoints) {
            this.dataPoints = dataPoints;

            // Create a Timer to update the drawing. Adjust the delay as desired.
            timer = new Timer(100, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    currentIndex++;
                    if (currentIndex > dataPoints.size()) {
                        currentIndex = dataPoints.size();
                        timer.stop();
                    }
                    repaint();
                }
            });
            timer.start();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;

            // Define margins for the plot area.
            int marginLeft = 60;
            int marginRight = 20;
            int marginTop = 20;
            int marginBottom = 60;
            int width = getWidth();
            int height = getHeight();

            // Clear background.
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, width, height);

            // Draw axes.
            g2.setColor(Color.BLACK);
            g2.drawLine(marginLeft, height - marginBottom, width - marginRight, height - marginBottom); // x-axis
            g2.drawLine(marginLeft, height - marginBottom, marginLeft, marginTop); // y-axis

            if (dataPoints.isEmpty()) {
                g2.drawString("No data to display", width / 2 - 50, height / 2);
                return;
            }

            // Determine data range.
            int minIteration = dataPoints.get(0).iteration;
            int maxIteration = dataPoints.get(dataPoints.size() - 1).iteration;
            double minPop = Double.MAX_VALUE;
            double maxPop = Double.MIN_VALUE;
            for (DataPoint dp : dataPoints) {
                if (dp.population < minPop) {
                    minPop = dp.population;
                }
                if (dp.population > maxPop) {
                    maxPop = dp.population;
                }
            }
            minPop = Math.min(0, minPop);

            int plotWidth = width - marginLeft - marginRight;
            int plotHeight = height - marginTop - marginBottom;

            // Draw the data as a polyline up to currentIndex.
            int prevX = -1, prevY = -1;
            g2.setColor(Color.BLUE);
            for (int i = 0; i < currentIndex && i < dataPoints.size(); i++) {
                DataPoint dp = dataPoints.get(i);
                int x = marginLeft + (int)((dp.iteration - minIteration) * (double)plotWidth / (maxIteration - minIteration));
                int y = height - marginBottom - (int)((dp.population - minPop) * (double)plotHeight / (maxPop - minPop));

                // Draw the data point.
                g2.fillOval(x - 2, y - 2, 4, 4);
                if (prevX != -1) {
                    g2.drawLine(prevX, prevY, x, y);
                }
                prevX = x;
                prevY = y;
            }

            // Draw axis labels.
            g2.setColor(Color.BLACK);
            g2.drawString("Iteration", marginLeft + plotWidth / 2, height - 20);
            g2.drawString("Population", 10, marginTop + plotHeight / 2);
        }
    }
}

