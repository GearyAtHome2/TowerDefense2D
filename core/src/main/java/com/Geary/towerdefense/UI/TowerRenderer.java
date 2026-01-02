package com.Geary.towerdefense.UI;

import com.Geary.towerdefense.entity.Tower;
import com.Geary.towerdefense.world.GameWorld;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;

public class TowerRenderer {

    private final GameWorld world;

    // You can tweak these for the inset effect
    public static final float CELL_MARGIN = 8f; // pixels inset from cell edge
    public static final float CELL_SIZE = GameWorld.cellSize - 2 * CELL_MARGIN;
    public static final float CELL_MARGIN_OFFSET = CELL_MARGIN; // helper for tower center calculation

    public TowerRenderer(GameWorld world) {
        this.world = world;
    }

    public void drawTowers(ShapeRenderer sr) {
        for (Tower t : world.towers) {
            // --- Tower base (circle) ---
            sr.setColor(Color.GREEN);
            float centerX = t.xPos + CELL_MARGIN + CELL_SIZE / 2;
            float centerY = t.yPos + CELL_MARGIN + CELL_SIZE / 2;
            sr.circle(centerX, centerY, CELL_SIZE / 2);
            sr.setColor(Color.RED);
            float gunWidth = 4;
            float gunHeight = CELL_SIZE / 2;
            float angleDeg = (float) Math.toDegrees(t.gunAngle) - 90f;
            sr.rect(
                centerX - gunWidth / 2,  // x: bottom-left
                centerY,                 // y: bottom-left
                gunWidth / 2,            // originX: center of width
                0,                       // originY: bottom of rectangle
                gunWidth,
                gunHeight,
                1f,
                1f,
                angleDeg
            );
        }
    }

    public void drawTowerRanges(ShapeRenderer sr) {
        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.setColor(0f, 1f, 0f, 0.3f);

        for (Tower t : world.towers) {
            sr.circle(
                t.xPos + GameWorld.cellSize / 2f,
                t.yPos + GameWorld.cellSize / 2f,
                t.range
            );
        }

        sr.end();
    }
}
