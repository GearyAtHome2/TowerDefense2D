package com.Geary.towerdefense.entity.mob.friendly.nature;

import com.Geary.towerdefense.entity.mob.MobStats;
import com.badlogic.gdx.graphics.Color;


public class Elvis extends FNature {

    public static final MobStats STATS = new MobStats(
        "Elvis",
        "",
        "",
        0.8f, 2200, 5, 0.2f, 280f,9.0f,  0.1f,
        Color.FOREST,
        2, // spawnTime
        0  // armour
    );

    public Elvis(float x, float y) {
        super(x, y, STATS, Elvis.class);
    }
}
