package com.Geary.towerdefense.UI.displays.modal.tower;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

import java.util.List;

public class InfoBoxRenderer {

    private final BitmapFont font;
    private final GlyphLayout layout = new GlyphLayout();
    private final Rectangle selectButtonBounds = new Rectangle();

    public InfoBoxRenderer(BitmapFont font) {
        this.font = font;
    }

    public void draw(
        ShapeRenderer renderer,
        SpriteBatch batch,
        Rectangle boxBounds,
        List<String> lines,
        boolean showSelectButton
    ) {
        // Background
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(0.14f, 0.14f, 0.14f, 1f);
        renderer.rect(
            boxBounds.x,
            boxBounds.y,
            boxBounds.width,
            boxBounds.height
        );
        renderer.end();

        if (lines == null || lines.isEmpty()) {
            return;
        }

        // Text
        batch.begin();
        float y = boxBounds.y + boxBounds.height - 20;
        for (String line : lines) {
            font.draw(batch, line, boxBounds.x + 10, y);
            y -= 25;
        }
        batch.end();

        if (showSelectButton) {
            drawSelectButton(renderer, batch, boxBounds);
        }
    }

    private void drawSelectButton(
        ShapeRenderer renderer,
        SpriteBatch batch,
        Rectangle boxBounds
    ) {
        float btnW = boxBounds.width * 0.5f;
        float btnH = 32f;
        float btnX = boxBounds.x + (boxBounds.width - btnW) / 2f;
        float btnY = boxBounds.y + 10f;

        selectButtonBounds.set(btnX, btnY, btnW, btnH);

        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(0.2f, 0.4f, 0.2f, 1f);
        renderer.rect(btnX, btnY, btnW, btnH);
        renderer.end();

        batch.begin();
        layout.setText(font, "Select");
        font.draw(
            batch,
            layout,
            btnX + (btnW - layout.width) / 2f,
            btnY + (btnH + layout.height) / 2f - 4
        );
        batch.end();
    }

    public Rectangle getSelectButtonBounds() {
        return selectButtonBounds;
    }
}
