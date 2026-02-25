package com.Geary.towerdefense.entity.mob.enemy.tech;

import com.Geary.towerdefense.entity.mob.MobStats;
import com.Geary.towerdefense.entity.mob.enemy.Enemy;
import com.badlogic.gdx.graphics.Color;


public class PlasmaKnight extends Enemy {

    public static final MobStats STATS = new MobStats(
        "Plasma Knight",
        "Applies Burning",
        "Plasma Knight",
        0.18f, 18, 20, 0.62f, 23f, 1.4f, 0.15f,
        Color.PINK,
        2,
        3
    );

    public PlasmaKnight(float x, float y) {
        super(x, y, STATS);
    }
}
