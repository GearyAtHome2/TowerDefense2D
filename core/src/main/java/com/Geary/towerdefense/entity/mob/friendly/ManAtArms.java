package com.Geary.towerdefense.entity.mob.friendly;

import com.Geary.towerdefense.entity.mob.MobStats;
import com.badlogic.gdx.graphics.Color;


public class ManAtArms extends Friendly {

    public static final MobStats STATS = new MobStats(
        0.13f,
        220,   // health
        5,   // damage
        0.69f, // speed
        12f,    // knockback damping
        0.2f, //random movement
        Color.GREEN
    );

    public ManAtArms(float x, float y) {
        super(x, y, STATS);
        this.name = "Man At Arms";
        this.armour = 4;
        this.effectText = "";
        this.flavourText = "This man has been given access to arms, and he is At Them.";
    }
}
