import javax.swing.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;

public class RabbitDiscreteSimulationGUI extends JFrame {

    // Simulation state
    private Ecosystem ecosystem;
    private int generation = 0;
    private java.util.List<Integer> populationHistory = new ArrayList<>();

    // GUI Components
    private GraphPanel graphPanel;
    private JTextField initialPopulationField;
    private JTextField carrotSupplyField;
    private JButton playButton;
    private JButton stepButton;
    private JButton resetButton;
    private JButton exportDataButton;
    private JButton exportGraphButton;
    private JSlider speedSlider;
    
    // Timer for running simulation steps in play mode - use javax.swing.Timer explicitly.
    private javax.swing.Timer simulationTimer;
    private boolean isRunning = false;

    public RabbitDiscreteSimulationGUI() {
        super("Rabbit Simulation GUI");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create graph panel
        graphPanel = new GraphPanel();
        graphPanel.setPreferredSize(new Dimension(800, 600));
        add(graphPanel, BorderLayout.CENTER);

        // Create control panel
        JPanel controlPanel = new JPanel(new FlowLayout());

        controlPanel.add(new JLabel("Initial Population:"));
        initialPopulationField = new JTextField("50", 5);
        controlPanel.add(initialPopulationField);

        controlPanel.add(new JLabel("Carrot Supply/Gen:"));
        carrotSupplyField = new JTextField("100", 5);
        controlPanel.add(carrotSupplyField);

        playButton = new JButton("Play");
        controlPanel.add(playButton);

        stepButton = new JButton("Step");
        controlPanel.add(stepButton);

        resetButton = new JButton("Reset");
        controlPanel.add(resetButton);

        controlPanel.add(new JLabel("Speed:"));
        speedSlider = new JSlider(JSlider.HORIZONTAL, 100, 2000, 1000);
        speedSlider.setMajorTickSpacing(400);
        speedSlider.setMinorTickSpacing(100);
        speedSlider.setPaintTicks(true);
        speedSlider.setPaintLabels(true);
        controlPanel.add(speedSlider);

        exportDataButton = new JButton("Export Data");
        controlPanel.add(exportDataButton);

        exportGraphButton = new JButton("Export Graph");
        controlPanel.add(exportGraphButton);

        add(controlPanel, BorderLayout.SOUTH);

        // Add action listeners for buttons and slider
        playButton.addActionListener(e -> togglePlay());
        stepButton.addActionListener(e -> stepSimulation());
        resetButton.addActionListener(e -> resetSimulation());
        exportDataButton.addActionListener(e -> exportData());
        exportGraphButton.addActionListener(e -> exportGraph());
        speedSlider.addChangeListener(e -> simulationTimer.setDelay(speedSlider.getValue()));

        // Create a timer to run simulation steps at the speed set by the slider
        simulationTimer = new javax.swing.Timer(speedSlider.getValue(), e -> stepSimulation());

        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        // Initialize simulation with default values
        resetSimulation();
    }

    // Toggles play/pause mode
    private void togglePlay() {
        if (isRunning) {
            simulationTimer.stop();
            playButton.setText("Play");
            isRunning = false;
        } else {
            simulationTimer.start();
            playButton.setText("Pause");
            isRunning = true;
        }
    }

    // Executes one simulation step
    private void stepSimulation() {
        if (ecosystem != null) {
            ecosystem.simulateGeneration();
            generation++;
            populationHistory.add(ecosystem.getPopulation());
            graphPanel.repaint();
        }
    }

    // Resets the simulation using the current initial conditions
    private void resetSimulation() {
        simulationTimer.stop();
        isRunning = false;
        playButton.setText("Play");
        generation = 0;
        populationHistory.clear();

        int initialPopulation = parseIntField(initialPopulationField, 50);
        int carrotSupply = parseIntField(carrotSupplyField, 100);

        ecosystem = new Ecosystem(initialPopulation, carrotSupply);
        populationHistory.add(ecosystem.getPopulation());
        graphPanel.repaint();
    }
    
