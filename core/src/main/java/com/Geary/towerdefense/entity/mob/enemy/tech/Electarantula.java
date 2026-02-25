package com.Geary.towerdefense.entity.mob.enemy.tech;

import com.Geary.towerdefense.entity.mob.MobStats;
import com.Geary.towerdefense.entity.mob.enemy.Enemy;
import com.badlogic.gdx.graphics.Color;


public class Electarantula extends Enemy {

    public static final MobStats STATS = new MobStats(
        "Electarantula",
        "",
        "Electarantula",
        0.48f, 40, 25, 0.28f, 49f, 1.58f, 0.3f,
        Color.DARK_GRAY,//GREY/GREEN/BROWN?
        2,
        2
    );

    public Electarantula(float x, float y) {
        super(x, y, STATS);
    }
}
