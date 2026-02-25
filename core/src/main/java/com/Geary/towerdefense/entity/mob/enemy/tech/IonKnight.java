package com.Geary.towerdefense.entity.mob.enemy.tech;

import com.Geary.towerdefense.entity.mob.MobStats;
import com.Geary.towerdefense.entity.mob.enemy.Enemy;
import com.badlogic.gdx.graphics.Color;


public class IonKnight extends Enemy {

    public static final MobStats STATS = new MobStats(
        "Ion Knight",
        "Applies Static",
        "Ion Knight",
        0.17f, 17, 17, 0.61f, 23f, 1.2f, 0.18f,
        Color.BLUE,
        2,
        3
    );

    public IonKnight(float x, float y) {
        super(x, y, STATS);
    }
}
