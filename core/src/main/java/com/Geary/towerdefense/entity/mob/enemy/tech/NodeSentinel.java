package com.Geary.towerdefense.entity.mob.enemy.tech;

import com.Geary.towerdefense.entity.mob.MobStats;
import com.Geary.towerdefense.entity.mob.enemy.Enemy;
import com.badlogic.gdx.graphics.Color;


public class NodeSentinel extends Enemy {

    public static final MobStats STATS = new MobStats(
        "NodeSentinel",
        "",
        "NodeSentinel",
        0.22f, 52, 10, 0.25f, 50f, 1.8f, 0.1f,
        Color.DARK_GRAY,
        2,
        6
    );

    public NodeSentinel(float x, float y) {
        super(x, y, STATS);
    }
}
