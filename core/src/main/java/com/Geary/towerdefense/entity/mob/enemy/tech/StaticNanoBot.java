package com.Geary.towerdefense.entity.mob.enemy.tech;

import com.Geary.towerdefense.entity.mob.MobStats;
import com.Geary.towerdefense.entity.mob.enemy.Enemy;
import com.badlogic.gdx.graphics.Color;


public class StaticNanoBot extends Enemy {

    public static final MobStats STATS = new MobStats(
        "Static NanoBot",
        "Applies Static",
        "Static NanoBot",
        0.03f, 3, 1, 0.6f, 2f, 0.1f, 0.7f,
        Color.CYAN,
        2,
        0
    );

    public StaticNanoBot(float x, float y) {
        super(x, y, STATS);
    }
}
