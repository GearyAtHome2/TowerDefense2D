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

    public TransportRenderer(GameWorld world) {
        this.world = world;
    }

    public void drawTransports(ShapeRenderer sr) {
        for (Transport t : world.transports) {
            drawTransports(sr, t, false); // real tower
        }

        if (world.ghostTransport != null) {
            drawTransports(sr, world.ghostTransport, true); // ghost tower
        }
    }

    /**
     * Helper to draw a single tower
     */
    private void drawTransports(ShapeRenderer sr, Transport t, boolean ghost) {
        float centerX = t.xPos + CELL_MARGIN + CELL_SIZE / 2;
        float centerY = t.yPos + CELL_MARGIN + CELL_SIZE / 2;
        float width = CELL_SIZE / 8;


        if (t.getClass().equals(Bridge.class)) sr.setColor(ghost ? new Color(0.2f, 0.6f, 0.4f, 0.4f) : Color.YELLOW);
        else sr.setColor(ghost ? new Color(0.2f, 0.2f, 0.5f, 0.4f) : Color.BLUE);
        ShapeRenderer.ShapeType type = t.isConnectedToNetwork ? ShapeRenderer.ShapeType.Filled : ShapeRenderer.ShapeType.Line;
        sr.set(type);
        sr.circle(centerX, centerY, width);
        if (t.isConnectedToNetwork) {
        }
        if (t.directions != null && t.directions.contains(Direction.UP)) {
            drawTransportRail(sr, centerX - width / 2, centerY, width, CELL_SIZE / 2f, ghost);
        }
        if (t.directions != null && t.directions.contains(Direction.DOWN)) {
            drawTransportRail(sr, centerX - width / 2, centerY, width, -CELL_SIZE / 2f, ghost);
        }
        if (t.directions != null && t.directions.contains(Direction.LEFT)) {
            drawTransportRail(sr, centerX, centerY - width / 2, -CELL_SIZE / 2f, width, ghost);
        }
        if (t.directions != null && t.directions.contains(Direction.RIGHT)) {
            drawTransportRail(sr, centerX, centerY - width / 2, CELL_SIZE / 2f, width, ghost);
        }
    }

    public void drawTransportRail(ShapeRenderer sr, float x, float y, float xLen, float yLen, boolean ghost) {
        sr.setColor(ghost ? new Color(0.3f, 0f, 0f, 0.4f) : Color.BLUE);
        rect(sr, x, y, xLen, yLen);
        sr.setColor(ghost ? new Color(0.8f, 0.8f, 0.8f, 0.4f) : Color.WHITE);
        rect(sr, x + 2, y + 2, xLen - 4, yLen - 4);
    }

    public void rect(ShapeRenderer sr, float x, float y, float width, float height) {
        sr.rect(x, y, width, height);
    }
}

