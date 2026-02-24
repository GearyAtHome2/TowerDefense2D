package com.Geary.towerdefense.entity.mob.enemy.dark;

import com.Geary.towerdefense.entity.mob.MobStats;
import com.Geary.towerdefense.entity.mob.enemy.Enemy;
import com.badlogic.gdx.graphics.Color;


public class Ghoul extends Enemy {


    public static final MobStats STATS = new MobStats(
        "Ghoul",
        "Ghoul",
        "Ghoul Ghoul Ghoul",
        0.13f, 22, 5, 0.73f, 18f, 0.3f, 0.35f,
        Color.DARK_GRAY,
        2, // spawnTime
        0  // armour
    );

    public Ghoul(float x, float y) {
        super(x, y, STATS);
    }
}
