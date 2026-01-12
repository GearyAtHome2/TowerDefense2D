package com.Geary.towerdefense.UI.displays.modal;

import com.Geary.towerdefense.UI.displays.modal.factory.RecipeMenuEntry;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class ScrollBox {

    public final Rectangle bounds = new Rectangle();
    public float scrollOffset = 0f;
    private float contentHeight = 0f;

    public final List<RecipeMenuEntry> entries = new ArrayList<>();

    public ScrollBox(float x, float y, float width, float height) {
        bounds.set(x, y, width, height);
    }

    public void setEntries(List<RecipeMenuEntry> entries, float totalHeight) {
        this.entries.clear();
        this.entries.addAll(entries);
        this.contentHeight = totalHeight;
        scrollOffset = 0;
        updateEntryPositions();
    }

    public void scroll(float deltaY) {
        scrollOffset += deltaY; // mouse wheel down = positive deltaY

        float maxOffset = Math.max(0, contentHeight - bounds.height);
        if (scrollOffset < 0) scrollOffset = 0;
        if (scrollOffset > maxOffset) scrollOffset = maxOffset;

        updateEntryPositions();
    }

    private void updateEntryPositions() {
        float y = bounds.y + bounds.height; // top of scrollbox
        float spacing = 5f;

        for (RecipeMenuEntry entry : entries) {
            entry.bounds.x = bounds.x + 10;
            entry.bounds.width = bounds.width - 20;

            y -= entry.bounds.height;
            entry.bounds.y = y + scrollOffset;
            y -= spacing;
        }
    }

    public void draw(ShapeRenderer renderer, SpriteBatch batch, BitmapFont font) {
        // Draw background
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(0.2f, 0.2f, 0.2f, 1f); // dark gray background
        renderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        renderer.end();

        // Draw entries
        for (RecipeMenuEntry entry : entries) {
            if (entry.bounds.y + entry.bounds.height < bounds.y) continue;
            if (entry.bounds.y > bounds.y + bounds.height) continue;

            entry.draw(renderer, batch, font);
        }

        // Draw border (optional)
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.setColor(1f, 1f, 1f, 1f); // white border
        renderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        renderer.end();
    }


    /**
     *
     * Check if a point is inside the scrollbox
     */
    public boolean contains(float x, float y) {
        return bounds.contains(x, y);
    }

    /**
     * Forward clicks to entries
     */
    public RecipeMenuEntry  click(float x, float y) {
        for (RecipeMenuEntry entry : entries) {
            if (entry.bounds.contains(x, y)) {
                return entry;
            }
        }
        return null;
    }

    public void setContentHeight(float height) {
        this.contentHeight = height;
    }

    public void relayout() {
        updateEntryPositions();
    }
}
