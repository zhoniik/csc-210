import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class LogisticMapGenerator {

    public static void main(String[] args) {
        // Check if at least three arguments are provided.
        if (args.length < 3) {
            System.err.println("Usage: java LogisticMapGenerator <growthRate> <initialPopulation> <iterations> [outputFile]");
            System.exit(1);
        }
        
        try {
            // Parse command line arguments.
            double growthRate = Double.parseDouble(args[0]);
            double initialPopulation = Double.parseDouble(args[1]);
            int iterations = Integer.parseInt(args[2]);
            String outputFile = (args.length >= 4) ? args[3] : "logistic_map_data.csv";
            
            // Open file for writing.
            try (PrintWriter writer = new PrintWriter(new FileWriter(outputFile))) {
                // Write CSV header.
                writer.println("Iteration,Population");
                
                // Set initial value.
                double x = initialPopulation;
                // Write the initial condition (iteration 0).
                writer.println("0," + x);
                
                // Iterate and calculate logistic map values.
                for (int i = 1; i <= iterations; i++) {
                    x = growthRate * x * (1 - x);
                    writer.println(i + "," + x);
                }
            }
            System.out.println("Data written to " + outputFile);
            
        } catch (NumberFormatException e) {
            System.err.println("Error: growth rate, initial population, and iterations must be numeric values.");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Error writing to file: " + e.getMessage());
            System.exit(1);
        }
    }
}

