package com.Geary.towerdefense.entity.mob.friendly;

import com.Geary.towerdefense.entity.mob.Mob;
import com.Geary.towerdefense.entity.mob.MobStats;

public abstract class Friendly extends Mob {
    public int spawnTime;

    public Friendly(float startX, float startY, MobStats stats, Order order) {
        super(startX, startY, stats, order);
        this.turnMultiplier = -1; // opposite direction
        this.reversed = true;     // path index reversed
    }
}
