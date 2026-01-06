package com.Geary.towerdefense.UI.displays.building.specialized;

import com.Geary.towerdefense.entity.buildings.Factory;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class FactoryMenu {

    private final Factory factory;
    private final Rectangle bounds = new Rectangle();
    private boolean closeRequested = false;

    public FactoryMenu(Factory factory) {
        this.factory = factory;
    }

    public void update() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            requestClose();
        }
    }

    public void layout(OrthographicCamera worldCamera) {
        float zoom = worldCamera.zoom;

        float baseWidth = worldCamera.viewportWidth * 0.6f;
        float baseHeight = worldCamera.viewportHeight * 0.6f;

        float scaledWidth = baseWidth * zoom;
        float scaledHeight = baseHeight * zoom;

        bounds.set(
            worldCamera.position.x - scaledWidth / 2f,
            worldCamera.position.y - scaledHeight / 2f,
            scaledWidth,
            scaledHeight
        );
    }

    public void draw(ShapeRenderer shapeRenderer, SpriteBatch batch, OrthographicCamera worldCamera) {

        float viewWidth = worldCamera.viewportWidth * worldCamera.zoom;
        float viewHeight = worldCamera.viewportHeight * worldCamera.zoom;

        float viewX = worldCamera.position.x - viewWidth / 2f;
        float viewY = worldCamera.position.y - viewHeight / 2f;

        // Enable alpha blending
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        // Translucent overlay
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0f, 0f, 0f, 0.25f);
        shapeRenderer.rect(viewX, viewY, viewWidth, viewHeight);
        shapeRenderer.end();

        // Disable blending if you want to be strict
        Gdx.gl.glDisable(GL20.GL_BLEND);

        // Menu panel (opaque)
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.12f, 0.12f, 0.12f, 1f);
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        shapeRenderer.end();
    }

    /**
     * @return true if the click was consumed
     */
    public boolean handleClick(float screenX, float screenY, OrthographicCamera worldCamera) {
        Vector3 worldClick = new Vector3(screenX, screenY, 0);
        worldCamera.unproject(worldClick);

        // Click anywhere while menu is open is consumed
        if (!bounds.contains(worldClick.x, worldClick.y)) {
            requestClose();
            return true; // consume clicks outside too
        }

        // TODO: button hit detection here

        return true;
    }

    public boolean shouldClose() {
        return closeRequested;
    }

    public void requestClose() {
        closeRequested = true;
    }
}
