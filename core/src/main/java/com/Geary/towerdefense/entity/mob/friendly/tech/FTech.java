package com.Geary.towerdefense.entity.mob.friendly.tech;

import com.Geary.towerdefense.entity.mob.MobStats;
import com.Geary.towerdefense.entity.mob.friendly.Friendly;

public abstract class FTech extends Friendly {
    public int spawnTime;

    public FTech(float startX, float startY, MobStats stats) {
        super(startX, startY, stats, Order.TECH);
        this.turnMultiplier = -1; // opposite direction
        this.reversed = true;     // path index reversed
    }
}
