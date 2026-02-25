package com.Geary.towerdefense.entity.mob.enemy.tech;

import com.Geary.towerdefense.entity.mob.MobStats;
import com.Geary.towerdefense.entity.mob.enemy.Enemy;
import com.badlogic.gdx.graphics.Color;


public class CableCreeper extends Enemy {

    public static final MobStats STATS = new MobStats(
        "Cable Creeper",
        "",
        "Cable Creeper",
        0.11f, 7, 5, 0.7f, 21f, 0.33f, 0.4f,
        Color.DARK_GRAY,//GREY/GREEN/BROWN?
        2,
        0
    );

    public CableCreeper(float x, float y) {
        super(x, y, STATS);
    }
}
