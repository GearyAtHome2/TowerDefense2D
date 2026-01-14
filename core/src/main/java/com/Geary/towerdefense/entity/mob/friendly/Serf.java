package com.Geary.towerdefense.entity.mob.friendly;

import com.Geary.towerdefense.entity.mob.MobStats;
import com.badlogic.gdx.graphics.Color;


public class Serf extends Friendly {

    public static final MobStats STATS = new MobStats(
        "Serf",
        "Serf Effect text...",
        "Expendable, dependable...",
        0.1f, 220, 5, 0.71f, 8f, 0.3f,
        Color.GREEN,
        2, // spawnTime
        0  // armour
    );

    public Serf(float x, float y) {
        super(x, y, STATS);
        // No more manual assignments here!
    }
}
