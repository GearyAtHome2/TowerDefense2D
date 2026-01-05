package com.Geary.towerdefense.UI;

import com.Geary.towerdefense.behaviour.buildings.manager.TowerManager;
import com.Geary.towerdefense.behaviour.buildings.manager.TransportManager;
import com.Geary.towerdefense.world.GameWorld;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameUI {

    private static final int UI_BAR_HEIGHT = 90;
    private static final float PLACE_TOWER_X = 80;
    private static final float PLACE_TOWER_Y = 10;
    private static final float PLACE_TOWER_WIDTH = 150;
    private static final float PLACE_TOWER_HEIGHT = 40;

    private final ShapeRenderer shapeRenderer;
    private final SpriteBatch batch;
    private final BitmapFont font;
    private final Viewport uiViewport;
    private final GameWorld world;
    private final TowerManager towerManager;
    private final TransportManager transportManager;

    public GameUI(ShapeRenderer shapeRenderer, SpriteBatch batch, BitmapFont font,
                  Viewport uiViewport, GameWorld world, TowerManager towerManager, TransportManager transportManager) {
        this.shapeRenderer = shapeRenderer;
        this.batch = batch;
        this.font = font;
        this.uiViewport = uiViewport;
        this.world = world;
        this.towerManager = towerManager;
        this.transportManager = transportManager;
    }

    public void drawUI(boolean paused, float gameSpeed) {
        uiViewport.apply();

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
        shapeRenderer.rect(PLACE_TOWER_X, PLACE_TOWER_Y, PLACE_TOWER_WIDTH, PLACE_TOWER_HEIGHT);
        shapeRenderer.end();

        // --- Transport active sign ---
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(transportManager.isPlacementActive() ? 0f : 0.3f, 0.6f, 0.3f, 1f);
        shapeRenderer.rect(PLACE_TOWER_X + PLACE_TOWER_WIDTH + 20, PLACE_TOWER_Y, PLACE_TOWER_WIDTH/2, PLACE_TOWER_HEIGHT);
        shapeRenderer.end();

        batch.begin();
        font.draw(batch, "Place Tower", PLACE_TOWER_X + 15, PLACE_TOWER_Y + 30);
        font.draw(batch, "Transport", PLACE_TOWER_X + PLACE_TOWER_WIDTH + 35, PLACE_TOWER_Y + 30);
        font.draw(batch, "ESC = Pause", 20, UI_BAR_HEIGHT - 20);
        font.draw(batch, "Towers: " + world.towers.size(), 200, UI_BAR_HEIGHT - 20);
        font.draw(batch, "Enemies: " + world.enemies.size(), 400, UI_BAR_HEIGHT - 20);
        font.draw(batch, "gamespeed: " + gameSpeed, 400, UI_BAR_HEIGHT - 50);

        if (paused) {
            font.getData().setScale(2.5f);
            font.draw(batch, "PAUSED", uiViewport.getWorldWidth() / 2f - 70, uiViewport.getWorldHeight() / 2f);
            font.getData().setScale(1.5f);
        }

        batch.end();
    }
}
