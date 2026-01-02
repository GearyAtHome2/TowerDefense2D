package com.Geary.towerdefense.entity;

import com.Geary.towerdefense.entity.mob.Enemy;
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

    // return false = destroy bullet
    public boolean update(float delta, java.util.List<Enemy> enemies) {
        lifetime += delta;
        if (lifetime > MAX_LIFETIME) return false;

        // move bullet
        x += vx * delta;
        y += vy * delta;

        // check for collisions with any enemy
        for (Enemy e : enemies) {
            if (e.health <= 0) continue;

            float enemyCenterX = e.x + e.texture.getWidth() / 2f;
            float enemyCenterY = e.y + e.texture.getHeight() / 2f;

            float dx = enemyCenterX - x;
            float dy = enemyCenterY - y;
            float distSq = dx * dx + dy * dy;

            if (distSq <= HIT_RADIUS * HIT_RADIUS) {
                e.health -= damage;
                return false; // destroy bullet on hit
            }
        }

        return true;
    }

    public void draw(SpriteBatch batch) {
        batch.draw(
            texture,
            x - texture.getWidth() / 2f,
            y - texture.getHeight() / 2f
        );
    }
}
