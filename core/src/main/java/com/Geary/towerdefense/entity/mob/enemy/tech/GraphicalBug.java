package com.Geary.towerdefense.entity.mob.enemy.tech;

import com.Geary.towerdefense.entity.mob.MobStats;
import com.Geary.towerdefense.entity.mob.enemy.Enemy;
import com.badlogic.gdx.graphics.Color;


public class GraphicalBug extends Enemy {

    public static final MobStats STATS = new MobStats(
        "Graphical Bug",
        "",
        "Graphical Bug",
        0.02f, 2, 1, 0.33f, 8f, 0.12f, 0.5f,
        Color.BLACK,
        2,
        0
    );

    public GraphicalBug(float x, float y) {
        super(x, y, STATS);
    }
}
