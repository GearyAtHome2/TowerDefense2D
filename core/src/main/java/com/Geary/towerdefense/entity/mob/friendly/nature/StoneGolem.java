package com.Geary.towerdefense.entity.mob.friendly.nature;

import com.Geary.towerdefense.entity.mob.MobStats;
import com.badlogic.gdx.graphics.Color;


public class StoneGolem extends FNature {

    public static final MobStats STATS = new MobStats(
        "Stone Golem",
        "",
        "Rocks",
        0.2f, 220, 5, 0.43f, 55f,2.0f,  0.26f,
        Color.FOREST,
        2, // spawnTime
        0  // armour
    );

    public StoneGolem(float x, float y) {
        super(x, y, STATS, StoneGolem.class);
    }
}
