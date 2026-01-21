package com.Geary.towerdefense.entity.mob.friendly.dark;

import com.Geary.towerdefense.entity.mob.MobStats;
import com.badlogic.gdx.graphics.Color;


public class DodgyGangster extends FDark {

    public static final MobStats STATS = new MobStats(
        "Dodgy Gangster",
        "",
        "Good at bad things",
        0.118f, 220, 5, 48f, 9f, 1f, 0.28f,
        Color.FOREST,
        2, // spawnTime
        0  // armour
    );

    public DodgyGangster(float x, float y) {
        super(x, y, STATS, DodgyGangster.class);
        this.coinCost = 6;
    }
}
