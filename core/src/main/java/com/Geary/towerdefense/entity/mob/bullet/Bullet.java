package com.Geary.towerdefense.entity.mob.bullet;

import com.Geary.towerdefense.entity.mob.enemy.Enemy;
import com.Geary.towerdefense.entity.resources.Resource;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.EnumMap;
import java.util.List;

import static com.badlogic.gdx.math.MathUtils.random;

public abstract class Bullet {

    public float x;
    public float y;
    public float vx;
    public float vy;

    public int damage;
    protected float knockback;

    protected float speed;

    public float lifetime;
    public float maxLifeTime;

    public String name;
    protected float size;
    protected Color color;

    EnumMap<Resource.RawResourceType, Double> rawResourceCost;
    EnumMap<Resource.RefinedResourceType, Double> refinedResourceCost;

    protected Bullet(String name, int damage, float speed, float maxLifeTime, float knockback, float size, Color color) {
        this.name = name;
        this.damage = damage;
        this.speed = speed * (1f + random() * 0.1f);
        this.maxLifeTime = maxLifeTime * (1f + random() * 0.1f);
        this.knockback = knockback;
        this.size = size;
        this.color = color;
    }

    public float getSpeed() {
        return speed;
    }

    public float getKnockBack() {
        return knockback;
    }

    protected float getSize() {
        return size;
    }

    protected float getMaxLifetime() {
        return maxLifeTime;
    }

    protected Color getColor() {
        return color;
    }

    public float getCenterX() {
        return x + getSize() / 2f;
    }

    public float getCenterY() {
        return y + getSize() / 2f;
    }

    public abstract Bullet createInstance(float x, float y, float angle);

    public boolean update(float delta, List<Enemy> enemies) {
        lifetime += delta;
        if (lifetime > getMaxLifetime()) return false;

        float startX = x;
        float startY = y;

        x += vx * delta;
        y += vy * delta;

        Enemy firstHit = null;
        float hitX = x;
        float hitY = y;
        float closestDistSq = Float.MAX_VALUE;

        for (Enemy e : enemies) {
            if (e.health <= 0) continue;

            float left = e.xPos;
            float bottom = e.yPos;
            float right = left + e.size;
            float top = bottom + e.size;

            if (!lineIntersectsRect(startX, startY, x, y, left, bottom, right, top)) {
                continue;
            }

            float centerX = (left + right) * 0.5f;
            float centerY = (bottom + top) * 0.5f;

            float dx = x - startX;
            float dy = y - startY;
            float t = projectPoint(startX, startY, dx, dy, centerX, centerY);

            float px = startX + t * dx;
            float py = startY + t * dy;

            float distSq = (px - startX) * (px - startX)
                + (py - startY) * (py - startY);

            if (distSq < closestDistSq) {
                closestDistSq = distSq;
                firstHit = e;
                hitX = px;
                hitY = py;
            }
        }

        if (firstHit != null) {
            firstHit.health -= damage;
            x = hitX;
            y = hitY;
            return false;
        }

        return true;
    }

    public void draw(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(getColor());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(
            x - getSize() / 2f,
            y - getSize() / 2f,
            getSize(),
            getSize()
        );
        shapeRenderer.end();
    }

    public void setLifeTime(float lifeTime) {
        this.lifetime = lifeTime;
    }

    private float projectPoint(float x, float y, float dx, float dy, float px, float py) {
        float lenSq = dx * dx + dy * dy;
        if (lenSq == 0f) return 0f;

        float t = ((px - x) * dx + (py - y) * dy) / lenSq;
        return Math.max(0f, Math.min(1f, t));
    }

    private boolean lineIntersectsRect(float x1, float y1, float x2, float y2,
                                       float left, float bottom, float right, float top) {

        if (pointInRect(x1, y1, left, bottom, right, top) ||
            pointInRect(x2, y2, left, bottom, right, top)) {
            return true;
        }

        return lineIntersectsLine(x1, y1, x2, y2, left, bottom, right, bottom) ||
            lineIntersectsLine(x1, y1, x2, y2, left, top, right, top) ||
            lineIntersectsLine(x1, y1, x2, y2, left, bottom, left, top) ||
            lineIntersectsLine(x1, y1, x2, y2, right, bottom, right, top);
    }

    private boolean pointInRect(float x, float y,
                                float left, float bottom, float right, float top) {
        return x >= left && x <= right && y >= bottom && y <= top;
    }

    private boolean lineIntersectsLine(float x1, float y1, float x2, float y2,
                                       float x3, float y3, float x4, float y4) {

        float den = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
        if (den == 0f) return false;

        float t = ((x1 - x3) * (y3 - y4) - (y1 - y3) * (x3 - x4)) / den;
        float u = -((x1 - x2) * (y1 - y3) - (y1 - y2) * (x1 - x3)) / den;

        return t >= 0f && t <= 1f && u >= 0f && u <= 1f;
    }
}
