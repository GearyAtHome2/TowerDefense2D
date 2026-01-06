package com.Geary.towerdefense.UI.displays.building;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class BuildingUIButton {
    public Rectangle bounds = new Rectangle();
    public String label;
    public Runnable onClick;

    public BuildingUIButton(String label, Runnable onClick) {
        this.label = label;
        this.onClick = onClick;
    }

    public void draw(SpriteBatch batch, BitmapFont font) {
        // draw button rectangle
        // here you'd need ShapeRenderer reference or pass in parameters
    }

    public boolean handleClick(float x, float y) {
        if (bounds.contains(x, y) && onClick != null) {
            onClick.run();
            return true;
        }
        return false;
    }
}
