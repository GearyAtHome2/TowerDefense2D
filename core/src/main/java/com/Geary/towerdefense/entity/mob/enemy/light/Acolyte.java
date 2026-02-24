package com.Geary.towerdefense.entity.mob.enemy.light;

import com.Geary.towerdefense.entity.mob.MobStats;
import com.Geary.towerdefense.entity.mob.enemy.Enemy;
import com.badlogic.gdx.graphics.Color;


public class Acolyte extends Enemy {

    public static final MobStats STATS = new MobStats(
        "Acolyte",
        "",
        "Acolyte",
        0.2f, 6, 4, 0.5f, 18f, 0.5f, 0.22f,
        Color.FOREST,
        2,
        0
    );

    public Acolyte(float x, float y) {
        super(x, y, STATS);
    }
}
