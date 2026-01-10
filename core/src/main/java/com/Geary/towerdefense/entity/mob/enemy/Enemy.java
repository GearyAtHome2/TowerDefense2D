package com.Geary.towerdefense.entity.mob.enemy;

import com.Geary.towerdefense.entity.mob.Mob;
import com.Geary.towerdefense.entity.mob.MobStats;

public abstract class Enemy extends Mob {

    public Enemy(float startX, float startY, MobStats stats) {
        super(startX, startY, stats);
        this.useCustomTurnLogic = true;
    }
}
