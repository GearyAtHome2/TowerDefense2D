package com.Geary.towerdefense.entity.buildings;

import com.badlogic.gdx.graphics.Color;

import java.util.List;

public class Building {
    public float xPos, yPos;
    public boolean isConnectedToNetwork = false;
    public float animationState = 0;

    public Building(float x, float y) {
        this.xPos = x;
        this.yPos = y;
    }

    public boolean contains(float x, float y, float cellSize) {
        return x >= xPos && x <= xPos + cellSize && y >= yPos && y <= yPos + cellSize;
    }

    public List<String> getInfoLines() {
        return List.of(); // default: empty
    }

    public Color getInfoTextColor() {
        return Color.WHITE; // default
    }
}
