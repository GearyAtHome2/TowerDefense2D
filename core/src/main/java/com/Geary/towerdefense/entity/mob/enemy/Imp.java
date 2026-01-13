package com.Geary.towerdefense.entity.mob.enemy;

import com.Geary.towerdefense.entity.mob.MobStats;
import com.badlogic.gdx.graphics.Color;


public class Imp extends Enemy {

    private static final MobStats STATS = new MobStats(
        0.1f,
        180,   // health
        5,   // damage
        0.8f, // speed
        6f,    // knockback damping
        0.45f,
        Color.FIREBRICK
    );

    public Imp(float x, float y) {
        super(x, y, STATS);
        this.name = "Imp";
        this.effectText = "Demonic";
        this.flavourText = "Lorem impsum dolor sit amet";
    }
}
