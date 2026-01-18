package com.Geary.towerdefense.entity;

import com.Geary.towerdefense.entity.mob.Mob;
import com.badlogic.gdx.graphics.Color;

import java.util.List;

public class Entity {
    public float xPos, yPos;
    public float collisionRadius;
    public String name;
    public int health;
    public Mob.Order order = Mob.Order.NEUTRAL;

    public List<String> getInfoLines() {
        return List.of(); // default: empty
    }

    public Color getInfoTextColor() {
        return Color.WHITE; // default
    }

    public void applyDamage(int amount) {
        health -= amount;
    }

    public enum Order {
        NEUTRAL,
        TECH,
        NATURE,
        DARK,
        LIGHT,
        FIRE,
        WATER
    }
}
