import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.*;

public class BifurcationDiagram extends JFrame {

    public BifurcationDiagram() {
        setTitle("Bifurcation Diagram - Logistic Map");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Create the diagram panel.
        BifurcationPanel diagramPanel = new BifurcationPanel();
        add(diagramPanel, BorderLayout.CENTER);

        // Create a control panel with a vertical BoxLayout.
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));

        // Row 1: rMin and rMax sliders.
        JPanel row1 = new JPanel(new FlowLayout());
        row1.add(new JLabel("r min:"));
        JSlider rMinSlider = new JSlider(JSlider.HORIZONTAL, 20, 40, 25);
        rMinSlider.setMajorTickSpacing(5);
        rMinSlider.setMinorTickSpacing(1);
        rMinSlider.setPaintTicks(true);
        rMinSlider.setPaintLabels(true);
        row1.add(rMinSlider);

        row1.add(new JLabel("r max:"));
        JSlider rMaxSlider = new JSlider(JSlider.HORIZONTAL, 20, 40, 40);
        rMaxSlider.setMajorTickSpacing(5);
        rMaxSlider.setMinorTickSpacing(1);
        rMaxSlider.setPaintTicks(true);
        rMaxSlider.setPaintLabels(true);
        row1.add(rMaxSlider);
        controlPanel.add(row1);

        // Row 2: Animation controls.
        JPanel row2 = new JPanel(new FlowLayout());
        JCheckBox animateCheckBox = new JCheckBox("Animate Drawing");
        row2.add(animateCheckBox);
        JButton startAnimButton = new JButton("Start Animation");
        row2.add(startAnimButton);
        controlPanel.add(row2);

        // Row 3: Zoom Reset, PNG Export, and CSV Export.
        JPanel row3 = new JPanel(new FlowLayout());
        JButton resetZoomButton = new JButton("Reset Zoom");
        row3.add(resetZoomButton);
        JButton exportPNGButton = new JButton("Export PNG");
        row3.add(exportPNGButton);
        JButton exportCSVButton = new JButton("Export CSV");
        row3.add(exportCSVButton);
        controlPanel.add(row3);

        add(controlPanel, BorderLayout.NORTH);

        // --- Listeners for Control Panel ---

        // Update rMin when slider is changed.
        rMinSlider.addChangeListener(e -> {
            int val = rMinSlider.getValue();
            double newRMin = val / 10.0;
            if (newRMin >= diagramPanel.rMax) {
                newRMin = diagramPanel.rMax - 0.1;
                rMinSlider.setValue((int) (newRMin * 10));
            }
            diagramPanel.rMin = newRMin;
            diagramPanel.repaint();
        });

        // Update rMax when slider is changed.
        rMaxSlider.addChangeListener(e -> {
            int val = rMaxSlider.getValue();
            double newRMax = val / 10.0;
            if (newRMax <= diagramPanel.rMin) {
                newRMax = diagramPanel.rMin + 0.1;
                rMaxSlider.setValue((int) (newRMax * 10));
            }
            diagramPanel.rMax = newRMax;
            diagramPanel.repaint();
        });

        // Reset zoom to default visible region.
        resetZoomButton.addActionListener(e -> {
            diagramPanel.rMin = 2.5;
            diagramPanel.rMax = 4.0;
            diagramPanel.yMin = 0;
            diagramPanel.yMax = 1;
            rMinSlider.setValue(25);
            rMaxSlider.setValue(40);
            diagramPanel.repaint();
        });

        // Export the current diagram as a PNG file.
        exportPNGButton.addActionListener(e -> {
            BufferedImage image = new BufferedImage(diagramPanel.getWidth(), diagramPanel.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = image.createGraphics();
            diagramPanel.paint(g2);
            g2.dispose();
            JFileChooser chooser = new JFileChooser();
            int option = chooser.showSaveDialog(BifurcationDiagram.this);
            if (option == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                try {
                    ImageIO.write(image, "png", file);
                    JOptionPane.showMessageDialog(BifurcationDiagram.this, "PNG exported successfully!");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(BifurcationDiagram.this, "Error exporting PNG: " + ex.getMessage());
                }
            }
        });

        // Export simulation data as CSV.
        exportCSVButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            int option = chooser.showSaveDialog(BifurcationDiagram.this);
            if (option == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    // Write CSV header.
                    writer.write("r,iteration,x");
                    writer.newLine();

                    int panelWidth = diagramPanel.getWidth();
                    for (int px = 40; px < panelWidth - 20; px++) {
                        double r = diagramPanel.map(px, 40, panelWidth - 20, diagramPanel.rMin, diagramPanel.rMax);
                        double x = 0.5;
                        for (int i = 0; i < BifurcationPanel.SKIP; i++) {
                            x = r * x * (1 - x);
                        }
                        for (int i = 0; i < BifurcationPanel.ITER; i++) {
                            x = r * x * (1 - x);
                            writer.write(r + "," + i + "," + x);
                            writer.newLine();
                        }
                    }
                    JOptionPane.showMessageDialog(BifurcationDiagram.this, "CSV exported successfully!");
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(BifurcationDiagram.this, "Error exporting CSV: " + ex.getMessage());
                }
            }
        });

        // Animation: if the animate checkbox is selected, start animation when button is clicked.
        startAnimButton.addActionListener(e -> {
            if (animateCheckBox.isSelected()) {
                diagramPanel.startAnimation();
            } else {
                diagramPanel.stopAnimation();
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BifurcationDiagram frame = new BifurcationDiagram();
            frame.setVisible(true);
        });
    }

    // --- Bifurcation Diagram Panel ---
    static class BifurcationPanel extends JPanel implements MouseListener, MouseMotionListener {
        // Visible region for the diagram:
        double rMin = 2.5;
        double rMax = 4.0;
        double yMin = 0;
        double yMax = 1;
        static final int SKIP = 100;  // Transient iterations to skip.
        static final int ITER = 200;  // Iterations to plot per r value.

        // Animation variables.
        boolean animateDrawing = false;
        int currentColumn = 0;
        Timer animationTimer;

        // Zooming variables.
        int zoomStartX, zoomStartY, zoomCurrentX, zoomCurrentY;
        boolean drawingZoomRect = false;

        public BifurcationPanel() {
            addMouseListener(this);
            addMouseMotionListener(this);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            drawDiagram(g);

            // Draw zoom rectangle if in progress.
            if (drawingZoomRect) {
                g.setColor(Color.RED);
                int x = Math.min(zoomStartX, zoomCurrentX);
                int y = Math.min(zoomStartY, zoomCurrentY);
                int w = Math.abs(zoomCurrentX - zoomStartX);
                int h = Math.abs(zoomCurrentY - zoomStartY);
                g.drawRect(x, y, w, h);
            }
        }

        private void drawDiagram(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            int width = getWidth();
            int height = getHeight();

            // Clear background.
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, width, height);

            // Draw title and axes.
            g2.setColor(Color.BLACK);
            g2.drawString("Bifurcation Diagram: x(n+1) = r * x * (1 - x)", 10, 20);
            g2.drawLine(40, height - 40, width - 20, height - 40); // x-axis
            g2.drawLine(40, height - 40, 40, 20);                  // y-axis

            // Determine max column to draw.
            int maxColumn = animateDrawing ? currentColumn : width - 20;
            g2.setColor(new Color(0, 0, 150)); // Deep blue.

            // Plot simulation for each pixel column.
            for (int px = 40; px < maxColumn; px++) {
                double r = map(px, 40, width - 20, rMin, rMax);
                double x = 0.5;  // initial population

                // Skip transient iterations.
                for (int i = 0; i < SKIP; i++) {
                    x = r * x * (1 - x);
                }
                // Plot subsequent iterations.
                for (int i = 0; i < ITER; i++) {
                    x = r * x * (1 - x);
                    int py = (int) map(x, yMin, yMax, height - 40, 20);
                    g2.drawLine(px, py, px, py);
                }
            }
        }

        // Utility: maps a value from one range to another.
        private double map(double value, double srcMin, double srcMax, double dstMin, double dstMax) {
            return dstMin + (value - srcMin) * (dstMax - dstMin) / (srcMax - srcMin);
        }

        // --- Animation Methods ---
        public void startAnimation() {
            animateDrawing = true;
            currentColumn = 40;
            if (animationTimer != null && animationTimer.isRunning()) {
                animationTimer.stop();
            }
            animationTimer = new Timer(10, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    currentColumn++;
                    if (currentColumn >= getWidth() - 20) {
                        animationTimer.stop();
                    }
                    repaint();
                }
            });
            animationTimer.start();
        }

        public void stopAnimation() {
            animateDrawing = false;
            if (animationTimer != null) {
                animationTimer.stop();
            }
            repaint();
        }

        // --- Mouse Events for Zooming ---
        @Override
        public void mousePressed(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON1) {
                zoomStartX = e.getX();
                zoomStartY = e.getY();
                drawingZoomRect = true;
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (drawingZoomRect) {
                zoomCurrentX = e.getX();
                zoomCurrentY = e.getY();
                drawingZoomRect = false;
                int x1 = Math.min(zoomStartX, zoomCurrentX);
                int x2 = Math.max(zoomStartX, zoomCurrentX);
                int y1 = Math.min(zoomStartY, zoomCurrentY);
                int y2 = Math.max(zoomStartY, zoomCurrentY);
                int width = getWidth();
                int height = getHeight();
                if (x2 - x1 > 10 && y2 - y1 > 10) {
                    double newRMin = map(x1, 40, width - 20, rMin, rMax);
                    double newRMax = map(x2, 40, width - 20, rMin, rMax);
                    double newYMax = map(y1, height - 40, 20, yMin, yMax);
                    double newYMin = map(y2, height - 40, 20, yMin, yMax);
                    rMin = newRMin;
                    rMax = newRMax;
                    yMin = newYMin;
                    yMax = newYMax;
                    repaint();
                }
            }
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (drawingZoomRect) {
                zoomCurrentX = e.getX();
                zoomCurrentY = e.getY();
                repaint();
            }
        }

        @Override public void mouseClicked(MouseEvent e) {}
        @Override public void mouseEntered(MouseEvent e) {}
        @Override public void mouseExited(MouseEvent e) {}
        @Override public void mouseMoved(MouseEvent e) {}
    }
}

