package com.Geary.towerdefense.entity.mob.bullet;

import com.badlogic.gdx.graphics.Color;

public class BasicBullet extends Bullet {

    private static final float SPEED = 350f;
    private static final float SIZE = 5.3f;
    private static final float MAX_LIFETIME = 0.8f;
    private static final Color COLOR = Color.GRAY;

    public BasicBullet(float x, float y, float angle, int damage) {
        this.x = x;
        this.y = y;
        this.vx = (float)Math.cos(angle) * SPEED;
        this.vy = (float)Math.sin(angle) * SPEED;
        this.damage = damage;
    }

    @Override
    public float getSpeed() { return SPEED; }

    @Override
    public float getSize() { return SIZE; }

    @Override
    public float getMaxLifetime() { return MAX_LIFETIME; }

    @Override
    public Color getColor() { return COLOR; }

    @Override
    public Bullet createInstance(float x, float y, float angle, int damage) {
        return new BasicBullet(x, y, angle, damage);
    }
}
