package com.Geary.towerdefense.entity.mob.friendly.dark;

import com.Geary.towerdefense.entity.mob.MobStats;
import com.Geary.towerdefense.entity.mob.friendly.Friendly;

public abstract class FDark extends Friendly {
    private final Class<? extends FDark> clazz;  // store concrete class

    public FDark(float startX, float startY, MobStats stats, Class<? extends FDark> clazz) {
        super(startX, startY, stats, Order.NEUTRAL);
        this.clazz = clazz;
    }

    @Override
    public FDark copy() {
        try {
            // Assumes each subclass has a constructor (float x, float y)
            return clazz.getConstructor(float.class, float.class)
                .newInstance(this.xPos, this.yPos);
        } catch (Exception e) {
            throw new RuntimeException("Failed to copy FNeutral mob: " + clazz.getSimpleName(), e);
        }
    }
}
