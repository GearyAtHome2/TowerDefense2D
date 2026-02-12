package com.Geary.towerdefense.entity.mob.enemy;

import com.Geary.towerdefense.entity.mob.MobStats;
import com.badlogic.gdx.graphics.Color;


public class Groblin extends Enemy {

    public static final MobStats STATS = new MobStats(
        "Groblin",
        "",
        "Less fortunate cousin of the Goblin, more fortunate cousin of the Grob",
        0.1f, 5, 5, 0.65f, 28f, 0.8f, 0.25f,
        Color.FIREBRICK,
        2,
        0
    );

    public Groblin(float x, float y) {
        super(x, y, STATS);
    }
}
