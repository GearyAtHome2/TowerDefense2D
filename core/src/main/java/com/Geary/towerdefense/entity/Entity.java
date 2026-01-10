package com.Geary.towerdefense.entity;

import com.badlogic.gdx.graphics.Color;

import java.util.List;

public class Entity {
    public float xPos, yPos;
    public String name;

    public List<String> getInfoLines() {
        return List.of(); // default: empty
    }

    public Color getInfoTextColor() {
        return Color.WHITE; // default
    }
}
