package com.Geary.towerdefense.entity.spawner;

import com.Geary.towerdefense.entity.mob.friendly.Friendly;
import com.Geary.towerdefense.entity.mob.friendly.Serf;
import com.badlogic.gdx.graphics.Color;

public class FriendlySpawner extends Spawner {
    //to be removed
    public float maxCooldown = 3f;
    public float cooldown = maxCooldown;

    public FriendlySpawner(float x, float y) {
        super(x, y);
        isConnectedToNetwork = true;
    }

    @Override
    protected Color getColor() {
        return Color.GREEN;
    }

    public Friendly spawn() {
        return new Serf(
            getCenterX() - 7,
            getCenterY() - 7
        );
    }
}
