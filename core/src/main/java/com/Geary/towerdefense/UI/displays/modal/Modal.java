package com.Geary.towerdefense.UI.modal;

import com.Geary.towerdefense.UI.displays.modal.ModalButton;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public abstract class Modal {

    protected final BitmapFont font;
    protected final Rectangle bounds = new Rectangle();
    private boolean closeRequested = false;
    protected final List<ModalButton> buttons = new ArrayList<>();

    public Modal(BitmapFont font) {
        this.font = font;
    }

    /** Called every frame to handle modal logic */
    public void update() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            requestClose();
        }
    }

    /** Layout the modal in screen space (pixels) */
    public void layout() {
        float modalWidth = 500;
        float modalHeight = 450;

        float modalX = Gdx.graphics.getWidth() / 2f - modalWidth / 2f;
        float modalY = (Gdx.graphics.getHeight() / 2f - modalHeight / 2f)*1.3f;//push it a bit above the ui bar

        bounds.set(modalX, modalY, modalWidth, modalHeight);

        // let subclass position buttons / scrollboxes relative to bounds
        layoutButtons();
    }

    /** Subclasses override to layout buttons and internal content */
    protected void layoutButtons() {
    }

    /** Draws the modal */
    public void draw(ShapeRenderer shapeRenderer, SpriteBatch batch) {

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0f, 0f, 0f, 0.25f);
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.end();

        // modal panel
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.12f, 0.12f, 0.12f, 1f);
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        shapeRenderer.end();

        // draw buttons
        for (ModalButton button : buttons) {
            button.draw(shapeRenderer);
        }

        Gdx.gl.glDisable(GL20.GL_BLEND);

        drawContent(shapeRenderer, batch);
    }

    /** Convert screen-space click into modal-space check */
    public boolean handleClick(float screenX, float screenY) {
        Vector2 click = new Vector2(screenX, Gdx.graphics.getHeight() - screenY); // invert Y

        if (!bounds.contains(click.x, click.y)) {
            requestClose();
            return true; // click outside modal consumed
        }

        // check buttons
        for (ModalButton button : buttons) {
            if (button.click(click.x, click.y)) return true;
        }

        return handleClickInside(click.x, click.y);
    }

    /** Scroll event in modal space */
    public boolean handleScroll(float amountY) {
        int mouseY = Gdx.graphics.getHeight() - Gdx.input.getY();
        Vector2 mouse = new Vector2(Gdx.input.getX(), mouseY);

        if (bounds.contains(mouse)) {
            handleScrollInside(mouse.x, mouse.y, amountY);
            return true;
        }

        return false;
    }

    /** Subclasses override to scroll their content */
    protected boolean handleScrollInside(float x, float y, float amountY) {
        return false;
    }

    protected abstract void drawContent(ShapeRenderer shapeRenderer, SpriteBatch batch);

    protected abstract boolean handleClickInside(float x, float y);

    public boolean shouldClose() {
        return closeRequested;
    }

    public void requestClose() {
        closeRequested = true;
    }
}
