package com.Geary.towerdefense.entity.mob.enemy.tech;

import com.Geary.towerdefense.entity.mob.MobStats;
import com.Geary.towerdefense.entity.mob.enemy.Enemy;
import com.badlogic.gdx.graphics.Color;


public class CyberneticThrall extends Enemy {

    public static final MobStats STATS = new MobStats(
        "Cybernetic Thrall",
        "Something about this wasting away over time?",
        "Cybernetic Thrall",
        0.12f, 6, 4, 0.63f, 29f, 0.8f, 0.26f,
        Color.CYAN,
        2,
        0
    );

    public CyberneticThrall(float x, float y) {
        super(x, y, STATS);
    }
}
