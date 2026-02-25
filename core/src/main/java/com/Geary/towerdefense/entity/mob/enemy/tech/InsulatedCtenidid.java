package com.Geary.towerdefense.entity.mob.enemy.tech;

import com.Geary.towerdefense.entity.mob.MobStats;
import com.Geary.towerdefense.entity.mob.enemy.Enemy;
import com.badlogic.gdx.graphics.Color;


public class InsulatedCtenidid extends Enemy {

    public static final MobStats STATS = new MobStats(
        "Insulated Ctenidid",
        "",
        "Insulated Ctenidid",
        0.35f, 25, 29, 0.35f, 38f, 1.38f, 0.28f,
        Color.DARK_GRAY,//GREY/GREEN/BROWN?
        2,
        8
    );

    public InsulatedCtenidid(float x, float y) {
        super(x, y, STATS);
    }
}
