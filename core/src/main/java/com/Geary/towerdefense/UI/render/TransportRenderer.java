package com.Geary.towerdefense.UI.render;

import com.Geary.towerdefense.Direction;
import com.Geary.towerdefense.entity.buildings.Bridge;
import com.Geary.towerdefense.entity.buildings.Transport;
import com.Geary.towerdefense.world.GameWorld;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class TransportRenderer {

    private final GameWorld world;
    public static final float CELL_MARGIN = 8f;
    public static final float CELL_SIZE = GameWorld.cellSize - 2 * CELL_MARGIN;

    private final ShapeRenderer sr;

    public TransportRenderer(GameWorld world, ShapeRenderer sr) {
        this.world = world;
        this.sr = sr;
        sr.setAutoShapeType(true); // allow multiple shape types in one batch
    }

    /** Draw all transports (real + ghost) */
    public void drawTransports() {
        sr.begin(ShapeRenderer.ShapeType.Filled); // start batch
        // Draw all real transports
        for (Transport t : world.transports) {
            drawTransport(t, false);
        }
        // Draw ghost transport if present
        if (world.ghostTransport != null) {
            drawTransport(world.ghostTransport, true);
        }
        sr.end(); // finish batch
    }

    /** Draw a single transport (filled or line automatically handled) */
    private void drawTransport(Transport t, boolean ghost) {
        float centerX = t.xPos + CELL_MARGIN + CELL_SIZE / 2f;
        float centerY = t.yPos + CELL_MARGIN + CELL_SIZE / 2f;
        float width = CELL_SIZE / 8f;

        // Set color based on type
        if (t instanceof Bridge) {
            sr.setColor(ghost ? new Color(0.2f, 0.6f, 0.4f, 0.4f) : Color.YELLOW);
        } else {
            sr.setColor(ghost ? new Color(0.2f, 0.2f, 0.5f, 0.4f) : Color.BLUE);
        }

        boolean connectedToNetwork = t.isConnectedToNetwork;
        // Set shape type automatically
        sr.set(connectedToNetwork ? ShapeRenderer.ShapeType.Filled : ShapeRenderer.ShapeType.Line);
        sr.circle(centerX, centerY, width);

        // Draw rails
        if (t.directions != null) {
            if (t.directions.contains(Direction.UP)) {
                drawTransportRail(centerX - width / 2f, centerY, width, CELL_SIZE / 2f, ghost, connectedToNetwork);
            }
            if (t.directions.contains(Direction.DOWN)) {
                drawTransportRail(centerX - width / 2f, centerY, width, -CELL_SIZE / 2f, ghost, connectedToNetwork);
            }
            if (t.directions.contains(Direction.LEFT)) {
                drawTransportRail(centerX, centerY - width / 2f, -CELL_SIZE / 2f, width, ghost, connectedToNetwork);
            }
            if (t.directions.contains(Direction.RIGHT)) {
                drawTransportRail(centerX, centerY - width / 2f, CELL_SIZE / 2f, width, ghost, connectedToNetwork);
            }
        }
    }

    private void drawTransportRail(float x, float y, float xLen, float yLen, boolean ghost, boolean connected) {
        // Outer rail
        sr.set(connected ? ShapeRenderer.ShapeType.Filled : ShapeRenderer.ShapeType.Line);
        sr.setColor(ghost ? new Color(0.3f, 0f, 0f, 0.4f) : Color.BLUE);
        sr.rect(x, y, xLen, yLen);

        // Inner highlight: only for filled rails
        if (connected) {
            sr.setColor(ghost ? new Color(0.8f, 0.8f, 0.8f, 0.4f) : Color.WHITE);
            sr.rect(x + 2f, y + 2f, xLen - 4f, yLen - 4f);
        }
    }
}
