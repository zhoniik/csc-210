import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class CarrotRabbitSimulation extends JFrame {

    // Dimensions for simulation and graph panels.
    final int SIM_WIDTH = 800, SIM_HEIGHT = 600;
    final int GRAPH_WIDTH = 300, GRAPH_HEIGHT = 600;

    // Population model parameters.
    final int rabbitCarryingCapacity = 50;
    final int rabbitFecundity = 3;

    // The initial populations (set by user in SetupPanel).
    int initialRabbits = 5;
    // For carrots, we now use a maximum count (which is also the initial number)
    int carrotMax = 20;
    // Refill interval in ticks (default set to rabbit lifespan: 500 ticks).
    int carrotRefillInterval = 500;
    
    // CardLayout to switch between setup and simulation.
    CardLayout cardLayout = new CardLayout();
    JPanel cards = new JPanel(cardLayout);
    
    // Panels.
    SetupPanel setupPanel = new SetupPanel();
    SimulationContainer simContainer;  // Will be created after user clicks Play.

    public CarrotRabbitSimulation() {
        super("Ecosystem Simulation");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        // Add the setup screen.
        cards.add(setupPanel, "Setup");
        getContentPane().add(cards);
        
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    // ---------------- SetupPanel ------------------
    // A simple panel where the user chooses initial numbers.
    class SetupPanel extends JPanel {
        JTextField rabbitField;
        JTextField carrotMaxField;
        JTextField carrotIntervalField;
        JButton playButton;
        
        public SetupPanel() {
            setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10,10,10,10);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            
            JLabel titleLabel = new JLabel("Ecosystem Simulation Setup", JLabel.CENTER);
            titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
            gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
            add(titleLabel, gbc);
            
            gbc.gridwidth = 1;
            gbc.gridx = 0; gbc.gridy = 1;
            add(new JLabel("Initial Rabbits:"), gbc);
            rabbitField = new JTextField("5", 10);
            gbc.gridx = 1;
            add(rabbitField, gbc);
            
            gbc.gridx = 0; gbc.gridy = 3;
            add(new JLabel("Carrot Maximum:"), gbc);
            carrotMaxField = new JTextField("20", 10);
            gbc.gridx = 1;
            add(carrotMaxField, gbc);
            
            gbc.gridx = 0; gbc.gridy = 4;
            add(new JLabel("Carrot Refill Interval (ticks):"), gbc);
            carrotIntervalField = new JTextField("500", 10);  // default equal to rabbit lifespan
            gbc.gridx = 1;
            add(carrotIntervalField, gbc);
            
            playButton = new JButton("Play");
            gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
            add(playButton, gbc);
            
            playButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    try {
                        initialRabbits = Integer.parseInt(rabbitField.getText());
                        carrotMax = Integer.parseInt(carrotMaxField.getText());
                        carrotRefillInterval = Integer.parseInt(carrotIntervalField.getText());
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(SetupPanel.this,
                            "Please enter valid integer values.",
                            "Invalid Input", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    // Create the simulation container with user-defined initial values.
                    simContainer = new SimulationContainer(initialRabbits, carrotMax, carrotRefillInterval);
                    cards.add(simContainer, "Simulation");
                    cardLayout.show(cards, "Simulation");
                    simContainer.startSimulation();
                    CarrotRabbitSimulation.this.pack();
                }
            });
        }
    }
    
    // ---------------- SimulationContainer ------------------
    // Contains the simulation area and two graph panels (for rabbits).
    class SimulationContainer extends JPanel {
        SimulationArea simArea;
        GraphPanel rabbitGraph;
        javax.swing.Timer mainTimer;
        int tickCount = 0;
        
        public SimulationContainer(int initRabbits, int carrotMax, int carrotInterval) {
            setLayout(new BorderLayout());
            // Create the simulation area.
            simArea = new SimulationArea(initRabbits, carrotMax, carrotInterval);
            simArea.setPreferredSize(new Dimension(SIM_WIDTH, SIM_HEIGHT));
            add(simArea, BorderLayout.CENTER);
            
            // Create a container for the graphs using a vertical layout.
            JPanel graphContainer = new JPanel(new GridLayout(2, 1));
            rabbitGraph = new GraphPanel("Rabbits");
            rabbitGraph.setPreferredSize(new Dimension(GRAPH_WIDTH, GRAPH_HEIGHT / 2));
            graphContainer.add(rabbitGraph);
            add(graphContainer, BorderLayout.EAST);
        }
        
        public void startSimulation() {
            // Main simulation timer: update simulation and graphs every 50 ms.
            mainTimer = new javax.swing.Timer(50, new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    tickCount++;
                    simArea.updateSimulation(tickCount);
                    rabbitGraph.updateData(tickCount, simArea.getRabbitCount());
                    simArea.repaint();
                    rabbitGraph.repaint();
                }
            });
            mainTimer.start();
        }
    }
    
    // ---------------- SimulationArea ------------------
    // The area where carrots and rabbits are drawn and updated.
    class SimulationArea extends JPanel {
        int width = SIM_WIDTH, height = SIM_HEIGHT;
        List<Carrot> carrots;
        List<Rabbit> rabbits;
        Random rand = new Random();
        
        // Carrot refill parameters.
        int carrotMax;
        int carrotRefillInterval;
        
        public SimulationArea(int initRabbits, int carrotMax, int carrotRefillInterval) {
            setBackground(new Color(30, 100, 30));  // dark green background
            carrots = new ArrayList<>();
            rabbits = new ArrayList<>();
            this.carrotMax = carrotMax;
            this.carrotRefillInterval = carrotRefillInterval;
            
            // Add initial rabbits.
            for (int i = 0; i < initRabbits; i++) {
                rabbits.add(new Rabbit(rand.nextInt(width), rand.nextInt(height)));
            }
        
            // Initially fill carrots to the maximum.
            refillCarrots();
        }
        
        // Called from the main simulation timer. tick is the current tick count.
        public void updateSimulation(int tick) {
            // Instead of randomly adding carrots, refill them to max every interval.
            if (tick % carrotRefillInterval == 0) {
                refillCarrots();
            }
            
            List<Rabbit> newRabbits = new ArrayList<>();
            List<Rabbit> rabbitsToRemove = new ArrayList<>();
            
            // Update rabbits: move, age, and check for carrot consumption.
            Iterator<Rabbit> rit = rabbits.iterator();
            while (rit.hasNext()) {
                Rabbit r = rit.next();
                r.move();
                r.age++;
                if (r.age > r.lifespan) {
                    rit.remove();
                    continue;
                }
                // Check if the rabbit eats a carrot.
                Iterator<Carrot> cit = carrots.iterator();
                while (cit.hasNext()) {
                    Carrot c = cit.next();
                    if (r.intersects(c)) {
                        cit.remove();
                        double logisticFactor = 1 - ((double) rabbits.size() / rabbitCarryingCapacity);
                        int offspring = (int) (rabbitFecundity * logisticFactor);
                        for (int j = 0; j < offspring; j++) {
                            newRabbits.add(new Rabbit(r.x, r.y));
                        }
                        break;
                    }
                }
            }
            rabbits.addAll(newRabbits);
            rabbits.removeAll(rabbitsToRemove);
        }
        
        // Refill the carrot list to the maximum, placing them at random positions.
        private void refillCarrots() {
            carrots.clear();
            for (int i = 0; i < carrotMax; i++) {
                carrots.add(new Carrot(rand.nextInt(width - 10), rand.nextInt(height - 10)));
            }
        }
        
        public int getRabbitCount() { return rabbits.size(); }
        
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            // Draw carrots as small orange squares.
            g.setColor(Color.ORANGE);
            for (Carrot c : carrots) {
                g.fillRect(c.x, c.y, c.size, c.size);
            }
            // Draw rabbits as white circles.
            g.setColor(Color.WHITE);
            for (Rabbit r : rabbits) {
                g.fillOval((int)r.x, (int)r.y, r.size, r.size);
            }
        }
        
        // ---------------- Inner Classes for Simulation Objects ----------------
        class Carrot {
            int x, y;
            int size = 10;
            public Carrot(int x, int y) { this.x = x; this.y = y; }
        }
        
        // ----- Rabbit with directional movement -----
        class Rabbit {
            // Position stored as doubles for smoother directional movement.
            double x, y;
            int size = 15;
            int age = 0;
            int lifespan = 500;  // ticks
            double dx, dy;
            final double speed = 2.0;
            
            public Rabbit(double x, double y) {
                this.x = x;
                this.y = y;
                // Initialize with a random direction.
                double angle = rand.nextDouble() * 2 * Math.PI;
                dx = speed * Math.cos(angle);
                dy = speed * Math.sin(angle);
            }
            
            public void move() {
                // Occasionally change direction a bit.
                if (rand.nextDouble() < 0.05) {
                    double anglePerturb = (rand.nextDouble() - 0.5) * (Math.PI / 4); // ±45° perturbation
                    double currentSpeed = Math.sqrt(dx * dx + dy * dy);
                    double currentAngle = Math.atan2(dy, dx);
                    currentAngle += anglePerturb;
                    dx = currentSpeed * Math.cos(currentAngle);
                    dy = currentSpeed * Math.sin(currentAngle);
                }
                x += dx;
                y += dy;
                
                // Bounce off boundaries.
                if (x < 0) { x = 0; dx = -dx; }
                if (x > width - size) { x = width - size; dx = -dx; }
                if (y < 0) { y = 0; dy = -dy; }
                if (y > height - size) { y = height - size; dy = -dy; }
            }
            
            public boolean intersects(Carrot c) {
                Rectangle rRect = new Rectangle((int)x, (int)y, size, size);
                Rectangle cRect = new Rectangle(c.x, c.y, c.size, c.size);
                return rRect.intersects(cRect);
            }
        }
    }
    
    // ---------------- GraphPanel ------------------
    // A panel that plots population vs. time for one species.
    class GraphPanel extends JPanel {
        String species;
        List<Integer> timeData = new ArrayList<>();
        List<Integer> popData = new ArrayList<>();
        
        public GraphPanel(String species) {
            this.species = species;
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createTitledBorder(species + " Population"));
        }
        
        public void updateData(int t, int pop) {
            timeData.add(t);
            popData.add(pop);
        }
        
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int w = getWidth();
            int h = getHeight();
            // Draw axes.
            g.setColor(Color.BLACK);
            g.drawLine(40, h - 30, w - 10, h - 30); // X-axis
            g.drawLine(40, h - 30, 40, 10);          // Y-axis
            
            int maxTime = timeData.isEmpty() ? 1 : timeData.get(timeData.size()-1);
            double xScale = (double)(w - 50) / maxTime;
            // Assume maximum population is at most the carrying capacity.
            int maxPop = 2000; //rabbitCarryingCapacity;
            double yScale = (double)(h - 40) / maxPop;
            
            // Draw actual data as a line.
            g.setColor(species.equals("Rabbits") ? Color.RED : Color.BLUE);
            for (int i = 1; i < timeData.size(); i++) {
                int x1 = 40 + (int)(timeData.get(i-1) * xScale);
                int y1 = h - 30 - (int)(popData.get(i-1) * yScale);
                int x2 = 40 + (int)(timeData.get(i) * xScale);
                int y2 = h - 30 - (int)(popData.get(i) * yScale);
                g.drawLine(x1, y1, x2, y2);
            }
        }
    }
    
    // ---------------- main() ------------------
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CarrotRabbitSimulation());
    }
}

