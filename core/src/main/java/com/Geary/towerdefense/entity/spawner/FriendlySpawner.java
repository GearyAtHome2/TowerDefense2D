package com.Geary.towerdefense.entity.spawner;

import com.Geary.towerdefense.entity.mob.Friendly;
import com.badlogic.gdx.graphics.Color;

public class FriendlySpawner extends Spawner {

    //to be removed
    public float maxCooldown = 3f;
    public float cooldown = maxCooldown;

    public FriendlySpawner(float x, float y) {
        super(x, y);
    }

    @Override
    protected Color getColor() {
        return Color.GREEN;
    }

    public Friendly spawn() {
        return new Friendly(
            getCenterX() - 7,
            getCenterY() - 7
        );
    }
}
