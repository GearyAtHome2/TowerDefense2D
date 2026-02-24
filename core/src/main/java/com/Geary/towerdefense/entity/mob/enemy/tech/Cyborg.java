package com.Geary.towerdefense.entity.mob.enemy.tech;

import com.Geary.towerdefense.entity.mob.MobStats;
import com.Geary.towerdefense.entity.mob.enemy.Enemy;
import com.badlogic.gdx.graphics.Color;


public class Cyborg extends Enemy {

    public static final MobStats STATS = new MobStats(
        "Cyborg",
        "",
        "cyborg",
        0.15f, 8, 5, 0.45f, 23f, 1.3f, 0.01f,
        Color.GRAY,
        2,
        0
    );

    public Cyborg(float x, float y) {
        super(x, y, STATS);
    }
}
