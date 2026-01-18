package com.Geary.towerdefense.UI.gameUI.scrollbox;

import com.Geary.towerdefense.UI.displays.modal.scrollbox.ScrollEntry;
import com.Geary.towerdefense.entity.buildings.Building;
import com.Geary.towerdefense.entity.mob.Mob;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class BuildListEntry implements ScrollEntry {

    public final Building building;
    public final Rectangle bounds = new Rectangle();
    public Runnable onClick;


    public BuildListEntry(Building building, float sizeX, float sizeY) {
        bounds.set(0, 0, sizeX, sizeY);
        this.building = building;
    }

    @Override
    public void draw(ShapeRenderer renderer, SpriteBatch batch, BitmapFont font, Camera camera) {
        // Draw background
        Color bg = computeBackgroundColor(building.order);

        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(bg);
        renderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        renderer.end();

        // Draw text scaled to width, at top
        batch.begin();

        String text = building.name;

        float originalScaleX = font.getScaleX();
        float originalScaleY = font.getScaleY();

        font.getData().setScale(1f);
        GlyphLayout layout = new GlyphLayout(font, text);

        float scaleX = (bounds.width - 6f) / layout.width; // 5px padding each side
        scaleX = Math.min(scaleX, 1f); // optional: prevent text from being larger than base size

        font.getData().setScale(scaleX, 1f);

        layout.setText(font, text);

        float padding = 3f;
        float textX = bounds.x + padding;
        float textY = bounds.y + bounds.height - 2*padding;

        font.draw(batch, layout, textX, textY);

        font.getData().setScale(originalScaleX, originalScaleY);

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

    public void setSize(float width, float height) {
        bounds.set(bounds.x, bounds.y, width, height);
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
}
