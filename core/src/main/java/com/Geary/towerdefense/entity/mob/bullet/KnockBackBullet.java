package com.Geary.towerdefense.entity.mob.bullet;

import com.Geary.towerdefense.entity.resources.Resource;
import com.badlogic.gdx.graphics.Color;

import java.util.EnumMap;

public class KnockBackBullet extends Bullet {

    //creates the bullet for modal purposes
    public KnockBackBullet() {
        super("Rock", 1, 350f, 0.4f, 10, 0, Color.GRAY);
        EnumMap<Resource.RawResourceType, Double> rawResourceCost =  new EnumMap<>(Resource.RawResourceType.class);
        EnumMap<Resource.RefinedResourceType, Double> refinedResourceCost =  new EnumMap<>(Resource.RefinedResourceType.class);
        rawResourceCost.put(Resource.RawResourceType.STONE, 1.0);
        setResourceCost(rawResourceCost, refinedResourceCost);
    }

    //bullet for game purposes.
    private KnockBackBullet(float x, float y, float angle,
                            float maxLifeTime, float speed) {
        super("Rock", 1, speed, maxLifeTime, 10, 8.1f, Color.GRAY);
        this.x = x;
        this.y = y;
        this.vx = (float) Math.cos(angle) * speed;
        this.vy = (float) Math.sin(angle) * speed;
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
        return new KnockBackBullet(x, y, angle, this.maxLifeTime, this.speed);
    }
}
