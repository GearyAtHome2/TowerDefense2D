package com.Geary.towerdefense.entity.mob.friendly.light;

import com.Geary.towerdefense.entity.mob.MobStats;
import com.Geary.towerdefense.entity.mob.friendly.Friendly;

public abstract class FLight extends Friendly {
    public int spawnTime;

    public FLight(float startX, float startY, MobStats stats) {
        super(startX, startY, stats, Order.LIGHT);
        this.turnMultiplier = -1; // opposite direction
        this.reversed = true;     // path index reversed
    }
}
