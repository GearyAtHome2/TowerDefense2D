package com.Geary.towerdefense.UI.displays.modal.spawner;

import com.Geary.towerdefense.UI.modal.Modal;
import com.Geary.towerdefense.entity.spawner.FriendlySpawner;
import com.Geary.towerdefense.entity.spawner.Spawner;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class SpawnerModal extends Modal {

    private final Spawner spawner;

    // --- layout config (fractions of modal) ---
    private static class LayoutConfig {
        float selectionHeightRatio = 0.6f;
        float queueHeightRatio = 0.2f;
        float garrisonHeightRatio = 0.2f;

        float areaPaddingRatio = 0.03f; // padding inside each area
    }

    private final LayoutConfig layoutCfg = new LayoutConfig();

    // --- area bounds ---
    private final Rectangle selectionArea = new Rectangle();
    private final Rectangle queueArea = new Rectangle();
    private final Rectangle garrisonArea = new Rectangle();

    public SpawnerModal(FriendlySpawner spawner, BitmapFont font) {
        super(font);
        this.spawner = spawner;
    }

    @Override
    protected void layoutButtons() {
        layoutAreas();
    }

    private void layoutAreas() {
        float padding = bounds.width * layoutCfg.areaPaddingRatio;

        float y = bounds.y + bounds.height;

        // --- Selection area (top) ---
        float selectionHeight = bounds.height * layoutCfg.selectionHeightRatio;
        y -= selectionHeight;

        selectionArea.set(
            bounds.x + padding,
            y + padding,
            bounds.width - padding * 2,
            selectionHeight - padding * 2
        );

        // --- Queue area (middle) ---
        float queueHeight = bounds.height * layoutCfg.queueHeightRatio;
        y -= queueHeight;

        queueArea.set(
            bounds.x + padding,
            y + padding,
            bounds.width - padding * 2,
            queueHeight - padding * 2
        );

        // --- Garrison area (bottom) ---
        float garrisonHeight = bounds.height * layoutCfg.garrisonHeightRatio;
        y -= garrisonHeight;

        garrisonArea.set(
            bounds.x + padding,
            y + padding,
            bounds.width - padding * 2,
            garrisonHeight - padding * 2
        );
    }

    @Override
    protected void drawContent(ShapeRenderer shapeRenderer, SpriteBatch batch) {

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Selection area
        shapeRenderer.setColor(0.18f, 0.25f, 0.35f, 1f);
        shapeRenderer.rect(
            selectionArea.x,
            selectionArea.y,
            selectionArea.width,
            selectionArea.height
        );

        // Queue area
        shapeRenderer.setColor(0.25f, 0.25f, 0.18f, 1f);
        shapeRenderer.rect(
            queueArea.x,
            queueArea.y,
            queueArea.width,
            queueArea.height
        );

        // Garrison area
        shapeRenderer.setColor(0.25f, 0.18f, 0.18f, 1f);
        shapeRenderer.rect(
            garrisonArea.x,
            garrisonArea.y,
            garrisonArea.width,
            garrisonArea.height
        );

        shapeRenderer.end();
    }

    @Override
    protected boolean handleClickInside(float x, float y) {
        // No interaction yet
        return true;
    }
}
