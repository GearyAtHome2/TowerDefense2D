package com.Geary.towerdefense.UI.render.icons;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class TooltipRenderer {

    private final BitmapFont font;
    private final GlyphLayout layout = new GlyphLayout();

    public TooltipRenderer(BitmapFont font) {
        this.font = font;
    }

    public void drawTooltip(SpriteBatch batch, ShapeRenderer shapeRenderer,
                            String text, float x, float y, float padding) {

        layout.setText(font, text);
        float width = layout.width + padding * 2;
        float height = layout.height + padding * 2;

        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();

        float drawX = x;
        float drawY = y;
        // Clamp right edge
        if (drawX + width > screenWidth) {
            drawX = screenWidth - width;
        }

        // Clamp top edge
        if (drawY + height > screenHeight) {
            drawY = screenHeight - height;
        }

        // Clamp left & bottom just in case
        drawX = Math.max(0, drawX);
        drawY = Math.max(0, drawY);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0f, 0f, 0f, 0.8f);
        shapeRenderer.rect(drawX, drawY, width, height);
        shapeRenderer.end();

        // Draw text on top
        batch.begin();
        font.draw(batch, layout,
            drawX + padding,
            drawY + height - padding); // text baseline
        batch.end();
    }
}
