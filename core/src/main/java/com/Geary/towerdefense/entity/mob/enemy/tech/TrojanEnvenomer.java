package com.Geary.towerdefense.entity.mob.enemy.tech;

import com.Geary.towerdefense.entity.mob.MobStats;
import com.Geary.towerdefense.entity.mob.enemy.Enemy;
import com.badlogic.gdx.graphics.Color;


public class TrojanEnvenomer extends Enemy {

    public static final MobStats STATS = new MobStats(
        "Trojan Envenomer",
        "Applies Venom, Armour penetration",
        "Flavour text",
        0.44f, 36, 20, 0.38f, 22f, 1.38f, 0.28f,
        Color.DARK_GRAY,//GREY/GREEN/BROWN?
        2,
        1
    );

    public TrojanEnvenomer(float x, float y) {
        super(x, y, STATS);
    }
}
