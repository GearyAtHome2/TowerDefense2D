package com.Geary.towerdefense.UI.displays.modal.levelSelect;

import com.Geary.towerdefense.levelSelect.levels.LevelData;
import com.Geary.towerdefense.UI.displays.modal.Modal;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class LevelSelectModal extends Modal {

    private final LevelData levelData;
    private final GlyphLayout layout = new GlyphLayout();

    public LevelSelectModal(
            LevelData levelData,
            BitmapFont font,
            OrthographicCamera camera
    ) {
        super(font, camera);
        this.levelData = levelData;

        System.out.println("Opened LevelSelectModal for: " + levelData.getId());
    }

    @Override
    protected void drawContent(ShapeRenderer shapeRenderer, SpriteBatch batch) {

        // Draw basic placeholder title
        batch.begin();
        font.getData().setScale(1.6f);

        String title = "Level: " + levelData.getDisplayName();
        layout.setText(font, title);

        float textX = bounds.x + bounds.width / 2f - layout.width / 2f;
        float textY = bounds.y + bounds.height - 40f;

        font.draw(batch, layout, textX, textY);

        font.getData().setScale(1f);
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
