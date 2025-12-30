package com.Geary.towerdefense.entity;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Spawner {
    float xPos, yPos;
    Texture texture;
    public float maxCooldown = 3f;
    public float cooldown = maxCooldown;

    public Spawner(float x, float y) {
        this.xPos = x;
        this.yPos = y;
        texture = new Texture("spawner.png");
    }

    public void draw(SpriteBatch batch) {
        batch.draw(texture, xPos, yPos);
    }

    public Enemy spawn() {
        return new Enemy(xPos + (texture.getWidth() / 2) - 7, yPos + (texture.getHeight() / 2) - 7);
    }
}
