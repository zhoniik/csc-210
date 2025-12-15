import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.AffineTransform;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

/*
 * AquariumSim - single-file Swing simulation
 * Features:
 * - Left-click near top rim drops food; clicking lower shakes the tank (scares fish).
 * - Fish follow the cursor when nearby.
 * - Food falls slowly (drag + gentle gravity).
 * - Seaweed cleans poop and consumes settled fish corpses (decomposition).
 * - Big fish may eat small fish when hungry.
 * - Multiple species per fish type; per-meal growth toward a max size.
 * - Appetite: fish refuse food when not hungry.
 * - Top air band is out-of-water; fish cannot swim into it.
 * - Idle swimming is smooth/gliding with soft depth keeping (no random vertical racing).
 *
 * Keys: A (add mid fish), B (add bottom), G (add algae eater), F (toggle filter), R (reduce algae), P (pause).
 */
public class AquariumSim extends JFrame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AquariumSim sim = new AquariumSim();
            sim.setVisible(true);
        });
    }

    public AquariumSim() {
        super("Aquarium Simulation - Decomposition, Appetite, Smooth Gliding");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 720);
        setLocationByPlatform(true);

        AquariumPanel panel = new AquariumPanel(960, 620);
        setLayout(new BorderLayout());
        add(panel, BorderLayout.CENTER);
        add(panel.buildHUD(), BorderLayout.SOUTH);
    }
}

/* ===================== Panel / World ===================== */

class AquariumPanel extends JPanel implements ActionListener, MouseListener, MouseMotionListener, KeyListener {
    // World
    final int worldW, worldH;
    final Rectangle tankBounds;
    final Random rng = new Random();

    // Entities
    final List<Fish> fish = new CopyOnWriteArrayList<>();
    final List<Pellet> pellets = new CopyOnWriteArrayList<>();
    final List<Poop> poops = new CopyOnWriteArrayList<>();
    final List<Seaweed> seaweeds = new CopyOnWriteArrayList<>();
    final List<Corpse> corpses = new CopyOnWriteArrayList<>();

    // Environment
    boolean filterOn = true;
    double dirt = 0.0;                 // general water dirt (0..100)
    double algaeLevel = 0.0;           // bloom severity (0..100)
    double timeSeconds = 0.0;
    boolean paused = false;

    // Input
    Point mouse = new Point(0, 0);

    // Timing
    final Timer timer;
    long lastTickNanos = System.nanoTime();

    // Visuals
    final Color waterColorBase = new Color(40, 130, 200);
    final Color dirtTint = new Color(80, 50, 20);
    final Color algaeTint = new Color(30, 120, 30);

    // HUD
    private final JLabel statusLabel = new JLabel();

    // Interaction thresholds
    final int FEED_TOP_MARGIN = 50;           // px below tank top where clicks count as "feed" (air band)
    final double CURSOR_ATTRACT_RADIUS = 120; // fish will follow if within this distance

    AquariumPanel(int w, int h) {
        this.worldW = w;
        this.worldH = h;
        this.tankBounds = new Rectangle(20, 20, w - 40, h - 40);

        setPreferredSize(new Dimension(w, h));
        setBackground(new Color(20, 30, 40));
        setFocusable(true);
        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(this);

        // Seaweed garden along the floor
        int floorY = tankFloorY();
        int clumps = 7;
        for (int i = 0; i < clumps; i++) {
            double nx = tankBounds.x + (i + 0.5) * (tankBounds.width / (double) clumps);
            seaweeds.add(new Seaweed(nx, floorY, 40 + rng.nextInt(35)));
        }

        // Seed fish
        fish.add(makeMidWaterFish(18));
        fish.add(makeBottomFeeder(16));
        fish.add(makeAlgaeEater(16));

        timer = new Timer(16, this); // ~60 FPS
        timer.start();
        updateStatus();
    }

