package com.Geary.towerdefense.entity.mob.enemy.tech;

import com.Geary.towerdefense.entity.mob.MobStats;
import com.Geary.towerdefense.entity.mob.enemy.Enemy;
import com.badlogic.gdx.graphics.Color;


public class GoliathStatorBreaker extends Enemy {

    public static final MobStats STATS = new MobStats(
        "Goliath StatorBreaker",
        "Applies Static, Armour penetration",
        "Goliath StatorBreaker",
        0.66f, 85, 20, 0.28f, 112f, 2.88f, 0.11f,
        Color.DARK_GRAY,//GREY/GREEN/BROWN?
        2,
        8
    );

    public GoliathStatorBreaker(float x, float y) {
        super(x, y, STATS);
    }
}
