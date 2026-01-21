package com.Geary.towerdefense.entity.mob.friendly.light;

import com.Geary.towerdefense.entity.mob.MobStats;
import com.badlogic.gdx.graphics.Color;


public class ServantOfLight extends FLight {

    public static final MobStats STATS = new MobStats(
        "Servant of Light",
        "",
        "Choir boy",
        0.09f, 220, 5, 0.67f, 41f, 0.9f, 0.22f,
        Color.FOREST,
        2, // spawnTime
        0  // armour
    );

    public ServantOfLight(float x, float y) {
        super(x, y, STATS, ServantOfLight.class);
        // No more manual assignments here!
    }
}

