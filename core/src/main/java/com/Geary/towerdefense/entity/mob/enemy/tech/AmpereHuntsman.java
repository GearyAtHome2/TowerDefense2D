package com.Geary.towerdefense.entity.mob.enemy.tech;

import com.Geary.towerdefense.entity.mob.MobStats;
import com.Geary.towerdefense.entity.mob.enemy.Enemy;
import com.badlogic.gdx.graphics.Color;


public class AmpereHuntsman extends Enemy {

    public static final MobStats STATS = new MobStats(
        "Ampere Huntsman",
        "Armour penetration",
        "Ampere Huntsman",
        0.60f, 65, 8, 0.68f, 52f, 2.08f, 0.51f,
        Color.DARK_GRAY,//GREY/GREEN/BROWN?
        2,
        8
    );

    public AmpereHuntsman(float x, float y) {
        super(x, y, STATS);
    }
}
