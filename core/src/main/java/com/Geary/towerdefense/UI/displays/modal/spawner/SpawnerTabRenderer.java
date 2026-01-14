package com.Geary.towerdefense.UI.displays.modal.spawner;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

import java.util.List;
import java.util.Map;

public class SpawnerTabRenderer<T extends Enum<T>> {

    private final BitmapFont font;
    private final OrthographicCamera camera;

    public SpawnerTabRenderer(BitmapFont font, OrthographicCamera camera) {
        this.font = font;
        this.camera = camera;
    }

    public void draw(
        ShapeRenderer renderer,
        SpriteBatch batch,
        Rectangle bounds,
        float tabHeightFrac,
        List<T> tabs,
        int activeTabIndex,
        Map<T, Color> tabColors
    ) {
        float tabHeight = bounds.height * tabHeightFrac;
        float tabWidth = bounds.width / tabs.size();
        float y = bounds.y + bounds.height - tabHeight;

        /* -------- background -------- */

        renderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < tabs.size(); i++) {
            T tab = tabs.get(i);
            Color base = tabColors.get(tab);
            Color draw = new Color(base);

            if (i == activeTabIndex) {
                if (tab.name().equals("LIGHT")) {
                    draw.set(Color.WHITE);
                } else {
                    draw.lerp(Color.WHITE, 0.3f);
                }
            }

            renderer.setColor(draw);
            renderer.rect(bounds.x + i * tabWidth, y, tabWidth, tabHeight);
        }
        renderer.end();

        /* -------- labels -------- */

        batch.begin();
        for (int i = 0; i < tabs.size(); i++) {
            T tab = tabs.get(i);
            String text = tab.name();

            GlyphLayout layout = new GlyphLayout(font, text);
            float scale = Math.min(1f, (tabWidth - 10f) / layout.width);
            font.getData().setScale(scale);

            Color base = tabColors.get(tab);
            float brightness =
                0.299f * base.r +
                    0.587f * base.g +
                    0.114f * base.b;

            font.setColor(brightness > 0.6f ? Color.BLACK : Color.WHITE);

            float textX =
                bounds.x + i * tabWidth +
                    (tabWidth - layout.width * scale) / 2f;
            float textY = y + tabHeight * 0.7f;

            font.draw(batch, text, textX, textY);
        }
        batch.end();
        font.getData().setScale(1f);
    }
}
