package com.Geary.towerdefense.entity.mob.enemy;

import com.Geary.towerdefense.entity.mob.MobStats;
import com.badlogic.gdx.graphics.Color;


public class Groblin extends Enemy {

    private static final MobStats STATS = new MobStats(
        "Groblin",
        0.1f,
        180,   // health
        5,   // damage
        0.8f, // speed
        6f,    // knockback damping
        0.45f,
        Color.RED//random movement probability
    );

    public Groblin(float x, float y) {
        super(x, y, STATS);
    }
}
