package com.Geary.towerdefense.entity.mob.friendly.dark;

import com.Geary.towerdefense.entity.mob.MobStats;
import com.Geary.towerdefense.entity.mob.friendly.Friendly;

public abstract class FDark extends Friendly {
    public int spawnTime;

    public FDark(float startX, float startY, MobStats stats) {
        super(startX, startY, stats, Order.DARK);
        this.turnMultiplier = -1; // opposite direction
        this.reversed = true;     // path index reversed
    }
}
