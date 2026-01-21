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
        float buffer = (cellSize - size) / 2f;

        this.xPos = tileX + buffer;
        this.yPos = tileY + buffer;
    }

    public void setPosition(float x, float y) {
        float cellSize = GameWorld.cellSize;

        this.collisionRadius = this.size / 2;
        float buffer = (cellSize - size) / 2f;

        this.xPos = x + buffer;
        this.yPos = y + buffer;
    }

    public float getCentreX() {
        return this.xPos + size/2;
    }
    public float getCentreY() {
        return this.yPos + size/2;
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
