package com.Geary.towerdefense.entity.mob.enemy.tech;

import com.Geary.towerdefense.entity.mob.MobStats;
import com.Geary.towerdefense.entity.mob.enemy.Enemy;
import com.badlogic.gdx.graphics.Color;


public class WireCrawler extends Enemy {

    public static final MobStats STATS = new MobStats(
        "Wire Crawler",
        "",
        "Wire Crawler",
        0.07f, 5, 4, 0.8f, 2f, 0.12f, 0.5f,
        Color.DARK_GRAY,//GREY/GREEN/BROWN?
        2,
        0
    );

    public WireCrawler(float x, float y) {
        super(x, y, STATS);
    }
}
