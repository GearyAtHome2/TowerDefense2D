package com.Geary.towerdefense.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Bullet {

    float x, y;
    float speed = 300f;
    Enemy target;
    Texture texture;
    int damage = 1;

    private static final float HIT_RADIUS = 8f;

    public Bullet(float x, float y, Enemy target) {
        this.x = x;
        this.y = y;
        this.target = target;
        texture = new Texture("bullet.png");
    }

    // return false = destroy bullet
    public boolean update(float delta) {

        if (target == null || target.health <= 0) {
            return false;
        }

        float enemyCenterX = target.x + target.texture.getWidth() / 2f;
        float enemyCenterY = target.y + target.texture.getHeight() / 2f;

        float dx = enemyCenterX - x;
        float dy = enemyCenterY - y;
        float dist = (float) Math.sqrt(dx * dx + dy * dy);

        //hit
        if (dist <= HIT_RADIUS) {
            target.health -= damage;
            return false;
        }

        //move
        x += (dx / dist) * speed * delta;
        y += (dy / dist) * speed * delta;

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
