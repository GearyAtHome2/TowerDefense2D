package com.Geary.towerdefense.UI.render.icons;

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

        // Draw background first
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0f, 0f, 0f, 0.8f);
        shapeRenderer.rect(x, y, width, height);
        shapeRenderer.end();

        // Draw text on top
        batch.begin();
        font.draw(batch, layout, x + padding, y + height - padding); // top-left origin for text
        batch.end();
    }
}
