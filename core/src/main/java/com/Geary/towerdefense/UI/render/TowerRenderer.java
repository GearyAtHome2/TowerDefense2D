package com.Geary.towerdefense.UI.render;

import com.Geary.towerdefense.entity.buildings.Tower;
import com.Geary.towerdefense.world.GameWorld;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;

public class TowerRenderer {

    private final GameWorld world;

    public static final float CELL_MARGIN = 8f;
    public static final float CELL_SIZE = GameWorld.cellSize - 2 * CELL_MARGIN;

    private final ShapeRenderer sr;

    public TowerRenderer(GameWorld world, ShapeRenderer sr) {
        this.world = world;
        this.sr = sr;
    }

    public void drawTowers() {
        sr.begin(ShapeRenderer.ShapeType.Filled);
        for (Tower t : world.towers) {
            drawTower(t, false); // real tower
        }

        if (world.ghostTower != null) {
            drawTower(world.ghostTower, true); // ghost tower
        }
        sr.end();
    }

    public void drawTowerRanges() {
        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.setColor(0f, 1f, 0f, 0.3f);
        for (Tower t : world.towers) {
            sr.circle(
                t.xPos + GameWorld.cellSize / 2f,
                t.yPos + GameWorld.cellSize / 2f,
                t.range
            );
        }
        if (world.ghostTower != null) {
            Tower ghost = world.ghostTower;
            sr.setColor(0.2f, 0.4f, 0.2f, 0.3f);
            sr.circle(
                ghost.xPos + GameWorld.cellSize / 2f,
                ghost.yPos + GameWorld.cellSize / 2f,
                ghost.range
            );
        }
        sr.end();
    }

    private void drawTower(Tower t, boolean ghost) {
        // Determine tower color
        if (ghost) {
            sr.setColor(new Color(0.2f, 0.4f, 0.2f, 0.4f));
        } else if (t.isConnectedToNetwork) {
            sr.setColor(Color.GREEN); // fully solid green
        } else {
            sr.setColor(new Color(0.1f, 0.5f, 0.1f, 0.7f));
        }

        float centerX = t.xPos + CELL_MARGIN + CELL_SIZE / 2;
        float centerY = t.yPos + CELL_MARGIN + CELL_SIZE / 2;
        sr.circle(centerX, centerY, CELL_SIZE / 2);
        sr.setColor(ghost ? new Color(0.3f, 0f, 0f, 0.4f) : Color.RED);
        float gunWidth = 4;
        float gunHeight = CELL_SIZE / 2;
        float angleDeg = (float) Math.toDegrees(t.gunAngle) - 90f;
        sr.rect(
            centerX - gunWidth / 2,
            centerY,
            gunWidth / 2,
            0,
            gunWidth,
            gunHeight,
            1f,
            1f,
            angleDeg
        );
    }

}

