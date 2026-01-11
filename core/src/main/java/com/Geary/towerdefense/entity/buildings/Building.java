package com.Geary.towerdefense.entity.buildings;

import com.Geary.towerdefense.entity.Entity;
import com.badlogic.gdx.graphics.Color;

import java.util.ArrayList;
import java.util.List;

public class Building extends Entity {
    public boolean isConnectedToNetwork = false;
    public float animationState = 0;
    public boolean isDeletable = true;

    public Building(float x, float y) {
        this.xPos = x;
        this.yPos = y;
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
