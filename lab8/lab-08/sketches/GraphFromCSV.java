import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class GraphFromCSV extends JFrame {

    private List<DataPoint> dataPoints;

    public GraphFromCSV(String inputFileName) {
        setTitle("Rabbit Simulation Graph");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Read data from the specified CSV file.
        dataPoints = readCSV(inputFileName);
        add(new GraphPanel());
    }

    /**
     * Reads a CSV file with header "Generation,Population" and returns a list of data points.
     */
    private List<DataPoint> readCSV(String fileName) {
        List<DataPoint> points = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line = br.readLine(); // Skip header
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(",");
                if (tokens.length >= 2) {
                    int generation = Integer.parseInt(tokens[0].trim());
                    int population = Integer.parseInt(tokens[1].trim());
                    points.add(new DataPoint(generation, population));
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading CSV file: " + e.getMessage());
        }
        return points;
    }

    public static void main(String[] args) {
        // If an argument is provided, use it as the input file name; otherwise, default.
        String inputFileName = args.length > 0 ? args[0] : "simulation_output.csv";

        SwingUtilities.invokeLater(() -> {
            GraphFromCSV frame = new GraphFromCSV(inputFileName);
            frame.setVisible(true);
        });
    }

    /**
     * A simple data holder for a generation-population pair.
     */
    class DataPoint {
        int generation;
        int population;

        DataPoint(int generation, int population) {
            this.generation = generation;
            this.population = population;
        }
    }

    /**
     * GraphPanel is a custom JPanel that draws axes and plots the data points read from the CSV file.
     */
    class GraphPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;

            int marginLeft = 60;
            int marginRight = 20;
            int marginTop = 20;
            int marginBottom = 60;

            int width = getWidth();
            int height = getHeight();

            // Draw axes.
            g2.drawLine(marginLeft, height - marginBottom, width - marginRight, height - marginBottom); // x-axis
            g2.drawLine(marginLeft, height - marginBottom, marginLeft, marginTop); // y-axis

            if (dataPoints == null || dataPoints.isEmpty()) {
                g2.drawString("No data to display", width / 2 - 50, height / 2);
                return;
            }

            // Determine the range of the data.
            int minGen = dataPoints.get(0).generation;
            int maxGen = dataPoints.get(dataPoints.size() - 1).generation;
            int minPop = Integer.MAX_VALUE;
            int maxPop = Integer.MIN_VALUE;
            for (DataPoint dp : dataPoints) {
                if (dp.population < minPop) minPop = dp.population;
                if (dp.population > maxPop) maxPop = dp.population;
            }
            // Ensure the y-range covers 0.
            minPop = Math.min(0, minPop);

            int plotWidth = width - marginLeft - marginRight;
            int plotHeight = height - marginTop - marginBottom;

            // Plot the data points as a polyline.
            int prevX = -1, prevY = -1;
            for (DataPoint dp : dataPoints) {
                // Map generation to x coordinate.
                int x = marginLeft + (int) ((dp.generation - minGen) * (double) plotWidth / (maxGen - minGen));
                // Map population to y coordinate (inverted y-axis).
                int y = height - marginBottom - (int) ((dp.population - minPop) * (double) plotHeight / (maxPop - minPop));

                // Draw a small circle for the point.
                g2.fillOval(x - 2, y - 2, 4, 4);

                // Draw a line connecting the previous point.
                if (prevX != -1) {
                    g2.drawLine(prevX, prevY, x, y);
                }
                prevX = x;
                prevY = y;
            }

            // Label axes.
            g2.drawString("Generation", marginLeft + plotWidth / 2, height - 20);
            g2.drawString("Population", 10, marginTop + plotHeight / 2);
        }
    }
}

