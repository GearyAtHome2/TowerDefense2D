package com.Geary.towerdefense.UI.displays.modal.spawner;

import com.Geary.towerdefense.UI.displays.modal.ScrollEntry;
import com.Geary.towerdefense.UI.render.icons.IconStore;
import com.Geary.towerdefense.entity.mob.Mob;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class MobMenuEntry implements ScrollEntry {

    public final Mob templateMob;
    public final String name;
    public final Rectangle bounds = new Rectangle();
    public Runnable onClick;

    private static final GlyphLayout layout = new GlyphLayout();

    public MobMenuEntry(Mob templateMob, float x, float y, float width, float height) {
        this.templateMob = templateMob;
        this.name = templateMob.name;
        bounds.set(x, y, width, height);
    }

    public void draw(ShapeRenderer renderer, SpriteBatch batch, BitmapFont font) {
        // Background
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(0.2f, 0.3f, 0.4f, 1f);
        renderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        renderer.end();

        batch.begin();

        float x = bounds.x;
        float y = bounds.y;
        float h = bounds.height;
        float w = bounds.width;

        // --- Icon ---
        var icon = IconStore.mob(templateMob.name);
        if (icon != null) {
            batch.draw(icon, x + 4, y + 4, h - 8, h - 8);
        }

        // --- Name ---
        float nameX = x + h + 6;
        font.draw(batch, name, nameX, y + h * 0.7f);

        // Measure name width
        GlyphLayout nameLayout = new GlyphLayout(font, name);
        float nameW = nameLayout.width;

// --- Stats block (directly after name) ---
        float statsX = nameX + nameW + 12f;
        float statsW = w * 0.22f; // slightly smaller, so effect text can use more space

        String statsText = "Health: " + templateMob.health + "\n" +
            "Damage: " + templateMob.damage + "\n";

        if (templateMob.armour > 0){
            System.out.println("adding armour to text");
            statsText+="Armour: " + templateMob.armour;
        }

        drawScaledText(font, batch, statsText, statsX, y, statsW, h, 0.7f, 1.0f);

// --- Effect text (take up remaining right-hand side) ---
        float effectX = statsX + statsW + 6f;
        float effectW = (x + w) - effectX - 6f; // from effectX to right edge minus padding

        drawScaledText(font, batch, templateMob.effectText, effectX, y, effectW, h, 0.7f, 1.0f);

        batch.end();
    }

    public boolean click(float x, float y) {
        if (bounds.contains(x, y)) {
            if (onClick != null) onClick.run();
            return true;
        }
        return false;
    }

    private void drawScaledText(BitmapFont font, SpriteBatch batch, String text,
                                float x, float y, float width, float height,
                                float minScale, float alpha) {
        font.getData().setScale(1f);

        layout.setText(font, text, font.getColor(), width, com.badlogic.gdx.utils.Align.left, true);
        if (layout.height == 0) return;

        float scale = (height * 0.85f) / layout.height;
        scale = Math.min(scale, 1.0f);
        scale = Math.max(scale, minScale);

        font.getData().setScale(scale);

        layout.setText(font, text, font.getColor(), width, com.badlogic.gdx.utils.Align.left, true);

        // Apply alpha
        float oldA = font.getColor().a;
        font.setColor(font.getColor().r, font.getColor().g, font.getColor().b, alpha);

        float textY = y + (height / 2f) + (layout.height / 2f);
        font.draw(batch, layout, x, textY);

        // Restore
        font.setColor(font.getColor().r, font.getColor().g, font.getColor().b, oldA);
        font.getData().setScale(1f);
    }

    public Rectangle bounds() {
        return bounds;
    }
}