    // Helper to safely parse integer from text field
    private int parseIntField(JTextField field, int defaultValue) {
        try {
            return Integer.parseInt(field.getText());
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }

    // Exports the simulation data (population history) to a CSV file
    private void exportData() {
        JFileChooser fileChooser = new JFileChooser();
        int retVal = fileChooser.showSaveDialog(this);
        if (retVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (PrintWriter writer = new PrintWriter(new FileWriter(file))) {
                writer.println("Generation,Population");
                for (int i = 0; i < populationHistory.size(); i++) {
                    writer.println(i + "," + populationHistory.get(i));
                }
                JOptionPane.showMessageDialog(this, "Data exported successfully.");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error exporting data: " + e.getMessage());
            }
        }
    }

    // Exports the graph panel as a PNG image
    private void exportGraph() {
        BufferedImage image = new BufferedImage(graphPanel.getWidth(), graphPanel.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        graphPanel.paint(g2);
        g2.dispose();
        JFileChooser fileChooser = new JFileChooser();
        int retVal = fileChooser.showSaveDialog(this);
        if (retVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                ImageIO.write(image, "png", file);
                JOptionPane.showMessageDialog(this, "Graph exported successfully.");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error exporting graph: " + e.getMessage());
            }
        }
    }

    // Panel that draws the graph of generation versus population
    private class GraphPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            int width = getWidth();
            int height = getHeight();

            // Draw axes
            g2.drawLine(50, height - 50, width - 20, height - 50); // x-axis
            g2.drawLine(50, 20, 50, height - 50); // y-axis
            g2.drawString("Generation", width / 2, height - 20);
            g2.drawString("Population", 10, height / 2);

            if (populationHistory.size() < 2) return;

            int maxPop = Collections.max(populationHistory);
            if (maxPop == 0) maxPop = 1;

            int numPoints = populationHistory.size();
            int graphWidth = width - 70;
            int graphHeight = height - 70;
            double xScale = (double) graphWidth / (numPoints - 1);
            double yScale = (double) graphHeight / maxPop;

            // Draw the line graph
            for (int i = 0; i < numPoints - 1; i++) {
                int x1 = 50 + (int)(i * xScale);
                int y1 = height - 50 - (int)(populationHistory.get(i) * yScale);
                int x2 = 50 + (int)((i + 1) * xScale);
                int y2 = height - 50 - (int)(populationHistory.get(i + 1) * yScale);
                g2.drawLine(x1, y1, x2, y2);
            }
        }
    }

    // ----- Simulation Model Classes -----
    // Declare these inner classes as static so that they can have static members.

    // Rabbit class represents an individual agent
    private static class Rabbit {
        private static int nextId = 0;
        private int id;
        private double energy;

        public Rabbit() {
            this.id = nextId++;
            this.energy = 1.0;
        }

        public double getEnergy() {
            return energy;
        }

        public void forage(CarrotSupply supply) {
            if (supply.takeCarrot()) {
                energy += 1.0;
            }
        }

        public void metabolize() {
            energy -= 0.5;
        }

        public boolean isAlive() {
            return energy > 0;
        }

        public boolean isWillingToMate() {
            return energy >= 2.0;
        }

        public Rabbit mateWith(Rabbit partner) {
            if (this.isWillingToMate() && partner.isWillingToMate()) {
                this.energy -= 1.0;
                partner.energy -= 1.0;
                return new Rabbit();
            }
            return null;
        }
    }

    // CarrotSupply class represents the available food per generation
    private static class CarrotSupply {
        private int availableCarrots;

        public CarrotSupply(int count) {
            availableCarrots = count;
        }

        public boolean takeCarrot() {
            if (availableCarrots > 0) {
                availableCarrots--;
                return true;
            }
            return false;
        }
    }

    // Ecosystem class manages the rabbit population and simulates one generation
    private static class Ecosystem {
        private ArrayList<Rabbit> rabbits;
        private int carrotSupplyPerGen;

        public Ecosystem(int initialPopulation, int carrotSupplyPerGen) {
            this.carrotSupplyPerGen = carrotSupplyPerGen;
            rabbits = new ArrayList<>();
            for (int i = 0; i < initialPopulation; i++) {
                rabbits.add(new Rabbit());
            }
        }

        public int getPopulation() {
            return rabbits.size();
        }

        public void simulateGeneration() {
            // Reset carrot supply
            CarrotSupply supply = new CarrotSupply(carrotSupplyPerGen);
            Collections.shuffle(rabbits);
            for (Rabbit r : rabbits) {
                r.forage(supply);
            }
            for (Rabbit r : rabbits) {
                r.metabolize();
            }
            // Remove dead rabbits
            rabbits.removeIf(r -> !r.isAlive());
            // Mating phase
            ArrayList<Rabbit> offspring = new ArrayList<>();
            Collections.shuffle(rabbits);
            for (int i = 0; i < rabbits.size() - 1; i += 2) {
                Rabbit r1 = rabbits.get(i);
                Rabbit r2 = rabbits.get(i + 1);
                if (r1.isWillingToMate() && r2.isWillingToMate()) {
                    Rabbit child = r1.mateWith(r2);
                    if (child != null) {
                        offspring.add(child);
                    }
                }
            }
            rabbits.addAll(offspring);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RabbitDiscreteSimulationGUI());
    }
}

