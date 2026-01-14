package com.Geary.towerdefense.entity.mob.friendly.nature;

import com.Geary.towerdefense.entity.mob.MobStats;
import com.badlogic.gdx.graphics.Color;


public class Wolf extends FNature {

    public static final MobStats STATS = new MobStats(
        "Wolf",
        "",
        "Big and bad, but also a good boy.",
        0.12f, 220, 5, 0.73f, 7f, 0.26f,
        Color.FOREST,
        2, // spawnTime
        0  // armour
    );

    public Wolf(float x, float y) {
        super(x, y, STATS, Wolf.class);
        // No more manual assignments here!
    }
}
