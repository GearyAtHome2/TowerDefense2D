package com.Geary.towerdefense.entity.mob.friendly;

import com.Geary.towerdefense.entity.mob.MobStats;
import com.badlogic.gdx.graphics.Texture;

public class Serf extends Friendly {

    private static final MobStats STATS = new MobStats(
        "Serf",
        0.1f,
        220,   // health
        5,   // damage
        0.71f, // speed
        8f,    // knockback damping
        0.3f
    );

    public Serf(float x, float y) {
        super(x, y, new Texture("friendly.png"), STATS);
    }
}
