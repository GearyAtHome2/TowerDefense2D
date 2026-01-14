package com.Geary.towerdefense.entity.mob.friendly.nature;

import com.Geary.towerdefense.entity.mob.MobStats;
import com.Geary.towerdefense.entity.mob.friendly.Friendly;

public abstract class FNature extends Friendly {
    private final Class<? extends FNature> clazz;  // store concrete class

    public FNature(float startX, float startY, MobStats stats, Class<? extends FNature> clazz) {
        super(startX, startY, stats, Order.NATURE);
        this.clazz = clazz;
    }

    public FNature copy() {
        try {
            // Assumes each subclass has a constructor (float x, float y)
            return clazz.getConstructor(float.class, float.class)
                .newInstance(this.xPos, this.yPos);
        } catch (Exception e) {
            throw new RuntimeException("Failed to copy FNeutral mob: " + clazz.getSimpleName(), e);
        }
    }
}
