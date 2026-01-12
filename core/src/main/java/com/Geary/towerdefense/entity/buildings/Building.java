package com.Geary.towerdefense.entity.buildings;

import com.Geary.towerdefense.entity.Entity;
import com.Geary.towerdefense.world.GameWorld;
import com.badlogic.gdx.graphics.Color;

import java.util.ArrayList;
import java.util.List;

public class Building extends Entity {
    public boolean isConnectedToNetwork = false;
    public float animationState = 0;
    public boolean isDeletable = true;
    public float size;

    public Building(float tileX, float tileY) {
        float cellSize = GameWorld.cellSize;
        this.size = cellSize * 0.8f;
        this.collisionRadius = this.size / 2;

        // place building in the center of the tile
        this.xPos = tileX + GameWorld.cellSize / 2f;
        this.yPos = tileY + GameWorld.cellSize / 2f;
    }

    public boolean contains(float x, float y, float cellSize) {
        return x >= xPos && x <= xPos + cellSize && y >= yPos && y <= yPos + cellSize;
    }

    public List<String> getInfoLines() {
        List<String> lines = new ArrayList<>();
        lines.add(this.name);
        return lines;
    }

    public Color getInfoTextColor() {
        return Color.WHITE; // default
    }
}
