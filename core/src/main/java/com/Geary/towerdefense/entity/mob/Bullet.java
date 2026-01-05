package com.Geary.towerdefense.entity.mob;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Bullet {

    public static final float SPEED = 350f; // pixels/sec
    float x, y;
    float vx, vy; // velocity components
    float speed = SPEED; // pixels per second
    int damage;
    Texture texture;

    private static final float HIT_RADIUS = 8f;
    private static final float MAX_LIFETIME = 0.8f; // bullet disappears after 2 seconds
    private float lifetime = 0f;

    public Bullet(float x, float y, float angle, int damage) {
        this.x = x;
        this.y = y;
        this.vx = (float) Math.cos(angle) * speed;
        this.vy = (float) Math.sin(angle) * speed;
        this.damage = damage;
        texture = new Texture("bullet.png");
    }

    public boolean update(float delta, java.util.List<Enemy> enemies) {
        lifetime += delta;
        if (lifetime > MAX_LIFETIME) return false;
        // Save previous position
        float startX = x;
        float startY = y;
        // Move bullet
        x += vx * delta;
        y += vy * delta;
        Enemy firstHit = null;
        float hitX = x;
        float hitY = y;
        float closestDistSq = Float.MAX_VALUE;
        for (Enemy e : enemies) {
            if (e.health <= 0) continue;

            float left   = e.x;
            float right  = e.x + e.texture.getWidth();
            float bottom = e.y;
            float top    = e.y + e.texture.getHeight();
            if (lineIntersectsRect(startX, startY, x, y, left, bottom, right, top)) {
                float centerX = (left + right) / 2f;
                float centerY = (bottom + top) / 2f;
                float[] closest = closestPointOnLineSegment(startX, startY, x, y, centerX, centerY);
                float dx = closest[0] - startX;
                float dy = closest[1] - startY;
                float distSq = dx*dx + dy*dy;
                if (distSq < closestDistSq) {
                    closestDistSq = distSq;
                    firstHit = e;
                    hitX = closest[0];
                    hitY = closest[1];
                }
            }
        }

        if (firstHit != null) {
            // Apply damage and move bullet to collision point
            firstHit.health -= damage;
            x = hitX;
            y = hitY;
            return false; // destroy bullet
        }

        return true; // bullet continues
    }

    // Compute the closest point on a line segment (x1,y1 -> x2,y2) to a point (px, py)
    private float[] closestPointOnLineSegment(float x1, float y1, float x2, float y2, float px, float py) {
        float dx = x2 - x1;
        float dy = y2 - y1;

        if (dx == 0 && dy == 0) return new float[]{x1, y1}; // segment is a point

        float t = ((px - x1) * dx + (py - y1) * dy) / (dx*dx + dy*dy);
        t = Math.max(0, Math.min(1, t)); // clamp to segment

        return new float[]{x1 + t * dx, y1 + t * dy};
    }

    // Line segment vs rectangle intersection
    private boolean lineIntersectsRect(float x1, float y1, float x2, float y2, float left, float bottom, float right, float top) {
        // Check if either endpoint is inside the rectangle
        if ((x1 >= left && x1 <= right && y1 >= bottom && y1 <= top) ||
            (x2 >= left && x2 <= right && y2 >= bottom && y2 <= top)) {
            return true;
        }

        // Check for intersection with each edge of the rectangle
        return lineIntersectsLine(x1, y1, x2, y2, left, bottom, right, bottom) || // bottom
            lineIntersectsLine(x1, y1, x2, y2, left, top, right, top) ||       // top
            lineIntersectsLine(x1, y1, x2, y2, left, bottom, left, top) ||     // left
            lineIntersectsLine(x1, y1, x2, y2, right, bottom, right, top);     // right
    }

    // 2D line segment intersection
    private boolean lineIntersectsLine(float x1, float y1, float x2, float y2,
                                       float x3, float y3, float x4, float y4) {
        float den = (x1-x2)*(y3-y4) - (y1-y2)*(x3-x4);
        if (den == 0) return false; // parallel

        float t = ((x1-x3)*(y3-y4) - (y1-y3)*(x3-x4)) / den;
        float u = -((x1-x2)*(y1-y3) - (y1-y2)*(x1-x3)) / den;

        return t >= 0 && t <= 1 && u >= 0 && u <= 1;
    }


    public void draw(SpriteBatch batch) {
        batch.draw(
            texture,
            x - texture.getWidth() / 2f,
            y - texture.getHeight() / 2f
        );
    }
}
