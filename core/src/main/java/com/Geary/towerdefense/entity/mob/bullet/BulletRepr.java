package com.Geary.towerdefense.entity.mob.bullet;

import com.Geary.towerdefense.entity.resources.Resource;
import com.badlogic.gdx.graphics.Color;

import java.util.EnumMap;

public class BulletRepr<T extends Bullet> {
    public final T template;

    public BulletRepr(T template) {
        this.template = template;
    }

    /** Access the bullet’s stats */
    public String getName() { return template.name; }
    public int getDamage() { return template.damage; }
    public float getSpeed() { return template.getSpeed(); }
    public float getSize() { return template.getSize(); }
    public float getMaxLifetime() { return template.getMaxLifetime(); }
    public float getKnockback() { return template.getKnockBack(); }
    public Color getColor() { return template.getColor(); }

    /** Create a runtime instance for shooting */
    public T createInstance(float x, float y, float angle) {
        return (T) template.createInstance(x, y, angle);
    }

    /** Access costs if needed */
    public EnumMap<Resource.RawResourceType, Double> getRawCosts() {
        return template.rawResourceCost;
    }

    public EnumMap<Resource.RefinedResourceType, Double> getRefinedCosts() {
        return template.refinedResourceCost;
    }
}
