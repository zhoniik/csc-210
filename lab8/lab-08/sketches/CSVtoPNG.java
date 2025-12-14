import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CSVtoPNG {

    // DataPoint class holds one (generation, population) pair.
    static class DataPoint {
        int generation;
        double population;
        DataPoint(int generation, double population) {
            this.generation = generation;
            this.population = population;
        }
    }
    
    /**
     * Reads the CSV file provided by fileName.
     * Assumes the CSV has a header and then rows of "Iteration,Population".
     */
    public static List<DataPoint> readCSV(String fileName) {
        List<DataPoint> dataPoints = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            String line = br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(",");
                if (tokens.length >= 2) {
                    int generation = Integer.parseInt(tokens[0].trim());
                    double population = Double.parseDouble(tokens[1].trim());
                    dataPoints.add(new DataPoint(generation, population));
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading CSV file: " + e.getMessage());
            System.exit(1);
        }
        return dataPoints;
    }
    
    /**
     * Draws a graph (axes and polyline connecting data points) onto the Graphics2D object.
     */
    public static void drawGraph(List<DataPoint> dataPoints, Graphics2D g, int width, int height) {
        // Define margins.
        int marginLeft = 60;
        int marginRight = 20;
        int marginTop = 20;
        int marginBottom = 60;
        
        // Clear background.
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        
        // Draw axes.
        g.setColor(Color.BLACK);
        g.drawLine(marginLeft, height - marginBottom, width - marginRight, height - marginBottom); // x-axis
        g.drawLine(marginLeft, height - marginBottom, marginLeft, marginTop); // y-axis
        
        if (dataPoints.isEmpty()) {
            g.drawString("No data to display", width / 2 - 50, height / 2);
            return;
        }
        
        // Determine data range.
        int minGen = dataPoints.get(0).generation;
        int maxGen = dataPoints.get(dataPoints.size() - 1).generation;
        double minPop = Double.MAX_VALUE;
        double maxPop = Double.MIN_VALUE;
        for (DataPoint dp : dataPoints) {
            if (dp.population < minPop) minPop = dp.population;
            if (dp.population > maxPop) maxPop = dp.population;
        }
        // Ensure the y-range covers zero.
        minPop = Math.min(0, minPop);
        
        int plotWidth = width - marginLeft - marginRight;
        int plotHeight = height - marginTop - marginBottom;
        
        // Plot data as a polyline.
        int prevX = -1, prevY = -1;
        g.setColor(Color.BLUE);
        for (DataPoint dp : dataPoints) {
            int x = marginLeft + (int) ((dp.generation - minGen) * (double) plotWidth / (maxGen - minGen));
            int y = height - marginBottom - (int) ((dp.population - minPop) * (double) plotHeight / (maxPop - minPop));
            
            // Draw a small circle for the data point.
            g.fillOval(x - 2, y - 2, 4, 4);
            if (prevX != -1) {
                g.drawLine(prevX, prevY, x, y);
            }
            prevX = x;
            prevY = y;
        }
        
        // Label axes.
        g.setColor(Color.BLACK);
        g.drawString("Generation", marginLeft + plotWidth / 2, height - 20);
        g.drawString("Population", 10, marginTop + plotHeight / 2);
    }
    
    public static void main(String[] args) {
        // Usage: java CSVtoPNG <input_csv> [output_png]
        if (args.length < 1) {
            System.err.println("Usage: java CSVtoPNG <input_csv> [output_png]");
            System.exit(1);
        }
        
        String inputCsv = args[0];
        String outputPng = (args.length >= 2) ? args[1] : "graph_output.png";
        
        // Read the data points from the CSV file.
        List<DataPoint> dataPoints = readCSV(inputCsv);
        
        // Define the image dimensions.
        int width = 800;
        int height = 600;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = image.createGraphics();
        // Enable anti-aliasing for smoother graphics.
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw the graph onto the image.
        drawGraph(dataPoints, g, width, height);
        g.dispose();
        
        // Write the image as a PNG file.
        try {
            ImageIO.write(image, "png", new File(outputPng));
            System.out.println("PNG graph written to " + outputPng);
        } catch (IOException e) {
            System.err.println("Error writing PNG file: " + e.getMessage());
            System.exit(1);
        }
    }
}

