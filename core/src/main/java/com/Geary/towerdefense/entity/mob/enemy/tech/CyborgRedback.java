package com.Geary.towerdefense.entity.mob.enemy.tech;

import com.Geary.towerdefense.entity.mob.MobStats;
import com.Geary.towerdefense.entity.mob.enemy.Enemy;
import com.badlogic.gdx.graphics.Color;


public class CyborgRedback extends Enemy {

    public static final MobStats STATS = new MobStats(
        "CyborgRedback",
        "Applies Venom",
        "CyborgRedback",
        0.15f, 15, 29, 0.40f, 18f, 0.58f, 0.18f,
        Color.DARK_GRAY,//GREY/GREEN/BROWN?
        2,
        8
    );

    public CyborgRedback(float x, float y) {
        super(x, y, STATS);
    }
}
