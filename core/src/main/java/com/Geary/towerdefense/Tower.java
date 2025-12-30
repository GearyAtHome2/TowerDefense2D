package com.Geary.towerdefense;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.List;

public class Tower {
    float xPos, yPos;
    Texture texture;

    protected int maxCooldown = 50;
    public int cooldown = maxCooldown;

    float range = 250f; // pixels

    public Tower(float x, float y) {
        this.xPos = x;
        this.yPos = y;
        texture = new Texture("tower.png");
    }

    public void draw(SpriteBatch batch) {
        batch.draw(texture, xPos, yPos);
    }

    // Find the best enemy to shoot
    public Enemy findTarget(List<Enemy> enemies) {
        Enemy closest = null;
        float closestDistance = Float.MAX_VALUE;

        float centerX = xPos + texture.getWidth() / 2f;
        float centerY = yPos + texture.getHeight() / 2f;

        for (Enemy e : enemies) {
            float dx = e.getCenterX() - centerX;
            float dy = e.getCenterY() - centerY;
            float dist = dx * dx + dy * dy; // squared distance

            if (dist <= range * range && dist < closestDistance) {
                closestDistance = dist;
                closest = e;
            }
        }
        return closest;
    }

    public Bullet shoot(Enemy target) {
        return new Bullet(
            xPos + texture.getWidth() / 2f,
            yPos + texture.getHeight() / 2f,
            target
        );
    }
}
