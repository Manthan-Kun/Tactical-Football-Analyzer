import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class PitchPanel extends JPanel implements MouseListener, MouseMotionListener {

    private boolean wasDragged = false;

    private List<Player> players;
    private Player draggedPlayer = null; // Tracks the player currently being dragged{
    private Player ballCarrier = null; // Who currently has the ball

    // Live Game Statistics
    private double homeTerritoryPercent = 50.0;
    private double awayTerritoryPercent = 50.0;
    private int openLanesCount = 0;
    private int blockedLanesCount = 0;

    public PitchPanel() {
        players = new ArrayList<>();

        setFocusable(true);
        requestFocusInWindow();

        addMouseListener(this);
        addMouseMotionListener(this);

        String input = JOptionPane.showInputDialog(
                this,
                "Enter main player name or role (e.g. CM, ST, CAM):",
                "Select Main Player",
                JOptionPane.PLAIN_MESSAGE
        );


        String mainPlayerQuery = (input == null || input.trim().isEmpty()) ? "CM (You)" : input.trim();

// --- YOUR TEAM (Home - Green Zone Control) ---
        players.add(new Player("GK", 5.0, 34.0, true));
        players.add(new Player("LB", 25.0, 10.0, true));
        players.add(new Player("CB1", 25.0, 25.0, true));
        players.add(new Player("CB2", 25.0, 43.0, true));
        players.add(new Player("RB", 25.0, 58.0, true));
        players.add(new Player("CDM", 45.0, 34.0, true));
        players.add(new Player("CM ", 52.5, 34.0, true));
        players.add(new Player("LM", 65.0, 12.0, true));
        players.add(new Player("RM", 65.0, 56.0, true));
        players.add(new Player("CAM", 75.0, 34.0, true));
        players.add(new Player("ST", 85.0, 34.0, true));

// --- OPPOSING TEAM (Away - Red Zone Control) ---
        players.add(new Player("Opp GK", 100.0, 34.0, false));
        players.add(new Player("Opp LB", 80.0, 10.0, false));
        players.add(new Player("Opp CB1", 80.0, 25.0, false));
        players.add(new Player("Opp CB2", 80.0, 43.0, false));
        players.add(new Player("Opp RB", 80.0, 58.0, false));
        players.add(new Player("Opp CDM", 60.0, 34.0, false));
        players.add(new Player("Opp CM1", 48.0, 22.0, false));
        players.add(new Player("Opp CM2", 48.0, 46.0, false));
        players.add(new Player("Opp LM", 35.0, 12.0, false));
        players.add(new Player("Opp RM", 35.0, 56.0, false));
        players.add(new Player("Opp ST", 20.0, 34.0, false));

        for (Player p : players) {
            if (p.isHomeTeam() && p.getName().toLowerCase().contains(mainPlayerQuery.toLowerCase())) {
                ballCarrier = p;
                break;
            }
        }

// Fallback so ballCarrier is never null even if you type something unmatched
        if (ballCarrier == null && !players.isEmpty()) {
            ballCarrier = players.get(0);
        }

    }

    @Override
    protected void paintComponent(Graphics g) {


        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        int panelWidth = getWidth();
        int panelHeight = getHeight();

        // Scale real-world meters to fit whatever window size you open
        double scaleX = (double) panelWidth / 105.0;
        double scaleY = (double) panelHeight / 68.0;

        // 1. Paint the background green like turf
        g2d.setColor(new Color(34, 139, 34));
        g2d.fillRect(0, 0, panelWidth, panelHeight);

        int homeCells = 0;
        int awayCells = 0;

        // 2. Voronoi Space Control Calculation
        // We scan across the pitch in steps of 8 pixels to keep it super fast
        for (int px = 0; px < panelWidth; px += 8) {
            for (int py = 0; py < panelHeight; py += 8) {
                double gx = px / scaleX; // Convert pixel back to meters
                double gy = py / scaleY;

                Player closest = null;
                double minDist = Double.MAX_VALUE;

                // For every grid square, check every player to find who is closest
                for (Player p : players) {
                    double dist = Math.sqrt(Math.pow(p.getX() - gx, 2) + Math.pow(p.getY() - gy, 2));
                    if (dist < minDist) {
                        minDist = dist;
                        closest = p;
                    }
                }

                // Color the space blue if your team owns it, red if the opponent does
                if (closest != null) {
                    if (closest.isHomeTeam()) {
                        g2d.setColor(new Color(0, 180, 0)); // Transparent Blue
                    } else {
                        g2d.setColor(new Color(190, 0, 0)); // Transparent Red
                    }
                    g2d.fillRect(px, py, 8, 8);
                }

                if (closest != null) {
                    if (closest.isHomeTeam()) {
                        g2d.setColor(new Color(0, 180, 0));
                        homeCells++;
                    } else {
                        g2d.setColor(new Color(190, 0, 0));
                        awayCells++;
                    }
                    g2d.fillRect(px, py, 8, 8);
                }
            }
        }

        int totalCells = homeCells + awayCells;
        if (totalCells > 0) {
            homeTerritoryPercent = (homeCells * 100.0) / totalCells;
            awayTerritoryPercent = (awayCells * 100.0) / totalCells;
        }

        // 3. Draw Professional Pitch Markings (Outer boundary, halfway line, center circle, penalty boxes)
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(2));

        // Outer Boundary Box (The touchlines and goal lines)
        g2d.drawRect(20, 20, panelWidth - 40, panelHeight - 40);

        // Halfway Line
        g2d.drawLine(panelWidth / 2, 20, panelWidth / 2, panelHeight - 20);

        // Center Circle
        g2d.drawOval(panelWidth / 2 - 50, panelHeight / 2 - 50, 100, 100);
        g2d.fillOval(panelWidth / 2 - 4, panelHeight / 2 - 4, 8, 8); // Center spot

        // Left Penalty Box / Goal Area
        g2d.drawRect(20, panelHeight / 2 - 100, 80, 200);

        // Right Penalty Box / Goal Area
        g2d.drawRect(panelWidth - 100, panelHeight / 2 - 100, 80, 200);
        g2d.drawLine(panelWidth / 2, 20, panelWidth / 2, panelHeight - 20);

        // Center Circle
        g2d.drawOval(panelWidth / 2 - 50, panelHeight / 2 - 50, 100, 100);
        g2d.fillOval(panelWidth / 2 - 4, panelHeight / 2 - 4, 8, 8); // Center spot

        // Left Penalty Box / Goal Area
        g2d.drawRect(20, panelHeight / 2 - 100, 80, 200);

        // Right Penalty Box / Goal Area
        g2d.drawRect(panelWidth - 100, panelHeight / 2 - 100, 80, 200);


        // Call this right before drawing the players
        drawPassingLanes(g2d, scaleX, scaleY);


        // 4. Draw the players as dots with their names
        for (Player p : players) {

                int screenX = (int) (p.getX() * scaleX);
                int screenY = (int) (p.getY() * scaleY);

            if (p == ballCarrier) {
                g2d.setColor(new Color(241, 0, 150)); // Electric Gold / Yellow for Ball Carrier
            }
           else if (p.isHomeTeam()) {
                g2d.setColor(Color.CYAN);
            } else {
                g2d.setColor(Color.ORANGE);
            }
            g2d.fillOval(screenX - 8, screenY - 8, 16, 16);
            g2d.setColor(Color.WHITE);
            g2d.drawString(p.getName(), screenX + 10, screenY + 4);
        }

        // 3. Draw white pitch lines (Halfway line & center circle)
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawLine(panelWidth / 2, 0, panelWidth / 2, panelHeight);
        g2d.drawOval(panelWidth / 2 - 50, panelHeight / 2 - 50, 100, 100);

        // Draw live tactical stats HUD on top
        drawStatsOverlay(g2d);

    }
    @Override
    public void mousePressed(MouseEvent e) {
        int mouseX = e.getX();
        int mouseY = e.getY();

        int panelWidth = getWidth();
        int panelHeight = getHeight();

        wasDragged = false; // Reset drag flag on every new press

        for (Player p : players) {
            int screenX = (int) ((p.getX() / 100.0) * panelWidth);
            int screenY = (int) ((p.getY() / 68.0) * panelHeight);

            double distance = Math.sqrt(Math.pow(mouseX - screenX, 2) + Math.pow(mouseY - screenY, 2));

            if (distance < 38) {
                draggedPlayer = p;
                break;
            }
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (draggedPlayer != null) {
            wasDragged = true; // Mark that we're dragging, not just clicking

            int panelWidth = getWidth();
            int panelHeight = getHeight();

            double newX = ((double) e.getX() / panelWidth) * 100.0;
            double newY = ((double) e.getY() / panelHeight) * 68.0;

            newX = Math.max(2, Math.min(98, newX));
            newY = Math.max(2, Math.min(66, newY));

            draggedPlayer.setX(newX);
            draggedPlayer.setY(newY);

            repaint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // If the user clicked on a player without dragging them, hand the ball to that player!
        if (draggedPlayer != null && !wasDragged) {
            ballCarrier = draggedPlayer;
            repaint();
        }

        draggedPlayer = null;
        wasDragged = false;
    }

    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
    @Override public void mouseMoved(MouseEvent e) {}

    // 1. Draws passing lanes dynamically from whoever holds the ball
    private void drawPassingLanes(Graphics2D g2d, double scaleX, double scaleY) {
        if (ballCarrier == null) return;
        openLanesCount = 0;
        blockedLanesCount = 0;
        List<Player> teammates = new ArrayList<>();
        List<Player> opponents = new ArrayList<>();

        // Separate players based on team relative to current ballCarrier
        for (Player p : players) {
            if (p == ballCarrier) continue; // Skip the carrier themselves

            if (p.isHomeTeam() == ballCarrier.isHomeTeam()) {
                teammates.add(p);
            } else {
                opponents.add(p);
            }
        }

        if (teammates.isEmpty()) return;

        // Sort teammates by Euclidean distance from the ball carrier (nearest first)
        teammates.sort((p1, p2) -> {
            double d1 = Math.hypot(p1.getX() - ballCarrier.getX(), p1.getY() - ballCarrier.getY());
            double d2 = Math.hypot(p2.getX() - ballCarrier.getX(), p2.getY() - ballCarrier.getY());
            return Double.compare(d1, d2);
        });

        // Take the nearest 3 teammates to evaluate passing options
        int limit = Math.min(3, teammates.size());
        double interceptThreshold = 2.2; // Interception radius in meters

        for (int i = 0; i < limit; i++) {
            Player target = teammates.get(i);
            boolean isIntercepted = false;

            // Check every opponent against this passing line segment
            for (Player opp : opponents) {
                double distToLane = pointToSegmentDistance(
                        opp.getX(), opp.getY(),
                        ballCarrier.getX(), ballCarrier.getY(),
                        target.getX(), target.getY()
                );

                if (distToLane <= interceptThreshold) {
                    isIntercepted = true;
                    break; // One interceptor blocks the lane
                }
            }

            // Convert meter coordinates to pixel coordinates for drawing
            int x1 = (int) (ballCarrier.getX() * scaleX);
            int y1 = (int) (ballCarrier.getY() * scaleY);
            int x2 = (int) (target.getX() * scaleX);
            int y2 = (int) (target.getY() * scaleY);

            // Color code: Dashed Red for blocked, Solid Green for open lane
            if (isIntercepted) {
                blockedLanesCount++;
                g2d.setColor(new Color(235, 64, 52, 220));
                g2d.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0, new float[]{8, 6}, 0));
            } else {
                openLanesCount++;
                g2d.setColor(new Color(46, 204, 113, 230));
                g2d.setStroke(new BasicStroke(3.0f));
            }

            drawArrowLine(g2d, x1, y1, x2, y2, 12);
        }
    }
    private void drawStatsOverlay(Graphics2D g2d) {
        int boxX = 32;
        int boxY = 32;
        int boxW = 250;
        int boxH = 104;

        // Dark translucent card background
        g2d.setColor(new Color(15, 23, 42, 90));
        g2d.fillRoundRect(boxX, boxY, boxW, boxH, 12, 12);

        // Subtle border
        g2d.setColor(new Color(255, 255, 255, 60));
        g2d.setStroke(new BasicStroke(1.5f));
        g2d.drawRoundRect(boxX, boxY, boxW, boxH, 12, 12);

        g2d.setFont(new Font("SansSerif", Font.BOLD, 12));
        g2d.setColor(Color.WHITE);
        g2d.drawString("MATCH TACTICAL STATS", boxX + 14, boxY + 20);

        g2d.setFont(new Font("SansSerif", Font.PLAIN, 12));
        g2d.drawString(
                String.format("Territory: Home %.1f%% | Away %.1f%%", homeTerritoryPercent, awayTerritoryPercent),
                boxX + 14, boxY + 42
        );

        g2d.drawString(
                String.format("Pass Lanes: %d Open / %d Blocked", openLanesCount, blockedLanesCount),
                boxX + 14, boxY + 62
        );

        String carrierName = (ballCarrier != null) ? ballCarrier.getName() : "None";
        String sideLabel = (ballCarrier != null) ? (ballCarrier.isHomeTeam() ? "Home" : "Away") : "-";
        g2d.setColor(new Color(241, 196, 15));
        g2d.drawString("Ball Carrier: " + carrierName + " (" + sideLabel + ")", boxX + 14, boxY + 84);
    }
    // 2. Vector projection math to check if an opponent blocks the pass line
    private double pointToSegmentDistance(double px, double py, double x1, double y1, double x2, double y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        double lengthSquared = dx * dx + dy * dy;

        if (lengthSquared == 0) return Math.hypot(px - x1, py - y1);

        double t = ((px - x1) * dx + (py - y1) * dy) / lengthSquared;
        t = Math.max(0, Math.min(1, t));

        double closestX = x1 + t * dx;
        double closestY = y1 + t * dy;

        return Math.hypot(px - closestX, py - closestY);
    }

    // 3. Arrowhead drawing helper
    private void drawArrowLine(Graphics2D g2d, int x1, int y1, int x2, int y2, int arrowSize) {
        double angle = Math.atan2(y2 - y1, x2 - x1);
        int endX = (int) (x2 - 14 * Math.cos(angle));
        int endY = (int) (y2 - 14 * Math.sin(angle));

        g2d.drawLine(x1, y1, endX, endY);

        g2d.setStroke(new BasicStroke(1.5f));
        int xArrow1 = (int) (endX - arrowSize * Math.cos(angle - Math.PI / 6));
        int yArrow1 = (int) (endY - arrowSize * Math.sin(angle - Math.PI / 6));
        int xArrow2 = (int) (endX - arrowSize * Math.cos(angle + Math.PI / 6));
        int yArrow2 = (int) (endY - arrowSize * Math.sin(angle + Math.PI / 6));

        Polygon arrowHead = new Polygon();
        arrowHead.addPoint(endX, endY);
        arrowHead.addPoint(xArrow1, yArrow1);
        arrowHead.addPoint(xArrow2, yArrow2);

        g2d.fill(arrowHead);
    }

}



