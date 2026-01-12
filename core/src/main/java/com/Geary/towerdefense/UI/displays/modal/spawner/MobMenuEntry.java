package com.Geary.towerdefense.UI.displays.modal.spawner;

import com.Geary.towerdefense.UI.displays.modal.ScrollEntry;
import com.Geary.towerdefense.UI.render.icons.IconStore;
import com.Geary.towerdefense.entity.mob.Mob;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class MobMenuEntry implements ScrollEntry {

    public final Mob templateMob;
    public final String name;
    public final Rectangle bounds = new Rectangle();
    public Runnable onClick;

    public MobMenuEntry(Mob templateMob, float x, float y, float width, float height) {
        this.templateMob = templateMob;
        this.name = templateMob.name;
        bounds.set(x, y, width, height);
    }

    // REMOVE batch.begin() / end() here
    public void draw(ShapeRenderer renderer, SpriteBatch batch, BitmapFont font) {
        // Draw background with renderer (optional)
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(0.2f, 0.3f, 0.4f, 1f);
        renderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        renderer.end();

        // Draw icon + text using batch (assume already batch.begin() is called outside)
        batch.begin();
        var icon = IconStore.mob(templateMob.name);
        if (icon != null) {
            batch.draw(icon, bounds.x + 4, bounds.y + 4, bounds.height - 8, bounds.height - 8);
        }
        font.draw(batch, name, bounds.x + bounds.height, bounds.y + bounds.height * 0.7f);
        batch.end();
    }

    public boolean click(float x, float y) {
        if (bounds.contains(x, y)) {
            if (onClick != null) onClick.run();
            return true;
        }
        return false;
    }

    public Rectangle bounds() {
        return bounds;
    }
}
