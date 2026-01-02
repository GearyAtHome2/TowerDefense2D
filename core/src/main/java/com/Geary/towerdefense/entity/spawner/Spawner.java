package com.Geary.towerdefense.entity.spawner;

import com.Geary.towerdefense.world.GameWorld;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public abstract class Spawner {

    protected float xPos, yPos;

    // Visual inset from tile edges
    protected static final float BUFFER = 8f;

    protected Spawner(float x, float y) {
        this.xPos = x;
        this.yPos = y;
    }

    public void draw(ShapeRenderer sr) {
        sr.setColor(getColor());

        float size = GameWorld.cellSize - (2 * BUFFER);
        sr.rect(
            xPos + BUFFER,
            yPos + BUFFER,
            size,
            size
        );
    }

    /** Override for visual distinction */
    protected abstract Color getColor();

    /** World-space center of this tile */
    protected float getCenterX() {
        return xPos + GameWorld.cellSize / 2f;
    }

    protected float getCenterY() {
        return yPos + GameWorld.cellSize / 2f;
    }
}
