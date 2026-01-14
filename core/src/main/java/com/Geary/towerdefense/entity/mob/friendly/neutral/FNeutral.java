package com.Geary.towerdefense.entity.mob.friendly.neutral;

import com.Geary.towerdefense.entity.mob.MobStats;
import com.Geary.towerdefense.entity.mob.friendly.Friendly;

public abstract class FNeutral extends Friendly {
    private final Class<? extends FNeutral> clazz;  // store concrete class

    public FNeutral(float startX, float startY, MobStats stats, Class<? extends FNeutral> clazz) {
        super(startX, startY, stats, Order.NEUTRAL);
        this.clazz = clazz;
    }

    /** Generic copy method */
    @Override
    public FNeutral copy() {
        try {
            // Assumes each subclass has a constructor (float x, float y)
            return clazz.getConstructor(float.class, float.class)
                .newInstance(this.xPos, this.yPos);
        } catch (Exception e) {
            throw new RuntimeException("Failed to copy FNeutral mob: " + clazz.getSimpleName(), e);
        }
    }
}
