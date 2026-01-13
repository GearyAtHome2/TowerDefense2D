package com.Geary.towerdefense.UI.displays.modal.spawner;

import com.Geary.towerdefense.UI.displays.modal.scrollbox.ScrollEntry;
import com.Geary.towerdefense.UI.render.icons.IconStore;
import com.Geary.towerdefense.entity.mob.Mob;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class QueueEntry implements ScrollEntry {

    public final Mob mob;
    public final Rectangle bounds = new Rectangle();
    public Runnable onClick;

    public QueueEntry(Mob mob, float x, float y, float size) {
        this.mob = mob;
        bounds.set(x, y, size, size);
    }

    @Override
    public void draw(ShapeRenderer renderer, SpriteBatch batch, BitmapFont font) {
        // Background
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(0.2f, 0.2f, 0.2f, 1f);
        renderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        renderer.end();

        // Mob icon
        batch.begin();
        var icon = IconStore.mob(mob.name);
        if (icon != null) {
            batch.draw(icon, bounds.x + 2, bounds.y + 2, bounds.width - 4, bounds.height - 4);
        }
        batch.end();
    }

    @Override
    public boolean click(float x, float y) {
        if (bounds.contains(x, y)) {
            if (onClick != null) onClick.run();
            return true;
        }
        return false;
    }

    @Override
    public Rectangle bounds() {
        return bounds;
    }
}
