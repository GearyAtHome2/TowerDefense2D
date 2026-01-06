package com.Geary.towerdefense.UI;

import com.Geary.towerdefense.behaviour.buildings.manager.TowerManager;
import com.Geary.towerdefense.behaviour.buildings.manager.TransportManager;
import com.Geary.towerdefense.entity.resources.Resource;
import com.Geary.towerdefense.world.GameStateManager;
import com.Geary.towerdefense.world.GameWorld;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.EnumMap;

public class GameUI {

    public static final int UI_BAR_HEIGHT = 90;

    // Button size relative to UI_BAR_HEIGHT
    private static final float DEFAULT_BUTTON_WIDTH = 0.18f; // % of viewport width
    private static final float BUTTON_HEIGHT = 0.3f * UI_BAR_HEIGHT;
    private static final float BUTTON_SPACING = 20f;

    private static final float RESOURCE_SPACING = 120; // horizontal space between resources

    private final ShapeRenderer shapeRenderer;
    private final SpriteBatch batch;
    private final BitmapFont font;
    private final Viewport uiViewport;
    private final GameWorld world;
    private final TowerManager towerManager;
    private final TransportManager transportManager;
    private final GameStateManager gameStateManager;

    private Rectangle placeTowerButtonBounds;
    private Rectangle transportButtonBounds;

    public GameUI(ShapeRenderer shapeRenderer, SpriteBatch batch, BitmapFont font,
                  Viewport uiViewport, GameWorld world, TowerManager towerManager, TransportManager transportManager, GameStateManager gameStateManager) {
        this.shapeRenderer = shapeRenderer;
        this.batch = batch;
        this.font = font;
        this.uiViewport = uiViewport;
        this.world = world;
        this.towerManager = towerManager;
        this.transportManager = transportManager;
        this.gameStateManager = gameStateManager;

        updateButtonBounds();
    }

    private void updateButtonBounds() {
        float viewportWidth = uiViewport.getWorldWidth();

        float placeTowerX = BUTTON_SPACING;
        float placeTowerY = (UI_BAR_HEIGHT - BUTTON_HEIGHT) / 2f;

        float placeTowerWidthPixels = viewportWidth * DEFAULT_BUTTON_WIDTH;
        float transportWidthPixels = viewportWidth * DEFAULT_BUTTON_WIDTH *  1.22f;

        placeTowerButtonBounds = new Rectangle(placeTowerX, placeTowerY, placeTowerWidthPixels, BUTTON_HEIGHT);
        transportButtonBounds = new Rectangle(placeTowerX + placeTowerWidthPixels + BUTTON_SPACING, placeTowerY, transportWidthPixels, BUTTON_HEIGHT);
    }

    public void drawUI(boolean paused, float gameSpeed) {
        uiViewport.apply();
        updateButtonBounds();

        shapeRenderer.setProjectionMatrix(uiViewport.getCamera().combined);
        batch.setProjectionMatrix(uiViewport.getCamera().combined);

        // --- UI bar ---
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.1f, 0.1f, 0.1f, 1f);
        shapeRenderer.rect(0, 0, uiViewport.getWorldWidth(), UI_BAR_HEIGHT);
        shapeRenderer.end();

        // --- Tower button ---
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(towerManager.isPlacementActive() ? 0f : 0.3f, 0.6f, 0.3f, 1f);
        shapeRenderer.rect(placeTowerButtonBounds.x, placeTowerButtonBounds.y, placeTowerButtonBounds.width, placeTowerButtonBounds.height);
        shapeRenderer.end();

        // --- Transport active sign ---
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(transportManager.isPlacementActive() ? 0f : 0.3f, 0.6f, 0.3f, 1f);
        shapeRenderer.rect(transportButtonBounds.x, transportButtonBounds.y, transportButtonBounds.width, transportButtonBounds.height);
        shapeRenderer.end();

        batch.begin();
        font.draw(batch, "Place Tower", placeTowerButtonBounds.x + 15, placeTowerButtonBounds.y + placeTowerButtonBounds.height / 2 + BUTTON_HEIGHT/4);
        font.draw(batch, "Place Transport", transportButtonBounds.x + 10, transportButtonBounds.y + transportButtonBounds.height / 2 + BUTTON_HEIGHT/4);

        font.draw(batch, "ESC = Pause", 10, UI_BAR_HEIGHT - 10);
        font.draw(batch, "gamespeed: " + gameSpeed, 400, UI_BAR_HEIGHT - 50);

        drawResources(batch, font, gameStateManager.getResourceCount());

        if (paused) {
            font.getData().setScale(2.5f);
            font.draw(batch, "PAUSED", uiViewport.getWorldWidth() / 2f - 70, uiViewport.getWorldHeight() / 2f);
            font.getData().setScale(1.5f);
        }

        batch.end();
    }

    private void drawResources(SpriteBatch batch, BitmapFont font, EnumMap<Resource.ResourceType, Float> resources) {
        if (resources == null || resources.isEmpty()) return;

        float x = uiViewport.getWorldWidth() - RESOURCE_SPACING; // start from right
        float y = UI_BAR_HEIGHT - 30;

        for (Resource.ResourceType type : resources.keySet()) {
            String text = type.name() + ": " + (int)(float)resources.get(type);
            float textWidth = font.getRegion().getRegionWidth(); // approximate width if needed
            font.draw(batch, text, x - text.length() * 8, y); // shift left by estimated width
            x -= RESOURCE_SPACING;
        }
    }

    public boolean handleUiClick(Vector3 uiClick) {
        if (placeTowerButtonBounds.contains(uiClick.x, uiClick.y)) {
            towerManager.togglePlacementClick(
                uiClick,
                placeTowerButtonBounds.x,
                placeTowerButtonBounds.y,
                placeTowerButtonBounds.width,
                placeTowerButtonBounds.height
            );
            return true;
        }

        if (transportButtonBounds.contains(uiClick.x, uiClick.y)) {
            transportManager.togglePlacementClick();
            return true;
        }
        return false;
    }
}
