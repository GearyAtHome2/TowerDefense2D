package com.Geary.towerdefense.entity.spawner;

import com.Geary.towerdefense.entity.buildings.Building;
import com.Geary.towerdefense.world.GameWorld;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public abstract class Spawner extends Building {
    // Visual inset from tile edges
    protected static final float BUFFER = 8f;

    protected Spawner(float x, float y) {
        super(x,y);
        this.isConnectedToNetwork = true;
    }

    public void draw(ShapeRenderer sr) {
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(getColor());

        float size = GameWorld.cellSize - (2 * BUFFER);
        sr.rect(
            xPos + BUFFER,
            yPos + BUFFER,
            size,
            size
        );
        sr.end();
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