    JPanel buildHUD() {
        JPanel hud = new JPanel(new BorderLayout());
        hud.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        statusLabel.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        hud.add(statusLabel, BorderLayout.CENTER);

        JButton addA = new JButton("Add Mid Fish (A)");
        JButton addB = new JButton("Add Bottom (B)");
        JButton addG = new JButton("Add Algae (G)");
        JButton togF = new JButton("Toggle Filter (F)");
        JButton rstAlg = new JButton("Reduce Algae (R)");
        JButton pause = new JButton("Pause (P)");

        addA.addActionListener(e -> addFishKey('A'));
        addB.addActionListener(e -> addFishKey('B'));
        addG.addActionListener(e -> addFishKey('G'));
        togF.addActionListener(e -> addFishKey('F'));
        rstAlg.addActionListener(e -> addFishKey('R'));
        pause.addActionListener(e -> addFishKey('P'));

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 2));
        buttons.add(addA);
        buttons.add(addB);
        buttons.add(addG);
        buttons.add(togF);
        buttons.add(rstAlg);
        buttons.add(pause);
        hud.add(buttons, BorderLayout.EAST);
        return hud;
    }

    private void addFishKey(char c) {
        switch (Character.toUpperCase(c)) {
            case 'A': fish.add(makeMidWaterFish(14 + rng.nextInt(8))); break;
            case 'B': fish.add(makeBottomFeeder(14 + rng.nextInt(6))); break;
            case 'G': fish.add(makeAlgaeEater(14 + rng.nextInt(6))); break;
            case 'F': filterOn = !filterOn; break;
            case 'R': algaeLevel = Math.max(0, algaeLevel - 25); break;
            case 'P': paused = !paused; break;
            default: break;
        }
        updateStatus();
        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        long now = System.nanoTime();
        double dt = (now - lastTickNanos) / 1_000_000_000.0;
        lastTickNanos = now;
        if (dt > 0.1) dt = 0.1; // clamp for pauses

        if (!paused) step(dt);
        repaint();
    }

    private void step(double dt) {
        timeSeconds += dt;

        // Pellets fall more slowly with water drag
        for (Pellet p : pellets) {
            p.age += dt;
            if (!p.settled) {
                p.vy += 18 * dt;             // gentle gravity
                p.vy *= (1.0 - 0.22 * dt);   // water drag
                if (p.vy > 80) p.vy = 80;    // soft terminal velocity
                p.y += p.vy * dt;

                if (p.y >= tankFloorY() - p.radius) {
                    p.y = tankFloorY() - p.radius;
                    p.vy = 0;
                    p.settled = true;
                }
            }
            // Algae growth if pellet lingers
            if (p.age > 12) {
                algaeLevel = Math.min(100, algaeLevel + 2.2 * dt);
                dirt = Math.min(100, dirt + 1.6 * dt);
            }
        }

        // Poops sink a bit, add to dirt slowly
        for (Poop poop : poops) {
            poop.age += dt;
            if (!poop.settled) {
                poop.vy += 55 * dt;
                poop.vy *= (1.0 - 0.18 * dt);
                if (poop.vy > 110) poop.vy = 110;
                poop.y += poop.vy * dt;
                if (poop.y >= tankFloorY() - poop.radius) {
                    poop.y = tankFloorY() - poop.radius;
                    poop.settled = true;
                }
            }
            dirt = Math.min(100, dirt + 0.18 * dt);
        }

        // Corpses: sink, settle, decompose; contribute to dirt/algae
        for (Corpse c : corpses) {
            if (!c.settled) {
                c.vy += 40 * dt;             // heavier than poop
                c.vy *= (1.0 - 0.20 * dt);
                if (c.vy > 120) c.vy = 120;
                c.y += c.vy * dt;
                if (c.y >= tankFloorY() - c.radius) {
                    c.y = tankFloorY() - c.radius;
                    c.vy = 0;
                    c.settled = true;
                }
            } else {
                c.decay += c.decayRate * dt;
                dirt = Math.min(100, dirt + 0.25 * dt);
                algaeLevel = Math.min(100, algaeLevel + 0.18 * dt);
            }
            c.age += dt;
        }

        // Seaweed cleans nearby poop and consumes settled corpses
        for (Seaweed s : seaweeds) {
            s.phase += dt * 1.0; // sway animation

            // Clean poop
            double cleanedPoops = 0;
            for (Poop poop : poops) {
                if (dist(s.x, s.y - s.h * 0.5, poop.x, poop.y) < s.cleanRadius) {
                    poop.toRemove = true;
                    cleanedPoops += 1;
                }
            }
            if (cleanedPoops > 0) {
                dirt = Math.max(0, dirt - 0.6 * cleanedPoops);
                algaeLevel = Math.max(0, algaeLevel - 0.2 * cleanedPoops);
            }

            // Eat settled corpses
            for (Corpse c : corpses) {
                if (c.settled && !c.consumed) {
                    if (dist(s.x, s.y - s.h * 0.5, c.x, c.y) < s.cleanRadius + c.radius) {
                        double bite = 0.35 * s.eatRate * dt;
                        c.decay += bite;
                        dirt = Math.max(0, dirt - 0.1 * bite);
                        algaeLevel = Math.max(0, algaeLevel - 0.06 * bite);
                        if (c.decay >= 1.0) {
                            c.consumed = true;
                        }
                    }
                }
            }
        }

        // Filter effect
        if (filterOn) {
            double cleanRate = 6.5; // strength
            double takeAlg = Math.min(algaeLevel, cleanRate * dt);
            algaeLevel -= takeAlg;
            dirt = Math.max(0, dirt - (cleanRate * 0.85) * dt);
        } else {
            dirt = Math.min(100, dirt + 0.28 * dt);
        }

        // Fish behavior + aging
        for (Fish f : fish) {
            f.ageSeconds += dt;
            if (!f.alive) continue;

            // appetite increases slowly
            f.hunger = Math.min(1.0, f.hunger + f.hungerRate * dt);

            // scheduled poop after eating
            if (f.stomachTimer > 0) {
                f.stomachTimer -= dt;
                if (f.stomachTimer <= 0) {
                    Poop poop = new Poop(f.x, f.y + f.size * 0.2);
                    poops.add(poop);
                }
            }

            // gradual growth (applied over time)
            if (f.pendingMealGrowth > 0) {
                double d = Math.min(f.pendingMealGrowth, dt * 0.25);
                f.size = Math.min(f.maxSize, f.size + d);
                f.pendingMealGrowth -= d;
            }

            // Natural mortality
            if (f.ageSeconds >= f.lifespanSeconds) {
                f.alive = false; // conversion to corpse handled after loop
            }

            // Decide target/steering
            if (f.scareTimer > 0) {
                f.scareTimer -= dt;
                f.vx *= 0.985;
                f.vy *= 0.985;
            } else {
                chooseAndChaseTarget(f, dt);
            }

            // integrate motion
            f.x += f.vx * dt;
            f.y += f.vy * dt;

            // keep in tank (respect air band at top)
            bounceFishWithinTank(f);
        }

        // Predation pass (hungrier fish more aggressive)
        for (Fish f : fish) {
            if (!f.alive) continue;
            if (f.hunger < 0.40) continue; // must be notably hungry
            for (Fish other : fish) {
                if (other == f || !other.alive) continue;
                if (other.size < f.size * 0.6) {
                    if (dist(f.x, f.y, other.x, other.y) < (f.size + other.size) * 0.38) {
                        other.alive = false;                  // will convert to corpse below
                        f.hunger = Math.max(0, f.hunger - 0.6);
                        f.stomachTimer = 8.0;
                        f.pendingMealGrowth += f.growthPerMeal;
                        break;
                    }
                }
            }
        }

        // Eating pellets / algae (with appetite threshold)
        for (Fish f : fish) {
            if (!f.alive) continue;
            switch (f.type) {
                case MID: {
                    if (f.hunger >= f.eatThreshold) {
                        Pellet target = nearestPellet(f.x, f.y, false);
                        if (target != null && dist(f.x, f.y, target.x, target.y) < f.size * 0.5 + target.radius) {
                            eatPellet(f, target);
                        }
                    }
                    break;
                }
                case BOTTOM: {
                    if (f.hunger >= f.eatThreshold) {
                        Pellet target = nearestPellet(f.x, f.y, true);
                        if (target != null && dist(f.x, f.y, target.x, target.y) < f.size * 0.5 + target.radius) {
                            eatPellet(f, target);
                        }
                    }
                    break;
                }
                case ALGAE: {
                    if (f.hunger >= f.eatThreshold && algaeLevel > 2) {
                        boolean nearWall = f.x < tankBounds.x + 40 || f.x > tankBounds.x + tankBounds.width - 40
                                || f.y > tankFloorY() - 40;
                        double rate = nearWall ? 10.5 : 2.0;
                        double take = Math.min(algaeLevel, rate * dt);
                        algaeLevel -= take;
                        dirt = Math.max(0, dirt - 0.05 * take * dt);
                        f.hunger = Math.max(0, f.hunger - 0.12 * dt);
                        if (take > 0) {
                            f.stomachTimer = Math.max(f.stomachTimer, 5.0);
                            f.pendingMealGrowth += f.growthPerMeal * 0.30 * dt; // nibbling growth
                        }
                    }
                    break;
                }
                default: break;
            }
        }

        // Convert newly dead fish to corpses
        for (Fish f : fish) {
            if (!f.alive && !f.convertedToCorpse) {
                Corpse c = new Corpse(f.x, f.y, f.size * 0.55);
                corpses.add(c);
                f.convertedToCorpse = true;
            }
        }
        fish.removeIf(ff -> !ff.alive && ff.convertedToCorpse);

        // Cleanup removed entities
        pellets.removeIf(p -> p.eaten);
        poops.removeIf(p -> p.toRemove || p.age > 120); // old poop dissolves
        corpses.removeIf(c -> c.consumed || c.decay >= 1.0 || c.age > 300);

        updateStatus();
    }

    private void chooseAndChaseTarget(Fish f, double dt) {
        // Update gliding timer
        f.glideT += dt;

        // Desired velocity default = smooth glide horizontally + soft depth keeping
        double glide = Math.sin(f.glideT * f.glideOmega) * f.glideAmpX; // slow horizontal undulation
        double desiredVX = glide + f.wanderVX;
        double desiredVY = 0.0;

        // soft PD controller to preferred depth band
        double err = (f.preferredY - f.y);
        double band = f.depthBand; // allowed slack
        if (Math.abs(err) > band) {
            double sign = Math.signum(err);
            double adj = (Math.abs(err) - band);
            desiredVY += sign * Math.min(f.speed * 0.6, f.depthKp * adj - f.depthKd * f.vy);
        } else {
            // tiny buoyancy to flatten vertical motion
            desiredVY += -0.15 * f.vy;
        }

        // Cursor attraction
        if (tankBounds.contains(mouse)) {
            double dToMouse = dist(f.x, f.y, mouse.x, mouse.y);
            if (dToMouse < CURSOR_ATTRACT_RADIUS) {
                Point2D dir = dirTo(f.x, f.y, mouse.x, mouse.y);
                double followSpeed = f.speed * (0.8 + 0.4 * (1.0 - Math.min(1.0, dToMouse / CURSOR_ATTRACT_RADIUS)));
                desiredVX = dir.x * followSpeed;
                desiredVY = dir.y * followSpeed;
            }
        }

        // Food attraction (only if hungry)
        if (f.hunger >= f.eatThreshold) {
            Pellet target = (f.type == FishType.BOTTOM) ? nearestPellet(f.x, f.y, true) : nearestPellet(f.x, f.y, false);
            if (target != null) {
                Point2D dir = dirTo(f.x, f.y, target.x, target.y);
                double seek = (f.type == FishType.BOTTOM) ? 0.9 : 1.0;
                desiredVX = lerp(desiredVX, dir.x * f.speed * seek, 0.6);
                desiredVY = lerp(desiredVY, dir.y * f.speed * seek, 0.6);
            }
        }

        // Blend towards desired velocity (steering)
        double steer = 2.0;
        f.vx += (desiredVX - f.vx) * Math.min(1, steer * dt);
        f.vy += (desiredVY - f.vy) * Math.min(1, steer * dt);

        // Water quality affects max speed a bit
        double speedFactor = 1.0 - Math.min(0.5, (dirt + algaeLevel) / 300.0);
        double maxV = f.speed * speedFactor;
        double v = Math.hypot(f.vx, f.vy);
        if (v > maxV) {
            f.vx = f.vx / v * maxV;
            f.vy = f.vy / v * maxV;
        }
    }

    private void eatPellet(Fish f, Pellet p) {
        p.eaten = true;
        f.hunger = Math.max(0, f.hunger - 0.6);
        f.stomachTimer = Math.max(f.stomachTimer, 6.0 + rng.nextDouble() * 3.0); // poop later
        f.pendingMealGrowth += f.growthPerMeal;                                  // accumulate growth
        dirt = Math.min(100, dirt + 0.5); // some waste always
    }

    private void bounceFishWithinTank(Fish f) {
        double left = tankBounds.x + f.size * 0.3;
        double right = tankBounds.x + tankBounds.width - f.size * 0.3;
        double top = tankBounds.y + FEED_TOP_MARGIN + f.size * 0.3; // keep out of air band
        double bottom = tankFloorY() - f.size * 0.3;

        if (f.x < left) { f.x = left; f.vx = Math.abs(f.vx); }
        if (f.x > right) { f.x = right; f.vx = -Math.abs(f.vx); }
        if (f.y < top) { f.y = top; f.vy = Math.abs(f.vy) * 0.5; }
        if (f.y > bottom) { f.y = bottom; f.vy = -Math.abs(f.vy) * 0.5; }
    }

    private int tankFloorY() { return tankBounds.y + tankBounds.height - 10; }

    private Pellet nearestPellet(double x, double y, boolean onlySettled) {
        Pellet best = null;
        double bestD2 = Double.POSITIVE_INFINITY;
        for (Pellet p : pellets) {
            if (onlySettled && !p.settled) continue;
            if (p.eaten) continue;
            double dx = p.x - x, dy = p.y - y;
            double d2 = dx * dx + dy * dy;
            if (d2 < bestD2) { bestD2 = d2; best = p; }
        }
        return best;
    }

    private void updateStatus() {
        statusLabel.setText(String.format(
                "Fish: %d | Pellets: %d | Poops: %d | Corpses: %d | Dirt: %.1f | Algae: %.1f | Filter: %s | Time: %ds%s",
                fish.size(), pellets.size(), poops.size(), corpses.size(), dirt, algaeLevel, (filterOn ? "ON" : "OFF"),
                (int) timeSeconds, (paused ? " (PAUSED)" : "")
        ));
    }

    /* ===================== Input ===================== */

    @Override
    public void mouseClicked(MouseEvent e) {
        if (!tankBounds.contains(e.getPoint())) return;
        if (SwingUtilities.isLeftMouseButton(e)) {
            // Top-click feeds; lower-click shakes the tank
            if (e.getY() <= tankBounds.y + FEED_TOP_MARGIN) {
                dropPelletAt(e.getX());
            } else {
                shakeTank(e.getPoint());
            }
        } else if (SwingUtilities.isRightMouseButton(e)) {
            // Right-click also shakes (like tapping glass)
            shakeTank(e.getPoint());
        }
    }

    private void dropPelletAt(double x) {
        double y = tankBounds.y + 5; // drop from the air band
        pellets.add(new Pellet(x, y));
    }

    private void shakeTank(Point p) {
        for (Fish f : fish) {
            if (!f.alive) continue;
            Point2D dir = dirAway(f.x, f.y, p.x, p.y);
            double kick = f.speed * (2.0 + rng.nextDouble() * 0.8);
            f.vx = dir.x * kick + (rng.nextDouble() - 0.5) * f.speed * 0.4;
            f.vy = dir.y * kick + (rng.nextDouble() - 0.5) * f.speed * 0.4;
            f.scareTimer = 0.6 + rng.nextDouble() * 0.7;
        }
        dirt = Math.min(100, dirt + 0.8); // shaking adds disturbance
    }

    @Override public void mousePressed(MouseEvent e) { mouse = e.getPoint(); }
    @Override public void mouseDragged(MouseEvent e) { mouse = e.getPoint(); }
    @Override public void mouseReleased(MouseEvent e) { }
    @Override public void mouseMoved(MouseEvent e) { mouse = e.getPoint(); }
    @Override public void mouseEntered(MouseEvent e) { }
    @Override public void mouseExited(MouseEvent e) { }
    @Override public void keyTyped(KeyEvent e) { }
    @Override public void keyReleased(KeyEvent e) { }
    @Override public void keyPressed(KeyEvent e) { addFishKey(Character.toUpperCase(e.getKeyChar())); }

    /* ===================== Rendering ===================== */

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        paintWater(g2);

        // Tank border
        g2.setColor(new Color(180, 210, 230, 180));
        g2.setStroke(new BasicStroke(3f));
        g2.drawRect(tankBounds.x, tankBounds.y, tankBounds.width, tankBounds.height);

        // Floor
        g2.setColor(new Color(150, 120, 90));
        g2.fillRect(tankBounds.x, tankFloorY(), tankBounds.width, 10);

        // Seaweed (behind pellets/poop/corpses so it looks planted)
        for (Seaweed s : seaweeds) paintSeaweed(g2, s);

        // Pellets
        for (Pellet p : pellets) {
            g2.setColor(p.settled ? new Color(110, 80, 40) : new Color(160, 120, 60));
            g2.fill(new Ellipse2D.Double(p.x - p.radius, p.y - p.radius, p.radius * 2, p.radius * 2));
        }

        // Poop
        for (Poop poop : poops) {
            g2.setColor(new Color(90, 70, 50));
            g2.fill(new Ellipse2D.Double(poop.x - poop.radius, poop.y - poop.radius, poop.radius * 2, poop.radius * 2));
        }

        // Corpses
        for (Corpse c : corpses) {
            paintCorpse(g2, c);
        }

        // Fish
        for (Fish f : fish) {
            paintFish(g2, f);
        }

        // Filter indicator
        paintFilter(g2);

        g2.dispose();
    }

    private void paintWater(Graphics2D g2) {
        // Air band (top color)
        g2.setColor(new Color(230, 240, 255));
        g2.fillRect(tankBounds.x, tankBounds.y, tankBounds.width, FEED_TOP_MARGIN);

        // Water gradient below air band
        int waterY = tankBounds.y + FEED_TOP_MARGIN;
        int waterH = tankBounds.height - FEED_TOP_MARGIN;
        GradientPaint gp = new GradientPaint(
                tankBounds.x, waterY, waterColorBase.brighter(),
                tankBounds.x, waterY + waterH, waterColorBase.darker());
        g2.setPaint(gp);
        g2.fillRect(tankBounds.x, waterY, tankBounds.width, waterH);

        // Dirt / algae tints
        if (dirt > 0) {
            float alpha = (float) Math.min(0.6, dirt / 120.0);
            g2.setColor(new Color(dirtTint.getRed(), dirtTint.getGreen(), dirtTint.getBlue(), (int) (alpha * 255)));
            g2.fillRect(tankBounds.x, waterY, tankBounds.width, waterH);
        }
        if (algaeLevel > 0) {
            float alpha = (float) Math.min(0.6, algaeLevel / 120.0);
            g2.setColor(new Color(algaeTint.getRed(), algaeTint.getGreen(), algaeTint.getBlue(), (int) (alpha * 255)));
            g2.fillRect(tankBounds.x, waterY, tankBounds.width, waterH);
        }
    }

    private void paintSeaweed(Graphics2D g2, Seaweed s) {
        g2.setStroke(new BasicStroke(3f));
        double baseX = s.x;
        double baseY = s.y;
        int strands = 5;
        for (int i = 0; i < strands; i++) {
            double off = (i - (strands - 1) / 2.0) * 6.0;
            double h = s.h * (0.8 + i * 0.05);
            double sway = Math.sin(s.phase + i * 0.8) * 8.0;
            int x1 = (int) (baseX + off);
            int y1 = (int) baseY;
            int x2 = (int) (baseX + off + sway);
            int y2 = (int) (baseY - h);
            g2.setColor(new Color(40, 150 + i * 15, 60));
            g2.drawLine(x1, y1, x2, y2);
        }
    }

    private void paintCorpse(Graphics2D g2, Corpse c) {
        double bodyLen = c.radius * 3.0;
        double bodyHt = c.radius * 1.6;

        // faded gray-brown, more transparent as it decays
        float a = (float) Math.max(0.15, 1.0 - c.decay);
        Color base = new Color(120, 110, 100, (int) (a * 255));

        AffineTransform old = g2.getTransform();
        g2.translate(c.x, c.y);
        g2.rotate(Math.PI * 0.5); // sideways
        g2.setColor(base);
        g2.fill(new Ellipse2D.Double(-bodyLen * 0.5, -bodyHt * 0.5, bodyLen, bodyHt));
        g2.setTransform(old);
    }

    private void paintFish(Graphics2D g2, Fish f) {
        double dir = Math.atan2(f.vy, f.vx);
        double bodyLen = f.size * 1.6;
        double bodyHt = f.size * 0.8;

        // Base color by species
        Color base = f.species.baseColor;

        AffineTransform old = g2.getTransform();
        g2.translate(f.x, f.y);
        g2.rotate(dir);

        // Body
        g2.setColor(base);
        g2.fill(new Ellipse2D.Double(-bodyLen * 0.5, -bodyHt * 0.5, bodyLen, bodyHt));

        // Tail shape depends on species
        Polygon tail = new Polygon();
        int tailLen = (int) (bodyLen * (f.species.tailLong ? 0.35 : 0.25));
        tail.addPoint((int) (-bodyLen * 0.5), 0);
        tail.addPoint((int) (-bodyLen * 0.5 - tailLen), (int) (-bodyHt * (f.species.tailWide ? 0.5 : 0.35)));
        tail.addPoint((int) (-bodyLen * 0.5 - tailLen), (int) (bodyHt * (f.species.tailWide ? 0.5 : 0.35)));
        g2.setColor(base.darker());
        g2.fill(tail);

        // Fins tint
        g2.setColor(f.species.finTint);
        g2.fill(new Ellipse2D.Double(-bodyLen * 0.1, -bodyHt * 0.6, bodyHt * 0.45, bodyHt * 0.25)); // dorsal
        g2.fill(new Ellipse2D.Double(0, bodyHt * 0.2, bodyHt * 0.5, bodyHt * 0.25));               // ventral

        // Patterning (stripes or spots)
        if (f.species.pattern == SpeciesPattern.STRIPES) {
            g2.setStroke(new BasicStroke(2f));
            g2.setColor(f.species.patternColor);
            for (int i = -2; i <= 2; i++) {
                int x = (int) (i * (bodyLen * 0.15));
                g2.drawLine(x, (int) (-bodyHt * 0.45), x, (int) (bodyHt * 0.45));
            }
        } else if (f.species.pattern == SpeciesPattern.SPOTS) {
            g2.setColor(f.species.patternColor);
            for (int i = -1; i <= 1; i++) {
                double cx = i * (bodyLen * 0.18);
                g2.fill(new Ellipse2D.Double(cx - bodyHt * 0.12, -bodyHt * 0.12, bodyHt * 0.24, bodyHt * 0.24));
            }
        }

        // Eye
        g2.setColor(Color.WHITE);
        g2.fill(new Ellipse2D.Double(bodyLen * 0.22, -bodyHt * 0.2, bodyHt * 0.25, bodyHt * 0.25));
        g2.setColor(Color.BLACK);
        g2.fill(new Ellipse2D.Double(bodyLen * 0.27, -bodyHt * 0.15, bodyHt * 0.12, bodyHt * 0.12));

        g2.setTransform(old);
    }

    private void paintFilter(Graphics2D g2) {
        int fx = tankBounds.x + tankBounds.width - 28;
        int fy = tankBounds.y + 10;
        g2.setColor(filterOn ? new Color(140, 210, 240) : new Color(90, 90, 90));
        g2.fillRoundRect(fx, fy, 16, 60, 6, 6);
        g2.setColor(Color.DARK_GRAY);
        g2.drawRoundRect(fx, fy, 16, 60, 6, 6);
        if (filterOn) {
            g2.setColor(new Color(200, 230, 255, 120));
            g2.fillRect(fx - 6, fy + 12, 6, 30);
        }
    }

    /* ===================== Factory Helpers ===================== */

    private Fish makeMidWaterFish(double size) { return new Fish(FishType.MID, size, tankBounds, rng); }
    private Fish makeBottomFeeder(double size) { return new Fish(FishType.BOTTOM, size, tankBounds, rng); }
    private Fish makeAlgaeEater(double size) { return new Fish(FishType.ALGAE, size, tankBounds, rng); }

    /* ===================== Math helpers ===================== */

    double dist(double x1, double y1, double x2, double y2) {
        double dx = x2 - x1, dy = y2 - y1;
        return Math.hypot(dx, dy);
    }

    Point2D dirTo(double x1, double y1, double x2, double y2) {
        double dx = x2 - x1, dy = y2 - y1;
        double d = Math.hypot(dx, dy);
        if (d < 1e-6) return new Point2D(0, 0);
        return new Point2D(dx / d, dy / d);
    }

    Point2D dirAway(double x1, double y1, double x2, double y2) {
        Point2D to = dirTo(x1, y1, x2, y2);
        return new Point2D(-to.x, -to.y);
    }

    double lerp(double a, double b, double t) { return a + (b - a) * t; }
}

