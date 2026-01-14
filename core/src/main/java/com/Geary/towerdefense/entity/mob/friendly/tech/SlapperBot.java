package com.Geary.towerdefense.entity.mob.friendly.tech;

import com.Geary.towerdefense.entity.mob.MobStats;
import com.badlogic.gdx.graphics.Color;


public class SlapperBot extends FTech {

    public static final MobStats STATS = new MobStats(
        "Slapper Bot",
        "Serf Effect text...",
        "Expendable, dependable...",
        0.14f, 220, 2, 0.71f, 18f, 0.9f,
        Color.LIME,
        2, // spawnTime
        0  // armour
    );

    public SlapperBot(float x, float y) {
        super(x, y, STATS, SlapperBot.class);
    }
}
