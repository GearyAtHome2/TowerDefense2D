package com.Geary.towerdefense.UI.render;

import com.Geary.towerdefense.Direction;
import com.Geary.towerdefense.entity.buildings.Bridge;
import com.Geary.towerdefense.entity.buildings.Transport;
import com.Geary.towerdefense.world.GameWorld;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class TransportRenderer {

    private final GameWorld world;
    private final ShapeRenderer sr;

    public TransportRenderer(GameWorld world, ShapeRenderer sr) {
        this.world = world;
        this.sr = sr;
        sr.setAutoShapeType(true); // allow multiple shape types in one batch
    }

    /** Draw all transports (real + ghost) */
    public void drawTransports() {
        sr.begin(ShapeRenderer.ShapeType.Filled);
        for (Transport t : world.transports) {
            drawTransport(t, false);
        }
        if (world.ghostTransport != null) {
            drawTransport(world.ghostTransport, true);
        }
        sr.end();
    }

    /** Draw a single transport */
    private void drawTransport(Transport t, boolean ghost) {
        float centerX = t.xPos; // center-based
        float centerY = t.yPos;
        float width = t.size / 8f; // use transport size

        // Color based on type
        if (t instanceof Bridge) {
            sr.setColor(ghost ? new Color(0.2f, 0.6f, 0.4f, 0.4f) : Color.YELLOW);
        } else {
            sr.setColor(ghost ? new Color(0.2f, 0.2f, 0.5f, 0.4f) : Color.BLUE);
        }

        boolean connected = t.isConnectedToNetwork;
        sr.set(connected ? ShapeRenderer.ShapeType.Filled : ShapeRenderer.ShapeType.Line);
        sr.circle(centerX, centerY, width);

        // Draw rails
        if (t.directions != null) {
            for (Direction dir : t.directions) {
                switch (dir) {
                    case UP -> drawTransportRail(centerX - width / 2f, centerY, 0, t.size / 2f, ghost, connected);
                    case DOWN -> drawTransportRail(centerX - width / 2f, centerY, 0, -t.size / 2f, ghost, connected);
                    case LEFT -> drawTransportRail(centerX - t.size / 2f, centerY - width / 2f, t.size / 2f, 0, ghost, connected);
                    case RIGHT -> drawTransportRail(centerX, centerY - width / 2f, t.size / 2f, 0, ghost, connected);
                }
            }
        }
    }

    private void drawTransportRail(float x, float y, float xLen, float yLen, boolean ghost, boolean connected) {
        sr.set(connected ? ShapeRenderer.ShapeType.Filled : ShapeRenderer.ShapeType.Line);
        sr.setColor(ghost ? new Color(0.3f, 0f, 0f, 0.4f) : Color.BLUE);
        sr.rect(x, y, xLen, yLen);

        if (connected) {
            sr.setColor(ghost ? new Color(0.8f, 0.8f, 0.8f, 0.4f) : Color.WHITE);
            sr.rect(x + 2f, y + 2f, xLen - 4f, yLen - 4f);
        }
    }
}
