package com.Geary.towerdefense.entity.mob.enemy.nature;

import com.Geary.towerdefense.entity.mob.MobStats;
import com.Geary.towerdefense.entity.mob.enemy.Enemy;
import com.badlogic.gdx.graphics.Color;


public class Spider extends Enemy {

    public static final MobStats STATS = new MobStats(
        "SPIDER",
        "",
        "SPIDER",
        0.15f, 3, 3, 0.7f, 5f, 0.3f, 0.30f,
        Color.FOREST,
        2,
        0
    );

    public Spider(float x, float y) {
        super(x, y, STATS);
    }
}
