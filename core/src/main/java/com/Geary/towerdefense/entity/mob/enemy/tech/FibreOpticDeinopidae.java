package com.Geary.towerdefense.entity.mob.enemy.tech;

import com.Geary.towerdefense.entity.mob.MobStats;
import com.Geary.towerdefense.entity.mob.enemy.Enemy;
import com.badlogic.gdx.graphics.Color;


public class FibreOpticDeinopidae extends Enemy {

    public static final MobStats STATS = new MobStats(
        "Fibre Optic Deinopidae",
        "Rush",//this should mean that after collision, speed increases dramatically for a couple of seconds?
        "Fibre Optic Deinopidae",
        0.19f, 16, 5, 0.2f, 4f, 0.58f, 0.1f,
        Color.DARK_GRAY,//GREY/GREEN/BROWN?
        2,
        0
    );

    public FibreOpticDeinopidae(float x, float y) {
        super(x, y, STATS);
    }
}
