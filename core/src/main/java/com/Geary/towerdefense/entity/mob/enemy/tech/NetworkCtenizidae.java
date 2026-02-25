package com.Geary.towerdefense.entity.mob.enemy.tech;

import com.Geary.towerdefense.entity.mob.MobStats;
import com.Geary.towerdefense.entity.mob.enemy.Enemy;
import com.badlogic.gdx.graphics.Color;


public class NetworkCtenizidae extends Enemy {

    public static final MobStats STATS = new MobStats(
        "Network Ctenizidae",
        "",
        "Network Ctenizidae",
        0.15f, 10, 8, 0.6f, 32f, 0.53f, 0.3f,
        Color.DARK_GRAY,//GREY/GREEN/BROWN?
        2,
        0
    );

    public NetworkCtenizidae(float x, float y) {
        super(x, y, STATS);
    }
}
