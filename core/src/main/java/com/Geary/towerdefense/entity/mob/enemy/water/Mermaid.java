package com.Geary.towerdefense.entity.mob.enemy.water;

import com.Geary.towerdefense.entity.mob.MobStats;
import com.Geary.towerdefense.entity.mob.enemy.Enemy;
import com.badlogic.gdx.graphics.Color;


public class Mermaid extends Enemy {

    public static final MobStats STATS = new MobStats(
        "Mermaid",
        "",
        "Mermaid",
        0.12f, 5, 5, 0.65f, 20f, 1.1f, 0.35f,
        Color.NAVY,
        2,
        0
    );

    public Mermaid(float x, float y) {
        super(x, y, STATS);
    }
}
