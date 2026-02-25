package com.Geary.towerdefense.entity.mob.enemy.tech;

import com.Geary.towerdefense.entity.mob.MobStats;
import com.Geary.towerdefense.entity.mob.enemy.Enemy;
import com.badlogic.gdx.graphics.Color;


public class OctoBit extends Enemy {

    public static final MobStats STATS = new MobStats(
        "OctoBit",
        "",
        "OctoBit",
        0.04f, 4, 2, 0.6f, 2f, 0.1f, 0.7f,
        Color.GRAY,
        2,
        0
    );

    public OctoBit(float x, float y) {
        super(x, y, STATS);
    }
}
