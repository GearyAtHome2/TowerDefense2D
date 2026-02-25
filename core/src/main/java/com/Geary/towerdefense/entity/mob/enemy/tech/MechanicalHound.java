package com.Geary.towerdefense.entity.mob.enemy.tech;

import com.Geary.towerdefense.entity.mob.MobStats;
import com.Geary.towerdefense.entity.mob.enemy.Enemy;
import com.badlogic.gdx.graphics.Color;


public class MechanicalHound extends Enemy {

    public static final MobStats STATS = new MobStats(
        "MechanicalHound",
        "Applies Venom, Armour penetration",
        "MechanicalHound",
        0.18f, 12, 11, 0.25f, 20f, 1.1f, 0.2f,
        Color.GRAY,
        2,
        1
    );

    public MechanicalHound(float x, float y) {
        super(x, y, STATS);
    }
}
