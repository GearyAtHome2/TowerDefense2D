package com.Geary.towerdefense.entity.mob.enemy.tech;

import com.Geary.towerdefense.entity.mob.MobStats;
import com.Geary.towerdefense.entity.mob.enemy.Enemy;
import com.badlogic.gdx.graphics.Color;


public class CapacitorBeetle extends Enemy {

    public static final MobStats STATS = new MobStats(
        "CapacitorBeetle",
        "Sacrificial, Applies Static",
        "CapacitorBeetle",
        0.1f, 1, 50, 0.66f, 80f, 0.3f, 0.6f,
        Color.LIME,//GREY/GREEN/BROWN?
        2,
        0
    );

    public CapacitorBeetle(float x, float y) {
        super(x, y, STATS);
    }
}
