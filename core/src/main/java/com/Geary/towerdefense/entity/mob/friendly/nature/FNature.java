package com.Geary.towerdefense.entity.mob.friendly.nature;

import com.Geary.towerdefense.entity.mob.MobStats;
import com.Geary.towerdefense.entity.mob.friendly.Friendly;

public abstract class FNature extends Friendly {
    public int spawnTime;

    public FNature(float startX, float startY, MobStats stats) {
        super(startX, startY, stats, Order.NATURE);
        this.turnMultiplier = -1; // opposite direction
        this.reversed = true;     // path index reversed
    }
}
