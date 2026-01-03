package com.Geary.towerdefense.entity.sprite;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class Spark {

    private float xPos, yPos;
    private float lifetime;

    public Spark(float x, float y, float lifetime) {
        this.xPos = x;
        this.yPos = y;
        this.lifetime = lifetime;
    }

    public void reset(float x, float y, float lifetime) {
        this.xPos = x;
        this.yPos = y;
        this.lifetime = lifetime;
    }

    public boolean isVisible() {
        return lifetime > 0f;
    }

    public void update(float delta) {
        lifetime -= delta;
    }

    public void draw(ShapeRenderer sr) {
        if (!isVisible()) return;
        float size = 40f;
        float half = size / 2f;
        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.setColor(1f, 1f, 0f, 1f);
        sr.line(xPos, yPos - half, xPos, yPos + half);
        sr.line(xPos - half, yPos, xPos + half, yPos);
        sr.end();
    }
}