/* ===================== Entities & Types (Top-Level, Non-Public) ===================== */

enum FishType { MID, BOTTOM, ALGAE }
enum SpeciesPattern { NONE, STRIPES, SPOTS }

class Species {
    final Color baseColor;
    final Color finTint;
    final Color patternColor;
    final SpeciesPattern pattern;
    final boolean tailLong;
    final boolean tailWide;

    Species(Color baseColor, Color finTint, Color patternColor, SpeciesPattern pattern, boolean tailLong, boolean tailWide) {
        this.baseColor = baseColor;
        this.finTint = finTint;
        this.patternColor = patternColor;
        this.pattern = pattern;
        this.tailLong = tailLong;
        this.tailWide = tailWide;
    }

    static Species randomSpeciesFor(FishType type, Random rng) {
        switch (type) {
            case MID:
                switch (rng.nextInt(4)) {
                    case 0: return new Species(new Color(255, 180, 70), new Color(255, 230, 160), new Color(180, 110, 20), SpeciesPattern.STRIPES, false, true);
                    case 1: return new Species(new Color(220, 90, 90), new Color(255, 190, 190), new Color(140, 30, 30), SpeciesPattern.SPOTS, true, false);
                    case 2: return new Species(new Color(120, 180, 255), new Color(200, 230, 255), new Color(60, 100, 180), SpeciesPattern.STRIPES, true, true);
                    default: return new Species(new Color(240, 220, 120), new Color(255, 240, 180), new Color(170, 150, 60), SpeciesPattern.NONE, false, false);
                }
            case BOTTOM:
                switch (rng.nextInt(3)) {
                    case 0: return new Species(new Color(190, 170, 110), new Color(220, 200, 150), new Color(120, 100, 70), SpeciesPattern.SPOTS, false, true);
                    case 1: return new Species(new Color(170, 150, 100), new Color(200, 180, 140), new Color(120, 100, 70), SpeciesPattern.NONE, false, false);
                    default: return new Species(new Color(160, 140, 120), new Color(210, 190, 170), new Color(100, 80, 60), SpeciesPattern.STRIPES, false, false);
                }
            case ALGAE:
                switch (rng.nextInt(3)) {
                    case 0: return new Species(new Color(100, 210, 120), new Color(180, 255, 200), new Color(60, 150, 70), SpeciesPattern.NONE, false, true);
                    case 1: return new Species(new Color(90, 200, 170), new Color(180, 250, 230), new Color(40, 140, 120), SpeciesPattern.STRIPES, true, false);
                    default: return new Species(new Color(120, 220, 110), new Color(200, 255, 190), new Color(70, 160, 60), SpeciesPattern.SPOTS, false, false);
                }
            default:
                return new Species(new Color(200, 200, 200), new Color(220, 220, 220), new Color(150, 150, 150), SpeciesPattern.NONE, false, false);
        }
    }
}

