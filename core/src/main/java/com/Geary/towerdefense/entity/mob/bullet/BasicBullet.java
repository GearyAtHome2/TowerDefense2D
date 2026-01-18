package com.Geary.towerdefense.entity.mob.bullet;

import com.badlogic.gdx.graphics.Color;

import static com.badlogic.gdx.math.MathUtils.random;

public class BasicBullet extends Bullet {


    private static final float SIZE = 5.3f;
    private static final Color COLOR = Color.GRAY;

//    public BasicBullet() {
//        this.x = 0;
//        this.y = 0;
//        this.vx = 0;
//        this.vy = 0;
//        this.damage = 1;
//    }

    public BasicBullet(float maxLifeTime, float speed) {
        this.x = 0;
        this.y = 0;
        this.vx = 0;
        this.vy = 0;
        this.damage = 1;
        this.maxLifeTime = maxLifeTime;
        this.speed = speed;
    }

    public BasicBullet(float x, float y, float angle, float maxLifeTime, float speed) {
        this.x = x;
        this.y = y;
        this.vx = (float) Math.cos(angle) * speed;
        this.vy = (float) Math.sin(angle) * speed;
        this.damage = 1;
        this.lifetime = 0;
        float ranLifetimeMultiplier = 1 + (random() - 0.5f) * 0.1f;
        this.maxLifeTime = maxLifeTime * ranLifetimeMultiplier;
        float ranSpeedMultiplier = 1 + (random() - 0.5f) * 0.1f;
        this.speed = speed * ranSpeedMultiplier;
    }

    public void setBonusDamage(int bonusDamage) {
        this.damage += bonusDamage;
    }

    @Override
    public float getSpeed() {
        return speed;
    }

    @Override
    public float getSize() {
        return SIZE;
    }

    @Override
    public float getMaxLifetime() {
        return maxLifeTime;
    }

    @Override
    public Color getColor() {
        return COLOR;
    }

    @Override
    public Bullet createInstance(float x, float y, float angle) {
        return new BasicBullet(x, y, angle, this.maxLifeTime, this.speed);
    }
}
