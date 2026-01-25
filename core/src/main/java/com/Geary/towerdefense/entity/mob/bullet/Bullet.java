package com.Geary.towerdefense.entity.mob.bullet;

import com.Geary.towerdefense.entity.mob.enemy.Enemy;
import com.Geary.towerdefense.entity.resources.Resource;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.EnumMap;
import java.util.List;

public abstract class Bullet {

    public float x;
    public float y;
    public float vx;
    public float vy;
    public int damage;
    public float speed;
    public float maxLifeTime;//protect these once fixed
    public float lifetime;
    protected float knockback;
    public String name;
    public float size;
    public Color color;
    EnumMap<Resource.RawResourceType, Double> rawResourceCost;
    EnumMap<Resource.RefinedResourceType, Double> refinedResourceCost;

    protected Bullet(String name, int damage, float speed, float maxLifeTime, float knockback, float size, Color color) {
        this.name = name;
        this.damage = damage;
        this.speed = speed;
        this.maxLifeTime = maxLifeTime;
        this.knockback = knockback;
        this.size = size;
        this.color = color;
    }

    // Subclasses must provide these constants
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

    /**
     * Factory method: creates a new instance for shooting
     */
    public abstract Bullet createInstance(float x, float y, float angle);

    /**
     * Update position, check collision with enemies, return false if bullet should be removed
     */
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
            float right = e.xPos + e.size;
            float bottom = e.yPos;
            float top = e.yPos + e.size;

            if (lineIntersectsRect(startX, startY, x, y, left, bottom, right, top)) {
                float centerX = (left + right) / 2f;
                float centerY = (bottom + top) / 2f;
                float[] closest = closestPointOnLineSegment(startX, startY, x, y, centerX, centerY);
                float dx = closest[0] - startX;
                float dy = closest[1] - startY;
                float distSq = dx * dx + dy * dy;

                if (distSq < closestDistSq) {
                    closestDistSq = distSq;
                    firstHit = e;
                    hitX = closest[0];
                    hitY = closest[1];
                }
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

    public void setLifeTime(float lifeTime) {
        this.lifetime = lifeTime;
    }

    /**
     * Draw bullet on screen
     */
    public void draw(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(getColor());
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(x - getSize() / 2f, y - getSize() / 2f, getSize(), getSize());
        shapeRenderer.end();
    }

    public float getCenterX() {
        return x + getSize() / 2f;
    }

    public float getCenterY() {
        return y + getSize() / 2f;
    }

    // --- Utility functions for collision detection ---
    private float[] closestPointOnLineSegment(float x1, float y1, float x2, float y2, float px, float py) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        if (dx == 0 && dy == 0) return new float[]{x1, y1};
        float t = ((px - x1) * dx + (py - y1) * dy) / (dx * dx + dy * dy);
        t = Math.max(0, Math.min(1, t));
        return new float[]{x1 + t * dx, y1 + t * dy};
    }

    private boolean lineIntersectsRect(float x1, float y1, float x2, float y2, float left, float bottom, float right, float top) {
        if ((x1 >= left && x1 <= right && y1 >= bottom && y1 <= top) ||
            (x2 >= left && x2 <= right && y2 >= bottom && y2 <= top)) return true;

        return lineIntersectsLine(x1, y1, x2, y2, left, bottom, right, bottom) ||
            lineIntersectsLine(x1, y1, x2, y2, left, top, right, top) ||
            lineIntersectsLine(x1, y1, x2, y2, left, bottom, left, top) ||
            lineIntersectsLine(x1, y1, x2, y2, right, bottom, right, top);
    }

    private boolean lineIntersectsLine(float x1, float y1, float x2, float y2, float x3, float y3, float x4, float y4) {
        float den = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
        if (den == 0) return false;
        float t = ((x1 - x3) * (y3 - y4) - (y1 - y3) * (x3 - x4)) / den;
        float u = -((x1 - x2) * (y1 - y3) - (y1 - y2) * (x1 - x3)) / den;
        return t >= 0 && t <= 1 && u >= 0 && u <= 1;
    }
}
