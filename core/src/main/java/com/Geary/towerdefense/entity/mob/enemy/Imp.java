package com.Geary.towerdefense.entity.mob.enemy;

import com.Geary.towerdefense.entity.mob.MobStats;
import com.badlogic.gdx.graphics.Color;


public class Imp extends Enemy {


    public static final MobStats STATS = new MobStats(
        "Imp",
        "Demonic",
        "Lorem impsum dolor sit amet",
        0.08f, 220, 5, 0.73f, 18f, 0.3f, 0.35f,
        Color.FIREBRICK,
        2, // spawnTime
        0  // armour
    );

    public Imp(float x, float y) {
        super(x, y, STATS);
    }
}
