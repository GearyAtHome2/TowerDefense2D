package com.Geary.towerdefense.entity.mob.enemy;

import com.Geary.towerdefense.entity.mob.Mob;
import com.Geary.towerdefense.entity.mob.MobStats;

public abstract class Enemy extends Mob {

    public Enemy(float startX, float startY, MobStats stats) {
        super(startX, startY, stats, Order.NEUTRAL);
    }

    //this is just here because mob wants to copy friendly mobs for spawning purposes for now.
    @Override
    public Mob copy(){return this;}
}
