package com.Geary.towerdefense.entity.mob.friendly;

import com.Geary.towerdefense.entity.mob.MobStats;
import com.badlogic.gdx.graphics.Color;


public class ManAtArms extends Friendly {

    public static final MobStats STATS = new MobStats(
        "Man At Arms",
        "",
        "This man has been given access to arms, and he is At Them.",
        0.13f, 220, 5, 0.69f, 12f, 0.2f,
        Color.OLIVE,
        2, // spawnTime
        0  // armour
    );

    public ManAtArms(float x, float y) {
        super(x, y, STATS);
        // No more manual assignments here!
    }
}
