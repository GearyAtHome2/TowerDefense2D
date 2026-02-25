package com.Geary.towerdefense.entity.mob.enemy.tech;

import com.Geary.towerdefense.entity.mob.MobStats;
import com.Geary.towerdefense.entity.mob.enemy.Enemy;
import com.badlogic.gdx.graphics.Color;


public class NanoBot extends Enemy {

    public static final MobStats STATS = new MobStats(
        "NanoBot",
        "",
        "NanoBot",
        0.03f, 3, 1, 0.6f, 2f, 0.1f, 0.7f,
        Color.GRAY,
        2,
        0
    );

    public NanoBot(float x, float y) {
        super(x, y, STATS);
    }
}
