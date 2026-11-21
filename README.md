# Tactical Football Pitch Analyzer

An interactive Java Swing desktop application that models pitch space control, passing lanes, and live match telemetry on a scaled `105m × 68m` football pitch.

## What It Does
- **Startup Role Selection:** Prompts for a main player/role on launch (defaulting to `CM (You)` if left blank) and sets them as the initial ball carrier.
- **Voronoi Territory Mapping:** Computes real-time spatial control between Home and Away players and updates live territory percentages.
- **Interactive Pitch Controls:** Drag any player to reshape space and passing options, or click a player to switch the active ball carrier.
- **Passing Lane & Interception Detection:** Evaluates the 3 nearest teammates and marks lanes as open (green) or blocked (red dashed) using a `2.2m` interception threshold.
- **Live HUD Overlay:** Displays ball carrier, territory percentages, and open/blocked lane counts over the pitch using semi-transparent styling.

## How It's Built
- **UI & 2D Graphics (`Java Swing` / `AWT`):** Built around a custom `JPanel` overriding `paintComponent` with `Graphics2D` shapes, lines, arrows, text, and alpha transparency.
- **Coordinate Scaling (`105m × 68m`):** Maps window pixel coordinates to pitch meter coordinates so spatial logic stays consistent across window sizes.
- **Spatial Partitioning (Voronoi Grid):** Evaluates grid points across the pitch against player positions to determine nearest-player ownership and territory share.
- **Event Handling (`MouseListener` & `MouseMotionListener`):** Uses press/drag/release state and a drag flag to separate moving players from clicking to switch ball possession.
- **Vector Projection & Segment Distance:** Projects opponent coordinates onto pass segments (clamping `t` to `[0, 1]`) to measure distance to the lane and detect `2.2m` interceptions.

## How to Run
1. Open the `.java` source files in your Java IDE (such as IntelliJ IDEA) or compile them with `javac`.
2. Run the main application class to launch the Swing window.
-----------------------------------------------------------------------------------------------------------------------------------------
*How It's Built*

### 1. Scaled `105m × 68m` Pitch & Rendering (`Graphics2D`)
* The app uses a custom Swing `JPanel` and overrides `paintComponent` to draw the pitch markings (touchlines, halfway line, center circle, penalty boxes) scaled dynamically to the current window dimensions.
* Converting between screen pixels and real-world pitch meters (`105m` length × `68m` width) keeps spatial measurements consistent even when the window size changes.

### 2. Voronoi Space Control (Nearest-Player Grid)
* The pitch is sampled across a grid (stepped in increments, such as an 8-pixel grid) and converted into pitch meter coordinates.
* For each sampled point, the code measures distance to every player on the pitch and assigns control to whichever player is closest—shading Home and Away zones accordingly and calculating live territory percentages.

### 3. Mouse Interactions: Dragging vs. Ball Carrier Switching
* Implements `MouseListener` and `MouseMotionListener` to track mouse press, drag movement, and release events.
* Dragging moves the selected player across the pitch and triggers a repaint so Voronoi zones and passing options update immediately.
* A quick click without dragging (`wasDragged` stays false) switches `ballCarrier` to the clicked player, allowing possession changes across players.

### 4. Passing Lane Evaluation & Interception Math (`2.2m` Threshold)
* From the current `ballCarrier`, the code filters teammates, sorts them by distance to find the nearest passing options (up to the 3 nearest), and draws directional pass arrows.
* To check if a pass lane is contested, each opponent's position is projected onto the line segment between the passer and receiver (clamping the projection factor `t` between `0` and `1` to stay within the segment).
* If the perpendicular distance from an opponent to that segment is within `2.2m`, the lane is flagged as blocked/intercepted (drawn as a dashed red arrow rather than a solid green one).

### 5. Translucent Live Stats HUD
* A semi-transparent (`alpha` channel) overlay panel renders on top of the pitch to display current possession/ball carrier, Home vs. Away territory share, and open vs. blocked passing lane counts without completely hiding the field beneath it.
