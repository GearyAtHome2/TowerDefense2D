package com.Geary.towerdefense.entity.mob.friendly.tech;

import com.Geary.towerdefense.entity.mob.MobStats;
import com.Geary.towerdefense.entity.mob.friendly.Friendly;

public abstract class FTech extends Friendly {
    private final Class<? extends FTech> clazz;  // store concrete class

    public FTech(float startX, float startY, MobStats stats, Class<? extends FTech> clazz) {
        super(startX, startY, stats, Order.TECH);
        this.clazz = clazz;
    }

    @Override
    public FTech copy() {
        try {
            return clazz.getConstructor(float.class, float.class)
                .newInstance(this.xPos, this.yPos);
        } catch (Exception e) {
            throw new RuntimeException("Failed to copy FNeutral mob: " + clazz.getSimpleName(), e);
        }
    }
}
