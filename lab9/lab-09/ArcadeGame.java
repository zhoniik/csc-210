import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

public class ArcadeGame extends JPanel implements ActionListener, KeyListener {
    // Game area dimensions
    private final int WIDTH = 800, HEIGHT = 600;
    
    // Game objects
    private Wolf wolf;
    private java.util.List<Rabbit> rabbits;
    private java.util.List<Carrot> carrots;
    
    // Use Swing's Timer explicitly
    private javax.swing.Timer timer;
    
    // Key press flags for wolf movement
    private boolean up, down, left, right;
    
    public ArcadeGame() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(new Color(0, 150, 0)); // A green background
        
        // Create the wolf in the center of the screen.
        wolf = new Wolf(WIDTH / 2, HEIGHT / 2);
        
        // Initialize rabbits and carrots lists.
        rabbits = new ArrayList<>();
        carrots = new ArrayList<>();
        
        // Start with a few rabbits.
        for (int i = 0; i < 5; i++) {
            rabbits.add(new Rabbit(new Random().nextInt(WIDTH), new Random().nextInt(HEIGHT)));
        }
        
        // Set up a Swing timer for the game loop (updates every 30ms).
        timer = new javax.swing.Timer(30, this);
        timer.start();
        
        // Set up key listening.
        addKeyListener(this);
        setFocusable(true);
        requestFocusInWindow();
        
        // Start the carrot spawner thread that fetches random data from random.org.
        new CarrotSpawner().start();
    }
    
    // This method is called by the Timer every 30ms.
    public void actionPerformed(ActionEvent e) {
        updateWolf();
        updateRabbits();
        checkCollisions();
        repaint();
    }
    
    // Update wolf position based on key flags.
    private void updateWolf() {
        int speed = 5;
        if (up)    wolf.y -= speed;
        if (down)  wolf.y += speed;
        if (left)  wolf.x -= speed;
        if (right) wolf.x += speed;
        
        // Keep the wolf within bounds.
        wolf.x = Math.max(0, Math.min(WIDTH - wolf.size, wolf.x));
        wolf.y = Math.max(0, Math.min(HEIGHT - wolf.size, wolf.y));
    }
    
    // Update rabbits with simple random movement.
    private void updateRabbits() {
        for (Rabbit r : rabbits) {
            r.move(WIDTH, HEIGHT);
        }
    }
    
    // Check for collisions:
    // - Wolf eats a rabbit (rabbit is removed).
    // - A rabbit eating a carrot causes reproduction (new rabbit spawns).
    private void checkCollisions() {
        // Wolf eats rabbits.
        Iterator<Rabbit> rit = rabbits.iterator();
        while (rit.hasNext()) {
            Rabbit r = rit.next();
            if (collides(wolf.x, wolf.y, wolf.size, r.x, r.y, r.size)) {
                rit.remove();
                // (You could add a score counter here.)
            }
        }
        
        // Rabbits eat carrots.
        Iterator<Carrot> cit = carrots.iterator();
        while (cit.hasNext()) {
            Carrot c = cit.next();
            for (Rabbit r : rabbits) {
                if (collides(r.x, r.y, r.size, c.x, c.y, c.size)) {
                    // When a rabbit eats a carrot, reproduce by adding a new rabbit near it.
                    rabbits.add(new Rabbit(r.x + 10, r.y + 10));
                    cit.remove();
                    break;
                }
            }
        }
    }
    
    // Simple rectangle collision detection.
    private boolean collides(int x1, int y1, int size1, int x2, int y2, int size2) {
        Rectangle rect1 = new Rectangle(x1, y1, size1, size1);
        Rectangle rect2 = new Rectangle(x2, y2, size2, size2);
        return rect1.intersects(rect2);
    }
    
    // Draw the game objects.
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Draw wolf (gray circle).
        g.setColor(Color.DARK_GRAY);
        g.fillOval(wolf.x, wolf.y, wolf.size, wolf.size);
        
        // Draw rabbits (white circles).
        g.setColor(Color.WHITE);
        for (Rabbit r : rabbits) {
            g.fillOval(r.x, r.y, r.size, r.size);
        }
        
        // Draw carrots (orange squares).
        g.setColor(Color.ORANGE);
        for (Carrot c : carrots) {
            g.fillRect(c.x, c.y, c.size, c.size);
        }
    }
    
    // KeyListener methods.
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:    up = true; break;
            case KeyEvent.VK_DOWN:  down = true; break;
            case KeyEvent.VK_LEFT:  left = true; break;
            case KeyEvent.VK_RIGHT: right = true; break;
        }
    }
    
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:    up = false; break;
            case KeyEvent.VK_DOWN:  down = false; break;
            case KeyEvent.VK_LEFT:  left = false; break;
            case KeyEvent.VK_RIGHT: right = false; break;
        }
    }
    
    public void keyTyped(KeyEvent e) {}
    
    // --- Game object classes ---
    
    class Wolf {
        int x, y;
        int size = 30;
        public Wolf(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
    
    class Rabbit {
        int x, y;
        int size = 20;
        int dx, dy;
        
        public Rabbit(int x, int y) {
            this.x = x;
            this.y = y;
            // Random initial movement.
            Random rand = new Random();
            dx = rand.nextInt(5) - 2;  // -2 to +2
            dy = rand.nextInt(5) - 2;
        }
        
        public void move(int width, int height) {
            x += dx;
            y += dy;
            // Bounce off edges.
            if (x < 0 || x > width - size)  dx = -dx;
            if (y < 0 || y > height - size) dy = -dy;
        }
    }
    
    class Carrot {
        int x, y;
        int size = 10;
        public Carrot(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }
    
    // --- Carrot spawner thread ---
    // This thread continuously pulls random data from random.org to seed carrot spawn events.
    // It uses 4 bytes (8 hex digits) from random.org: the first 4 hex digits determine the x-coordinate,
    // and the next 4 determine the y-coordinate.
    class CarrotSpawner extends Thread {
        @Override
        public void run() {
            while (true) {
                try {
                    URL url = new URL("https://www.random.org/cgi-bin/randbyte?nbytes=4&format=h");
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String hex = in.readLine();
                    in.close();
                    connection.disconnect();
                    
                    if (hex != null) {
                        // Remove all whitespace characters.
                        hex = hex.replaceAll("\\s+", "");
                        if (hex.length() >= 8) {
                            String xHex = hex.substring(0, 4);
                            String yHex = hex.substring(4, 8);
                            int xSeed = Integer.parseInt(xHex, 16);
                            int ySeed = Integer.parseInt(yHex, 16);
                            int spawnX = xSeed % (WIDTH - 10);
                            int spawnY = ySeed % (HEIGHT - 10);
                            
                            // Use SwingUtilities.invokeLater to safely update game state from this thread.
                            SwingUtilities.invokeLater(() -> {
                                carrots.add(new Carrot(spawnX, spawnY));
                            });
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                
                // Wait 2 seconds before spawning the next carrot.
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
    
    // --- Main method to launch the game ---
    public static void main(String[] args) {
        JFrame frame = new JFrame("Wolf vs. Rabbits");
        ArcadeGame game = new ArcadeGame();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}

