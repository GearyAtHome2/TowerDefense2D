package com.Geary.towerdefense.UI.displays.modal;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class ModalButton {

    public final Rectangle bounds = new Rectangle();
    private final Runnable onClick;
    private final float r, g, b;

    public ModalButton(float x, float y, float width, float height, float r, float g, float b, Runnable onClick) {
        this.bounds.set(x, y, width, height);
        this.onClick = onClick;
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public void draw(ShapeRenderer renderer) {
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(r, g, b, 1f);
        renderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        renderer.end();
    }

    public boolean click(float worldX, float worldY) {
        if (bounds.contains(worldX, worldY)) {
            onClick.run();
            return true;
        }
        return false;
    }
}
