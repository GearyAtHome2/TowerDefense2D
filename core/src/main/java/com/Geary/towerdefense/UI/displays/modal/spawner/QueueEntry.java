package com.Geary.towerdefense.UI.displays.modal.spawner;

import com.Geary.towerdefense.UI.displays.modal.scrollbox.ScrollEntry;
import com.Geary.towerdefense.UI.render.icons.IconStore;
import com.Geary.towerdefense.entity.mob.Mob;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class QueueEntry implements ScrollEntry {

    public final Mob mob;
    public final Rectangle bounds = new Rectangle();
    public Runnable onClick;

    float cooldownElapsed = 0f;
    public boolean isLeftmost = false; // flag to know which entry gets the cooldown effect
    public boolean isToGarrison; // flag to know which entry gets the cooldown effect

    public QueueEntry(Mob mob, float size, boolean isToGarrison) {
        this.mob = mob;
        bounds.set(0, 0, size, size);
        this.isToGarrison = isToGarrison;
    }

    @Override
    public void draw(ShapeRenderer renderer, SpriteBatch batch, BitmapFont font, Camera camera) {
        // Dim only for queue entries
        Color bg = computeBackgroundColor(mob.order);

        if (!isLeftmost || !(cooldownElapsed > 0f)) {
            // this guarantees we only dim queue entries (because only queue entries can be leftmost or have cooldown elapsed)
            bg = new Color(bg.r * 0.6f, bg.g * 0.6f, bg.b * 0.6f, 1f);
        }

        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(bg);
        renderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        renderer.end();

        // Cooldown arc only for the leftmost entry
        if (isLeftmost && mob.spawnTime > 0f) {
            renderer.begin(ShapeRenderer.ShapeType.Line);
            renderer.setColor(Color.CYAN);
            float cx = bounds.x + bounds.width / 2f;
            float cy = bounds.y + bounds.height / 2f;
            float radius = bounds.width / 2f - 2f;
            float progress = cooldownElapsed / mob.spawnTime;
            drawArc(renderer, cx, cy, radius, 90f, -360f * progress, 3f);
            renderer.end();
        }
        if (isToGarrison) {
            batch.begin();
            font.setColor(Color.YELLOW);
            float gSize = bounds.height * 0.3f; // scale relative to entry
            font.getData().setScale(gSize / font.getCapHeight()); // scale so it fits
            font.draw(batch, "G", bounds.x + bounds.width - gSize, bounds.y + gSize);
            font.getData().setScale(1f); // reset scale
            batch.end();
        }

        // Draw the mob icon normally
        batch.begin();
        var icon = IconStore.mob(mob.name);
        if (icon != null) batch.draw(icon, bounds.x + 2, bounds.y + 2, bounds.width - 4, bounds.height - 4);
        batch.end();
    }

    @Override
    public boolean click(float x, float y) {
//        if (bounds.contains(x, y)) {
//            if (onClick != null) onClick.run();
//            return true;
//        }
//        return false;
        return bounds.contains(x, y);
    }

    @Override
    public Rectangle bounds() {
        return bounds;
    }

    private Color computeBackgroundColor(Mob.Order order) {
        return switch (order) {
            case NEUTRAL -> new Color(0.45f, 0.45f, 0.45f, 1f);
            case TECH    -> new Color(0.3f, 0.35f, 0.5f, 1f);
            case NATURE  -> new Color(0.15f, 0.55f, 0.15f, 1f);
            case DARK    -> new Color(0.05f, 0.05f, 0.05f, 1f);
            case LIGHT   -> new Color(0.65f, 0.65f, 0.45f, 1f);
            case FIRE    -> new Color(0.65f, 0.15f, 0.1f, 1f);
            case WATER   -> new Color(0.15f, 0.3f, 0.65f, 1f);
        };
    }

    public void update(float delta) {
        if (isLeftmost) {
            cooldownElapsed += delta;
            if (cooldownElapsed > mob.spawnTime) cooldownElapsed = mob.spawnTime;
        }
    }

    public void resetCooldown() {
        cooldownElapsed = 0f;
    }

    /**
     * Draws an arc using small line segments
     *
     * @param renderer ShapeRenderer with ShapeType.Line
     * @param cx       center X
     * @param cy       center Y
     * @param radius   radius of arc
     * @param startDeg starting angle in degrees (0 = right, 90 = top)
     * @param sweepDeg sweep angle in degrees (positive = clockwise)
     * @param thickness not used directly; renderer line width is global
     */
    private void drawArc(ShapeRenderer renderer, float cx, float cy, float radius, float startDeg, float sweepDeg, float thickness) {
        int segments = 60; // smoothness
        float angleStep = sweepDeg / segments;
        Vector2 prev = null;

        for (int i = 0; i <= segments; i++) {
            float angle = startDeg + i * angleStep;
            double rad = Math.toRadians(angle);
            float x = cx + radius * (float)Math.cos(rad);
            float y = cy + radius * (float)Math.sin(rad);

            if (prev != null) {
                renderer.line(prev.x, prev.y, x, y);
            }
            prev = new Vector2(x, y);
        }
    }
}
