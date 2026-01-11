package com.Geary.towerdefense.entity.spawner;

import com.Geary.towerdefense.entity.buildings.Building;
import com.Geary.towerdefense.world.GameWorld;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.ArrayList;
import java.util.List;

public abstract class Spawner extends Building {
    // Visual inset from tile edges
    protected static final float BUFFER = 8f;

    protected Spawner(float x, float y) {
        super(x, y);
        this.isConnectedToNetwork = true;
        this.collisionRadius = (GameWorld.cellSize - (2 * BUFFER)) / 2;
        this.health = 8000;
        this.isDeletable = false;
    }

    public void draw(ShapeRenderer sr) {
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(getColor());

        float size = collisionRadius * 2;
        sr.rect(
            xPos + BUFFER,
            yPos + BUFFER,
            size,
            size
        );
        sr.end();
    }

    /**
     * Override for visual distinction
     */
    protected abstract Color getColor();

    /**
     * World-space center of this tile
     */
    protected float getCenterX() {
        return xPos + GameWorld.cellSize / 2f;
    }

    protected float getCenterY() {
        return yPos + GameWorld.cellSize / 2f;
    }

    @Override
    public List<String> getInfoLines() {
        List<String> lines = new ArrayList<>();
        lines.add(this.name);
        lines.add("Health:" + this.health);
        return lines;
    }
}
