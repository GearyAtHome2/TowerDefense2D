package com.Geary.towerdefense.UI.displays.modal.spawner;

import com.Geary.towerdefense.UI.displays.modal.scrollbox.ScrollEntry;
import com.Geary.towerdefense.UI.render.icons.IconStore;
import com.Geary.towerdefense.entity.mob.Mob;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class MobMenuEntry implements ScrollEntry {

    public interface MobEntryListener {
        void onRecruitClicked(MobMenuEntry entry, int amount);
        void onGarrisonClicked(MobMenuEntry entry, int amount);
    }

    public boolean selected = false;
    private boolean affordable = true;

    public final Mob templateMob;
    public final Rectangle bounds = new Rectangle();
    public Runnable onClick;

    private final Rectangle recruitButton = new Rectangle();
    private final Rectangle garrisonButton = new Rectangle();

    private final MobEntryListener listener;

    public MobMenuEntry(Mob templateMob, float x, float y, float width, float height, MobEntryListener listener) {
        this.templateMob = templateMob;
        this.listener = listener;
        bounds.set(x, y, width, height);
    }

    public void draw(ShapeRenderer renderer, SpriteBatch batch, BitmapFont font, Camera camera) {
        float x = bounds.x;
        float y = bounds.y;
        float w = bounds.width;
        float h = bounds.height;

        // Background (highlight if selected)
        Color base = computeBackgroundColor(templateMob.order);
        Color bg = selected ? base.lerp(new Color(1f, 1f, 0.5f, 1f), 0.5f) : base;

        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(bg);
        renderer.rect(x, y, w, h);
        renderer.end();

        // Layout
        float hPad = w * 0.01f;
        float vPad = h * 0.08f;
        float rightPadding = w * 0.005f;

        float iconSize = h * 0.9f;
        float iconX = x + hPad;

        float contentX = iconX + iconSize + hPad;
        float contentW = w - (iconSize + hPad * 2f + rightPadding);

        float nameW = contentW * 0.25f;
        float costW = contentW * 0.5f;
        float buttonW = contentW - nameW - costW;

        float nameX = contentX;
        float costX = nameX + nameW;
        float buttonX = costX + costW;

        float buttonH = (h - vPad * 3f) / 2f;
        float buttonYTop = y + h - buttonH - vPad;
        float buttonYBottom = y + vPad;

        recruitButton.set(buttonX, buttonYTop, buttonW, buttonH);
        garrisonButton.set(buttonX, buttonYBottom, buttonW, buttonH);

        // Draw icon, name, cost
        batch.begin();
        float costAlpha = affordable ? 0.85f : 0.5f;
        font.setColor(1f, 1f, 1f, 1f);

        var icon = IconStore.mob(templateMob.name);
        if (icon != null) batch.draw(icon, iconX, y + (h - iconSize) * 0.5f, iconSize, iconSize);

        font.draw(batch, templateMob.name, nameX, y + h * 0.6f);
        font.draw(batch, templateMob.getCostText(), costX, y + h * 0.6f);
        batch.end();

        // Draw buttons
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(affordable ? 0.1f : 0.15f, affordable ? 0.7f : 0.35f, affordable ? 0.1f : 0.15f, 1f);
        renderer.rect(recruitButton.x, recruitButton.y, recruitButton.width, recruitButton.height);

        renderer.setColor(affordable ? 0.5f : 0.25f, affordable ? 0.5f : 0.25f, affordable ? 0.5f : 0.25f, 1f);
        renderer.rect(garrisonButton.x, garrisonButton.y, garrisonButton.width, garrisonButton.height);
        renderer.end();

        batch.begin();
        font.setColor(1f, 1f, 1f, costAlpha);
        font.draw(batch, isShiftHeld() ? "Recruit x5" : "Recruit", recruitButton.x + 4, recruitButton.y + buttonH * 0.7f);
        font.draw(batch, isShiftHeld() ? "Garrison x5" : "Garrison", garrisonButton.x + 4, garrisonButton.y + buttonH * 0.7f);
        batch.end();
    }

    public boolean click(float x, float y) {
        int amount = isShiftHeld() ? 5 : 1;

        // Buttons first (they imply selection, but don't set it here)
        if (affordable) {
            if (recruitButton.contains(x, y) && listener != null) {
                listener.onRecruitClicked(this, amount);
                return true;
            }
            if (garrisonButton.contains(x, y) && listener != null) {
                if (onClick != null) onClick.run();
                listener.onGarrisonClicked(this, amount);
                return true;
            }
        }

        if (bounds.contains(x, y)) {
            if (onClick != null) onClick.run();
            return true;
        }

        return false;
    }


    private boolean isShiftHeld() {
        return Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);
    }

    public void setAffordable(boolean affordable) {
        this.affordable = affordable;
    }

    @Override
    public Rectangle bounds() {
        return bounds;
    }

    private Color computeBackgroundColor(Mob.Order order) {
        return switch (order) {
            case NEUTRAL -> new Color(0.45f, 0.45f, 0.45f, 1f);
            case TECH -> new Color(0.32f, 0.35f, 0.35f, 1f);
            case NATURE -> new Color(0.15f, 0.55f, 0.15f, 1f);
            case DARK -> new Color(0.05f, 0.05f, 0.05f, 1f);
            case LIGHT -> new Color(0.95f, 0.95f, 0.75f, 1f);
            case FIRE -> new Color(0.65f, 0.15f, 0.1f, 1f);
            case WATER -> new Color(0.15f, 0.3f, 0.65f, 1f);
        };
    }
}
