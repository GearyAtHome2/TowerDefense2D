package com.Geary.towerdefense.entity.mob.bullet;

import com.Geary.towerdefense.entity.resources.Resource;
import com.badlogic.gdx.graphics.Color;

import java.util.EnumMap;

public class BasicBullet extends Bullet {

    public BasicBullet() {
        super("Basic", 1, 360f, 0.65f, 0, 5.3f, Color.GRAY);
        EnumMap<Resource.RawResourceType, Double> rawResourceCost =  new EnumMap<>(Resource.RawResourceType.class);
        EnumMap<Resource.RefinedResourceType, Double> refinedResourceCost =  new EnumMap<>(Resource.RefinedResourceType.class);
        refinedResourceCost.put(Resource.RefinedResourceType.BASIC_AMMO, 1.0);
        setResourceCost(rawResourceCost, refinedResourceCost);
    }

    private BasicBullet(float x, float y, float angle,
                        float maxLifeTime, float speed, float size, Color color) {
        super("Basic", 1, speed, maxLifeTime, 0, size ,color);
        this.x = x;
        this.y = y;
        this.vx = (float) Math.cos(angle) * speed;
        this.vy = (float)
            Math.sin(angle) * speed;
        this.lifetime = 0;
    }

    private void setResourceCost(EnumMap<Resource.RawResourceType, Double> rawResourceCost,EnumMap<Resource.RefinedResourceType, Double> refinedResourceCost ){
        this.rawResourceCost = rawResourceCost;
        this.refinedResourceCost = refinedResourceCost;
    }

    public void setBonusDamage(int bonusDamage) {
        this.damage += bonusDamage;
    }

    @Override
    public Bullet createInstance(float x, float y, float angle) {
        return new BasicBullet(x, y, angle, this.maxLifeTime, this.speed, this.getSize(), this.getColor());
    }
}
