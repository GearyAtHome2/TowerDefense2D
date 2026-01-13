package com.Geary.towerdefense.UI.displays.modal.scrollbox;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public interface ScrollEntry {
    void draw(ShapeRenderer renderer, SpriteBatch batch, BitmapFont font);
    boolean click(float x, float y);
    Rectangle bounds();
}
