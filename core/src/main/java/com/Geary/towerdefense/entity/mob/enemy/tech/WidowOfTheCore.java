package com.Geary.towerdefense.entity.mob.enemy.tech;

import com.Geary.towerdefense.entity.mob.MobStats;
import com.Geary.towerdefense.entity.mob.enemy.Enemy;
import com.badlogic.gdx.graphics.Color;


public class WidowOfTheCore extends Enemy {

    public static final MobStats STATS = new MobStats(
        "The Widow Of The Core",
        "Applies Static, Applies Venom, Armour penetration",
        "Grace Hopper never actaully did manage to get rid of the first ever bug",
        0.85f, 305, 45, 0.15f, 240f, 6.38f, 0.18f,
        Color.DARK_GRAY,//GREY/GREEN/BROWN?
        2,
        8
    );

    public WidowOfTheCore(float x, float y) {
        super(x, y, STATS);
    }
}
