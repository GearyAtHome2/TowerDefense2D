package com.Geary.towerdefense.entity.mob.friendly;

import com.Geary.towerdefense.entity.mob.MobStats;
import com.badlogic.gdx.graphics.Color;


public class Serf extends Friendly {

    public static final MobStats STATS = new MobStats(
        0.1f,
        220,   // health
        5,   // damage
        0.71f, // speed
        8f,    // knockback damping
        0.3f,
        Color.GREEN
    );

    public Serf(float x, float y) {
        super(x, y, STATS);
        this.name = "Serf";
        this.effectText = "Serf Effect text. Lorem ipsum sit dolor amet";
        this.flavourText = "Expendable, dependable, and commendable. We love serfs.";
        this.spawnTime = 2;
    }
}
