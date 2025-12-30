package com.Geary.towerdefense.UI;

import com.Geary.towerdefense.entity.Tower;
import com.Geary.towerdefense.world.GameWorld;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class TowerRenderer {
    private final GameWorld world;

    public TowerRenderer(GameWorld world) {
        this.world = world;
    }

    /**
     * Draws tower radii (range circles).
     * Cooldown and shooting logic is NOT handled here.
     */
    public void drawTowerRanges(ShapeRenderer shapeRenderer) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0, 0, 1, 0.4f); // blue translucent

        for (Tower tower : world.towers) {
            float cx = tower.xPos + world.cellSize / 2f;
            float cy = tower.yPos + world.cellSize / 2f;
            shapeRenderer.circle(cx, cy, tower.range);
        }

        shapeRenderer.end();
    }
}
