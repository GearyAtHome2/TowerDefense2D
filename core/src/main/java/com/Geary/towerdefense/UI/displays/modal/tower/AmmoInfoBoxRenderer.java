package com.Geary.towerdefense.UI.displays.modal.tower;

import com.Geary.towerdefense.entity.mob.bullet.BulletRepr;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class AmmoInfoBoxRenderer {

    private final BitmapFont font;
    private final GlyphLayout layout = new GlyphLayout();
    private final Rectangle selectButtonBounds = new Rectangle();

    public AmmoInfoBoxRenderer(BitmapFont font) {
        this.font = font;
    }

    public void draw(
        ShapeRenderer renderer,
        SpriteBatch batch,
        Rectangle modalBounds,
        float paddingRatio,
        float leftRatio,
        BulletScrollEntry selectedEntry
    ) {
        float pad = modalBounds.width * paddingRatio;
        float leftWidth = modalBounds.width * leftRatio;

        float boxX = modalBounds.x + pad;
        float boxY = modalBounds.y + modalBounds.height * 0.5f;
        float boxW = leftWidth;
        float boxH = modalBounds.height * 0.5f - pad * 2;

        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(0.14f, 0.14f, 0.14f, 1f);
        renderer.rect(boxX, boxY, boxW, boxH);
        renderer.end();

        if (selectedEntry == null) {
            return;
        }

        BulletRepr bullet = selectedEntry.getBullet();

        batch.begin();
        font.draw(batch, "Name: " + bullet.getName(), boxX + 10, boxY + boxH - 20);
        font.draw(batch, "Damage: " + bullet.getDamage(), boxX + 10, boxY + boxH - 45);
        font.draw(batch, "Speed: " + bullet.getSpeed(), boxX + 10, boxY + boxH - 70);
        batch.end();

        drawSelectButton(renderer, batch, boxX, boxY, boxW);
    }

    private void drawSelectButton(
        ShapeRenderer renderer,
        SpriteBatch batch,
        float boxX,
        float boxY,
        float boxW
    ) {
        float btnW = boxW * 0.5f;
        float btnH = 32f;
        float btnX = boxX + (boxW - btnW) / 2f;
        float btnY = boxY + 10f;

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
