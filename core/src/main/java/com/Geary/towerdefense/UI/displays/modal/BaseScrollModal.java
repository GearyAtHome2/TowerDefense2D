package com.Geary.towerdefense.UI.displays.modal;

import com.Geary.towerdefense.UI.displays.modal.scrollbox.HorizontalScrollBox;
import com.Geary.towerdefense.UI.displays.modal.scrollbox.VerticalScrollBox;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseScrollModal extends Modal {

    protected final List<Object> scrollBoxes = new ArrayList<>();

    protected BaseScrollModal(BitmapFont font, OrthographicCamera camera) {
        super(font, camera);
    }

    protected void registerScrollBox(Object box) {
        scrollBoxes.add(box);
    }

    @Override
    protected boolean handleScrollInside(float x, float y, float amountY) {
        for (Object box : scrollBoxes) {
            if (box instanceof VerticalScrollBox<?> v && v.contains(x, y)) {
                v.scroll(amountY * 10f);
                return true;
            }
            if (box instanceof HorizontalScrollBox<?> h && h.contains(x, y)) {
                h.scroll(amountY * 10f);
                return true;
            }
        }
        return false;
    }

    protected void drawScrollBoxes(
        ShapeRenderer renderer,
        SpriteBatch batch
    ) {
        for (Object box : scrollBoxes) {
            if (box instanceof VerticalScrollBox<?> v)
                v.draw(renderer, batch, font, camera);

            if (box instanceof HorizontalScrollBox<?> h)
                h.draw(renderer, batch, font, camera);
        }
    }
}
