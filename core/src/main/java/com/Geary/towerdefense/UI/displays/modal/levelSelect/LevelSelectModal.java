package com.Geary.towerdefense.UI.displays.modal.levelSelect;

import com.Geary.towerdefense.levelSelect.levels.LevelData;
import com.Geary.towerdefense.levelSelect.levels.LevelData.EnemySpawnInfo;
import com.Geary.towerdefense.UI.displays.modal.Modal;
import com.Geary.towerdefense.entity.resources.Resource;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class LevelSelectModal extends Modal {

    private final LevelData levelData;
    private final GlyphLayout layout = new GlyphLayout();

    public LevelSelectModal(LevelData levelData, BitmapFont font, OrthographicCamera camera) {
        super(font, camera);
        this.levelData = levelData;
        System.out.println("Opened LevelSelectModal for: " + levelData.getId());
    }

    @Override
    protected void drawContent(ShapeRenderer shapeRenderer, SpriteBatch batch) {
        float padding = 20f;
        float contentWidth = bounds.width - 2 * padding;
        float contentHeight = bounds.height - 2 * padding;

        // --- Draw background ---
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(new Color(0.1f, 0.1f, 0.1f, 1f));
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);

        // Right-side square for texture or placeholder
        float squareSize = Math.min(contentWidth * 0.4f, contentHeight); // ensure square
        float textureX = bounds.x + bounds.width - padding - squareSize;
        float textureY = bounds.y + padding;

        shapeRenderer.setColor(new Color(0.2f, 0.2f, 0.2f, 1f));
        shapeRenderer.rect(textureX, textureY, squareSize, squareSize);
        shapeRenderer.end();

        // --- Draw text and texture ---
        batch.begin();
        font.getData().setScale(1.6f);

        // Title
        String title = "Level: " + levelData.getDisplayName();
        layout.setText(font, title);
        float leftX = bounds.x + padding;
        float currentY = bounds.y + bounds.height - padding;
        font.draw(batch, layout, leftX, currentY);

        font.getData().setScale(1f);
        currentY -= layout.height + 10f;

        // Tier
        String tierText = "Tier: " + levelData.getTier();
        layout.setText(font, tierText);
        font.draw(batch, layout, leftX, currentY);
        currentY -= layout.height + 10f;

        // Resources
        layout.setText(font, "Resources:");
        font.draw(batch, layout, leftX, currentY);
        currentY -= layout.height + 5f;
        for (Resource.RawResourceType type : levelData.getResourceAllocation().keySet()) {
            String resText = type.name() + ": " + levelData.getResourceAllocation().get(type);
            layout.setText(font, resText);
            font.draw(batch, resText, leftX + 10f, currentY);
            currentY -= layout.height + 3f;
        }
        currentY -= 10f;

        // Enemies
        layout.setText(font, "Enemies:");
        font.draw(batch, layout, leftX, currentY);
        currentY -= layout.height + 5f;
        for (EnemySpawnInfo enemy : levelData.getEnemies()) {
            String enemyText = enemy.getEnemyType();
            layout.setText(font, enemyText);
            font.draw(batch, enemyText, leftX + 10f, currentY);
            currentY -= layout.height + 3f;
        }

        // Draw texture if present; otherwise, placeholder square is already drawn
        TextureRegion tex = levelData.getTexture();
        if (tex != null) {
            batch.draw(tex, textureX, textureY, squareSize, squareSize);
        }

        batch.end();
    }

    @Override
    protected boolean handleClickInside(float x, float y) {
        return false;
    }

    public LevelData getLevelData() {
        return levelData;
    }

}
