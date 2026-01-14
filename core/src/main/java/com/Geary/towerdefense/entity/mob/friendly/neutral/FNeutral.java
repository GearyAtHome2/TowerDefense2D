package com.Geary.towerdefense.entity.mob.friendly.neutral;

import com.Geary.towerdefense.entity.mob.MobStats;
import com.Geary.towerdefense.entity.mob.friendly.Friendly;

public abstract class FNeutral extends Friendly {
    public int spawnTime;

    public FNeutral(float startX, float startY, MobStats stats) {
        super(startX, startY, stats, Order.NEUTRAL);
        this.turnMultiplier = -1; // opposite direction
        this.reversed = true;     // path index reversed
    }
}
