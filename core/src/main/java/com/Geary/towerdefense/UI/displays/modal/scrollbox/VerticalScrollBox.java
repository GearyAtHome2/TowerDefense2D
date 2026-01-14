package com.Geary.towerdefense.UI.displays.modal.scrollbox;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;

import java.util.ArrayList;
import java.util.List;

public class VerticalScrollBox<T extends ScrollEntry> {

    public final Rectangle bounds = new Rectangle();
    public float scrollOffset = 0f;
    private float contentHeight = 0f;

    public final List<T> entries = new ArrayList<>();

    // New: background color for scrollbox
    private float bgR = 0.2f, bgG = 0.2f, bgB = 0.2f, bgA = 1f;

    public VerticalScrollBox(float x, float y, float width, float height) {
        bounds.set(x, y, width, height);
    }

    public void setBackgroundColor(float r, float g, float b, float a) {
        bgR = r; bgG = g; bgB = b; bgA = a;
    }

    public void setEntries(List<T> entries, float totalHeight) {
        this.entries.clear();
        this.entries.addAll(entries);
        this.contentHeight = totalHeight;
        scrollOffset = 0;
        updateEntryPositions();
    }

    public void scroll(float deltaY) {
        scrollOffset += deltaY;

        float maxOffset = Math.max(0, contentHeight - bounds.height);
        if (scrollOffset < 0) scrollOffset = 0;
        if (scrollOffset > maxOffset) scrollOffset = maxOffset;

        updateEntryPositions();
    }

    private void updateEntryPositions() {
        float y = bounds.y + bounds.height;
        float spacing = 5f;

        for (T entry : entries) {
            entry.bounds().x = bounds.x + 10;
            entry.bounds().width = bounds.width - 20;

            y -= entry.bounds().height;
            entry.bounds().y = y + scrollOffset;
            y -= spacing;
        }
    }


    public void draw(ShapeRenderer renderer, SpriteBatch batch, BitmapFont font, Camera camera) {
        // Draw background
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(bgR, bgG, bgB, bgA);
        renderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        renderer.end();

        // Setup scissor rectangle
        Rectangle scissors = new Rectangle();
        ScissorStack.calculateScissors(
            camera,                  // Camera
            batch.getTransformMatrix(),
            bounds,
            scissors
        );

        batch.flush();
        if (ScissorStack.pushScissors(scissors)) {
            // Draw clipped content here
            for (T entry : entries) {
                entry.draw(renderer, batch, font, camera);
            }

            ScissorStack.popScissors();
        }

        // Draw border
        renderer.begin(ShapeRenderer.ShapeType.Line);
        renderer.setColor(1f, 1f, 1f, 1f);
        renderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        renderer.end();
        drawScrollIndicators(renderer);
    }

    private void drawScrollIndicators(ShapeRenderer renderer) {
        float arrowSize = 20f;
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(1f, 1f, 1f, 0.6f); // Semi-transparent white

        float arrowY = bounds.y+bounds.height*0.98f;
        float arrowX = bounds.x + bounds.width/2;

        // Left Arrow (Show if we have scrolled right at all)
        if (scrollOffset > 0) {
            renderer.triangle(
                arrowX, arrowY + arrowSize / 2f,
                arrowX + arrowSize / 2f, arrowY,
                arrowX  - arrowSize / 2f, arrowY
            );
        }
        arrowY = bounds.y+bounds.height*0.02f;
        // Right Arrow (Show if there is more content to the right)
        if (scrollOffset < contentHeight - bounds.height - 1f) { // -1f to avoid float precision flickering
            renderer.triangle(
                arrowX, arrowY - arrowSize / 2f,
                arrowX + arrowSize / 2f, arrowY,
                arrowX - arrowSize / 2f, arrowY
            );
        }
        renderer.end();
    }

    public boolean contains(float x, float y) {
        return bounds.contains(x, y);
    }

    public T click(float x, float y) {
        for (T entry : entries) {
            if (entry.click(x, y)) return entry;
        }
        return null;
    }

    public void setContentHeight(float height) {
        this.contentHeight = height;
    }

    public void relayout() {
        updateEntryPositions();
    }

    public List<T> getEntries() {
        return entries;
    }
}
