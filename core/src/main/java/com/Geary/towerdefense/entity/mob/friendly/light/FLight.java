package com.Geary.towerdefense.entity.mob.friendly.light;

import com.Geary.towerdefense.entity.mob.MobStats;
import com.Geary.towerdefense.entity.mob.friendly.Friendly;

public abstract class FLight extends Friendly {
    private final Class<? extends FLight> clazz;  // store concrete class

    public FLight(float startX, float startY, MobStats stats, Class<? extends FLight> clazz) {
        super(startX, startY, stats, Order.LIGHT);
        this.clazz = clazz;
    }

    public FLight copy() {
        try {
            // Assumes each subclass has a constructor (float x, float y)
            return clazz.getConstructor(float.class, float.class)
                .newInstance(this.xPos, this.yPos);
        } catch (Exception e) {
            throw new RuntimeException("Failed to copy FNeutral mob: " + clazz.getSimpleName(), e);
        }
    }
}
