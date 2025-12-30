package com.Geary.towerdefense.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.List;

public class Tower {
    public float xPos, yPos;
    public Texture texture;

    public float range = 207f; // pixels

    public float maxCooldown = 2f;
    public float cooldown = maxCooldown;


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
