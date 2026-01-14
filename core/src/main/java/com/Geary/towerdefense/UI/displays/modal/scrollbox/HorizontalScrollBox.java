package com.Geary.towerdefense.UI.displays.modal.scrollbox;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.List;

/**
 * Scrollbox that lays out entries horizontally with optional spacing.
 * Scroll is done left/right with scrollX, but mouse wheel or touch can adjust it.
 */
public class HorizontalScrollBox<T extends ScrollEntry> {

    public final Rectangle bounds = new Rectangle();
    private final List<T> entries = new ArrayList<>();
    private float scrollX = 0f;
    private float contentWidth = 0f;
    private float spacing = 5f;
    private float backgroundR = 0.15f, backgroundG = 0.15f, backgroundB = 0.15f, backgroundA = 1f;

    public HorizontalScrollBox(float x, float y, float width, float height) {
        bounds.set(x, y, width, height);
    }

    public void setSpacing(float spacing) {
        this.spacing = spacing;
    }

    public void setBackgroundColor(float r, float g, float b, float a) {
        backgroundR = r;
        backgroundG = g;
        backgroundB = b;
        backgroundA = a;
    }

    public void setEntries(List<T> newEntries) {
        entries.clear();
        if (newEntries != null) entries.addAll(newEntries);
        layoutEntries();
    }

    public List<T> getEntries() {
        return entries;
    }

    /** Layout entries horizontally with spacing, compute total content width */
    private void layoutEntries() {
        float xOffset = bounds.x + spacing;
        float maxHeight = 0f;

        for (T entry : entries) {
            Rectangle eBounds = entry.bounds();
            eBounds.set(xOffset, bounds.y + spacing, eBounds.width, eBounds.height);
            xOffset += eBounds.width + spacing;
            maxHeight = Math.max(maxHeight, eBounds.height);
        }

        contentWidth = xOffset - bounds.x; // total width including spacing
        clampScroll();
    }


    //todo: apply scissor stack here.
    /** Draw entries clipped to the scrollbox area */
    public void draw(ShapeRenderer renderer, SpriteBatch batch, BitmapFont font) {
        // Draw background
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(backgroundR, backgroundG, backgroundB, backgroundA);
        renderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        renderer.end();

        // Draw entries with scroll offset
//        batch.begin();
        for (T entry : entries) {
            Rectangle eBounds = entry.bounds();
            float drawX = eBounds.x - scrollX;
            if (drawX + eBounds.width < bounds.x) continue; // left of visible area
            if (drawX > bounds.x + bounds.width) continue; // right of visible area

            // Temporarily shift the entry
            float oldX = eBounds.x;
            eBounds.x = drawX;
            entry.draw(renderer, batch, font);
            eBounds.x = oldX; // restore
        }
//        batch.end();
    }

    /** Scroll horizontally by delta pixels */
    public void scroll(float delta) {
        scrollX += delta;
        clampScroll();
    }

    /** Ensure scrollX stays within valid range */
    private void clampScroll() {
        if (contentWidth <= bounds.width) {
            scrollX = 0;
        } else {
            scrollX = Math.max(0f, Math.min(scrollX, contentWidth - bounds.width));
        }
    }

    /** Check if a point is inside the scrollbox */
    public boolean contains(float x, float y) {
        return bounds.contains(x, y);
    }

    /** Click handling: forward to entry if inside */
    public T click(float x, float y) {
        for (T entry : entries) {
            Rectangle eBounds = entry.bounds();
            float adjustedX = eBounds.x - scrollX;
            if (adjustedX <= x && x <= adjustedX + eBounds.width &&
                eBounds.y <= y && y <= eBounds.y + eBounds.height) {
                entry.click(x + scrollX, y);
                return entry;
            }
        }
        return null;
    }

}
