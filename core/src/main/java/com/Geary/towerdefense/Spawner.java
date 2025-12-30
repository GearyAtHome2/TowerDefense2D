package com.Geary.towerdefense;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Spawner {
    float xPos, yPos;
    Texture texture;
    protected int maxCooldown = 100;
    public int cooldown = maxCooldown;

    public Spawner(float x, float y) {
        this.xPos = x;
        this.yPos = y;
        texture = new Texture("spawner.png");
    }

    public void draw(SpriteBatch batch) {
        batch.draw(texture, xPos, yPos);
    }

    public Enemy spawn() {
        //todo: fix this once I've made the spawner bigger/access spawner width from static?
        //todo: fix around offset?
        return new Enemy(xPos + (texture.getWidth() / 2) - 7, yPos + (texture.getHeight() / 2) - 7);
    }
}