class Fish {
    final FishType type;
    final Species species;

    // Kinematics
    double x, y;
    double vx, vy;
    double wanderVX;
    double size;          // visual scale
    double maxSize;
    double speed;

    // Appetite & metabolism
    double hunger;        // 0..1; higher is hungrier
    double hungerRate;    // per second
    double eatThreshold;  // must exceed to pursue/eat
    double stomachTimer;  // counts down to poop

    // Lifecycle
    double ageSeconds;
    double lifespanSeconds;
    double growthPerMeal;
    double pendingMealGrowth = 0;
    boolean alive = true;
    boolean convertedToCorpse = false;
    double scareTimer = 0;

    // Smooth idle gliding
    double preferredY;    // target depth
    double depthBand;     // slack band around preferred depth
    double depthKp = 0.35, depthKd = 0.25;
    double glideT = 0.0;
    double glideOmega;    // horizontal undulation frequency
    double glideAmpX;     // undulation amplitude

    Fish(FishType type, double size, Rectangle tankBounds, Random rng) {
        this.type = type;
        this.species = Species.randomSpeciesFor(type, rng);
        this.size = size;

        double waterTopY = tankBounds.y + 50; // FEED_TOP_MARGIN (air band)
        double waterBotY = tankBounds.y + tankBounds.height - 10;

        switch (type) {
            case MID:
                maxSize = size * 2.0;
                speed = 80 + rng.nextDouble() * 40;
                x = tankBounds.getCenterX() + rng.nextDouble() * 120 - 60;
                y = waterTopY + (waterBotY - waterTopY) * (0.35 + rng.nextDouble() * 0.30);
                wanderVX = (rng.nextDouble() * 2 - 1) * 20;
                hunger = 0.5;
                hungerRate = 0.02 + rng.nextDouble() * 0.03;
                eatThreshold = 0.35;
                lifespanSeconds = 180 + rng.nextDouble() * 240;
                growthPerMeal = 0.30;

                preferredY = y;
                depthBand = 20 + rng.nextDouble() * 20;
                glideOmega = 0.6 + rng.nextDouble() * 0.4;
                glideAmpX = 20 + rng.nextDouble() * 25;
                break;
            case BOTTOM:
                maxSize = size * 1.8;
                speed = 70 + rng.nextDouble() * 30;
                x = tankBounds.getCenterX() + rng.nextDouble() * 140 - 70;
                y = waterBotY - 30;
                wanderVX = (rng.nextDouble() * 2 - 1) * 15;
                hunger = 0.4;
                hungerRate = 0.015 + rng.nextDouble() * 0.025;
                eatThreshold = 0.30;
                lifespanSeconds = 210 + rng.nextDouble() * 300;
                growthPerMeal = 0.25;

                preferredY = waterBotY - 25;
                depthBand = 15 + rng.nextDouble() * 10;
                glideOmega = 0.5 + rng.nextDouble() * 0.3;
                glideAmpX = 18 + rng.nextDouble() * 22;
                break;
            case ALGAE:
                maxSize = size * 1.7;
                speed = 85 + rng.nextDouble() * 35;
                x = tankBounds.getCenterX() + rng.nextDouble() * 160 - 80;
                y = waterTopY + (waterBotY - waterTopY) * (0.45 + rng.nextDouble() * 0.25);
                wanderVX = (rng.nextDouble() * 2 - 1) * 18;
                hunger = 0.3;
                hungerRate = 0.012 + rng.nextDouble() * 0.02;
                eatThreshold = 0.25;
                lifespanSeconds = 240 + rng.nextDouble() * 300;
                growthPerMeal = 0.20;

                preferredY = y;
                depthBand = 22 + rng.nextDouble() * 18;
                glideOmega = 0.55 + rng.nextDouble() * 0.35;
                glideAmpX = 18 + rng.nextDouble() * 22;
                break;
            default:
                break;
        }
    }
}

