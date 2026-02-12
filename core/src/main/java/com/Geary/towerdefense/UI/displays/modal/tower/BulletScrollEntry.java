package com.Geary.towerdefense.UI.displays.modal.tower;

import com.Geary.towerdefense.UI.displays.modal.scrollbox.ScrollEntry;
import com.Geary.towerdefense.UI.render.icons.IconStore;
import com.Geary.towerdefense.entity.mob.bullet.BulletRepr;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class BulletScrollEntry implements ScrollEntry {

    private final Rectangle bounds = new Rectangle();
    private final BulletRepr bullet;
    private final TextureRegion icon;

    public boolean selected = false;
    public boolean active = false;
    public boolean affordable = true; // default to true

    public BulletScrollEntry(BulletRepr bullet) {
        this.bullet = bullet;
        this.bounds.height = 52f;
        this.icon = IconStore.ammo(bullet.getName());
    }

    @Override
    public Rectangle bounds() {
        return bounds;
    }

    @Override
    public void draw(
        ShapeRenderer renderer,
        SpriteBatch batch,
        BitmapFont font,
        Camera camera
    ) {
        // -------- Background color by state --------
        if (active) {
            renderer.begin(ShapeRenderer.ShapeType.Filled);
            renderer.setColor(0.15f, 0.35f, 0.15f, 1f); // green
            renderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
            renderer.end();
        } else if (selected) {
            renderer.begin(ShapeRenderer.ShapeType.Filled);
            renderer.setColor(0.28f, 0.28f, 0.28f, 1f); // pale grey
            renderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
            renderer.end();
        } else {
            renderer.begin(ShapeRenderer.ShapeType.Filled);
            renderer.setColor(0.18f, 0.18f, 0.18f, 1f);
            renderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
            renderer.end();
        }

        // Outline for ACTIVE
        if (active) {
            renderer.begin(ShapeRenderer.ShapeType.Line);
            renderer.setColor(1f, 1f, 0f, 1f); // yellow
            renderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
            renderer.end();
        }

        batch.begin();

        // Icon
        float iconSize = bounds.height - 8f;
        batch.draw(icon, bounds.x + 4, bounds.y + 4, iconSize, iconSize);

        float textX = bounds.x + iconSize + 12;
        float topTextY = bounds.y + bounds.height - 6;

        // Name + stats with affordability coloring
        if (!affordable) {
            font.setColor(0.5f, 0.5f, 0.5f, 1f); // grey out
        } else {
            font.setColor(Color.WHITE);
        }

        font.draw(batch, bullet.getName(), textX, topTextY);
        font.draw(batch, "DMG: " + bullet.getDamage() + "  SPD: " + bullet.getSpeed(), textX, topTextY - 16);

        batch.end();
    }

    public void setAffordable(boolean affordable) {
        this.affordable = affordable;
    }

    @Override
    public boolean click(float x, float y) {
        return bounds.contains(x, y);
    }

    public BulletRepr getBullet() {
        return bullet;
    }
}
