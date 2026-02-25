package com.Geary.towerdefense.entity.mob.enemy.tech;

import com.Geary.towerdefense.entity.mob.MobStats;
import com.Geary.towerdefense.entity.mob.enemy.Enemy;
import com.badlogic.gdx.graphics.Color;


public class FunnelWebNetSwarmer extends Enemy {

    public static final MobStats STATS = new MobStats(
        "Funnel WebNet Swarmer",
        "Applies Static, Applies Venom, Armour penetration",
        "Funnel WebNet Swarmer",
        0.35f, 25, 29, 0.35f, 38f, 1.38f, 0.28f,
        Color.DARK_GRAY,//GREY/GREEN/BROWN?
        2,
        8
    );

    public FunnelWebNetSwarmer(float x, float y) {
        super(x, y, STATS);
    }
}
