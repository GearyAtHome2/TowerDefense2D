package com.Geary.towerdefense.entity.mob.enemy.tech;

import com.Geary.towerdefense.entity.mob.MobStats;
import com.Geary.towerdefense.entity.mob.enemy.Enemy;
import com.badlogic.gdx.graphics.Color;


public class ConduitScrambler extends Enemy {

    public static final MobStats STATS = new MobStats(
        "Cable Creeper",
        "",
        "Cable Creeper",
        0.15f, 10, 8, 0.6f, 32f, 0.53f, 0.3f,
        Color.DARK_GRAY,//GREY/GREEN/BROWN?
        2,
        0
    );

    public ConduitScrambler(float x, float y) {
        super(x, y, STATS);
    }
}