class Pellet {
    double x, y;
    double vy = 0;
    double radius = 4;
    boolean settled = false;
    boolean eaten = false;
    double age = 0;

    Pellet(double x, double y) { this.x = x; this.y = y; }
}

class Poop {
    double x, y;
    double vy = 0;
    double radius = 3;
    boolean settled = false;
    double age = 0;
    boolean toRemove = false;

    Poop(double x, double y) { this.x = x; this.y = y; }
}

class Corpse {
    double x, y;
    double vy = 0;
    double radius;
    boolean settled = false;
    double age = 0;
    double decay = 0;         // 0..1
    double decayRate = 0.02;  // per second baseline (plus seaweed bites)
    boolean consumed = false; // fully eaten by plants

    Corpse(double x, double y, double radius) {
        this.x = x; this.y = y; this.radius = radius;
    }
}

class Seaweed {
    double x, y;
    double h;
    double phase;
    double cleanRadius = 42;
    double eatRate = 1.0; // multiplier for corpse-eating

    Seaweed(double x, double y, double h) {
        this.x = x; this.y = y; this.h = h;
        this.phase = new Random().nextDouble() * Math.PI * 2;
    }
}

/* ===================== Small Geometry Helper ===================== */

class Point2D {
    final double x, y;
    Point2D(double x, double y) { this.x = x; this.y = y; }
}
