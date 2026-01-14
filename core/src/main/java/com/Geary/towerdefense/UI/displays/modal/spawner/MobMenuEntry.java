package com.Geary.towerdefense.UI.displays.modal.spawner;

import com.Geary.towerdefense.UI.displays.modal.scrollbox.ScrollEntry;
import com.Geary.towerdefense.UI.render.icons.IconStore;
import com.Geary.towerdefense.entity.mob.Mob;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class MobMenuEntry implements ScrollEntry {

    public interface MobEntryListener {
        void onRecruitClicked(MobMenuEntry entry);
        void onGarrisonClicked(MobMenuEntry entry);
    }

    public final Mob templateMob;
    public final String name;
    public final Rectangle bounds = new Rectangle();
    public Runnable onClick;

    // Button bounds
    private final Rectangle recruitButton = new Rectangle();
    private final Rectangle garrisonButton = new Rectangle();

    private final MobEntryListener listener;

    private static final GlyphLayout layout = new GlyphLayout();

    public MobMenuEntry(Mob templateMob, float x, float y, float width, float height, MobEntryListener listener) {
        this.templateMob = templateMob;
        this.name = templateMob.name;
        bounds.set(x, y, width, height);
        this.listener = listener;
    }

    public void draw(ShapeRenderer renderer, SpriteBatch batch, BitmapFont font, Camera camera) {
        float x = bounds.x;
        float y = bounds.y;
        float h = bounds.height;
        float w = bounds.width;

        // --- Background ---
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        Color bg = computeBackgroundColor(templateMob.order);
        renderer.setColor(bg);
        renderer.rect(x, y, w, h);
        renderer.end();

        // --- Icon & Name & Stats ---
        batch.begin();

        var icon = IconStore.mob(templateMob.name);
        if (icon != null) batch.draw(icon, x + 4, y + 4, h - 8, h - 8);

        float nameX = x + h + 6;
        font.draw(batch, name, nameX, y + h * 0.7f);

        GlyphLayout nameLayout = new GlyphLayout(font, name);
        float nameW = nameLayout.width;

        float statsX = nameX + nameW + 12f;
        float statsW = w * 0.22f;

        String statsText = "Health: " + templateMob.health + "\n" +
            "Damage: " + templateMob.damage;
        if (templateMob.armour > 0) statsText += "\nArmour: " + templateMob.armour;

        drawScaledText(font, batch, statsText, statsX, y, statsW, h, 0.7f, 1.0f);

        batch.end();

        // --- Buttons ---
        float buttonWidth = 60f;
        float buttonHeight = (h - 6f) / 2f;
        float buttonX = x + w - buttonWidth - 6f;
        float buttonYTop = y + h - buttonHeight - 3f;
        float buttonYBottom = y + 3f;

        // Update button rectangles
        recruitButton.set(buttonX, buttonYTop, buttonWidth, buttonHeight);
        garrisonButton.set(buttonX, buttonYBottom, buttonWidth, buttonHeight);

        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(0.1f, 0.7f, 0.1f, 1f); // Recruit
        renderer.rect(recruitButton.x, recruitButton.y, recruitButton.width, recruitButton.height);
        renderer.setColor(0.5f, 0.5f, 0.5f, 1f); // Garrison
        renderer.rect(garrisonButton.x, garrisonButton.y, garrisonButton.width, garrisonButton.height);
        renderer.end();

        // --- Effect text ---
        batch.begin();
        float effectX = statsX + statsW + 6f;
        float effectW = buttonX - effectX - 6f; // leave padding for buttons
        drawScaledText(font, batch, templateMob.effectText, effectX, y, effectW, h, 0.7f, 1.0f);

        // --- Button labels ---
        drawScaledText(font, batch, "Recruit", buttonX + 2, buttonYTop, buttonWidth, buttonHeight, 0.5f, 1f);
        drawScaledText(font, batch, "Garrison", buttonX + 2, buttonYBottom, buttonWidth, buttonHeight, 0.5f, 0.7f);

        batch.end();
    }

    public boolean click(float x, float y) {
        // Buttons first
        if (recruitButton.contains(x, y)) {
            if (listener != null) listener.onRecruitClicked(this);
            return true;
        }
        if (garrisonButton.contains(x, y)) {
            if (listener != null) listener.onGarrisonClicked(this);
            return true;
        }

        // Entry click
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

        float oldA = font.getColor().a;
        font.setColor(font.getColor().r, font.getColor().g, font.getColor().b, alpha);

        float textY = y + (height / 2f) + (layout.height / 2f);
        font.draw(batch, layout, x, textY);

        font.setColor(font.getColor().r, font.getColor().g, font.getColor().b, oldA);
        font.getData().setScale(1f);
    }

    public Rectangle bounds() {
        return bounds;
    }

    private Color computeBackgroundColor(Mob.Order order) {
        Color base = switch (order) {
            case NEUTRAL -> new Color(0.45f, 0.45f, 0.45f, 1f);
            case TECH    -> new Color(0.3f, 0.35f, 0.5f, 1f);
            case NATURE  -> new Color(0.15f, 0.55f, 0.15f, 1f);
            case DARK    -> new Color(0.05f, 0.05f, 0.05f, 1f);
            case LIGHT   -> new Color(0.65f, 0.65f, 0.45f, 1f);
            case FIRE    -> new Color(0.65f, 0.15f, 0.1f, 1f);
            case WATER   -> new Color(0.15f, 0.3f, 0.65f, 1f);
        };
        return base;
    }
}
