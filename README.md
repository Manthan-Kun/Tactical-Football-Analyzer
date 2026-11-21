# Tactical Football Pitch Analyzer

Interactive Java Swing visualizer modeling spatial control, passing lanes, and live match stats on a 105m x 68m pitch.

## Features
- Dynamic Voronoi territory calculation and live territory %
- Interactive player dragging and click-to-switch ball carrier
- Passing lane detection using vector projection (2.2m interception threshold)
- Live HUD overlay for territory, open/blocked lanes, and ball carrier
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
