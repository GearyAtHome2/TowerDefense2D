package com.Geary.towerdefense.UI;

import com.Geary.towerdefense.entity.buildings.Tower;
import com.Geary.towerdefense.world.GameWorld;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

public class TowerUI {

    private final GameWorld world;
    private final ShapeRenderer shapeRenderer;
    private final SpriteBatch batch;
    private final BitmapFont font;

    private float popupScale = 1f;
    private Rectangle deleteButtonBounds = new Rectangle();

    private boolean deleteClickedThisFrame = false;

    public TowerUI(GameWorld world, ShapeRenderer shapeRenderer, SpriteBatch batch, BitmapFont font) {
        this.world = world;
        this.shapeRenderer = shapeRenderer;
        this.batch = batch;
        this.font = font;
    }

    public void drawTowerPopup(Tower tower, float worldCameraZoom) {
        if (tower == null) return;

        float baseWidth = 140;
        float baseHeight = 120;
        float padding = 8;

        float scale = getPopupScale(worldCameraZoom);

        float scaledWidth = baseWidth * scale;
        float scaledHeight = baseHeight * scale;

        float rowHeight = 24;
        float x = tower.xPos + world.cellSize + 5;
        float y = tower.yPos + world.cellSize;

        // Highlight tower
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1f, 1f, 0f, 1f);
        shapeRenderer.rect(tower.xPos, tower.yPos, world.cellSize, world.cellSize);
        shapeRenderer.end();

        // Background
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0f, 0f, 0f, 0.8f);
        shapeRenderer.rect(x, y, scaledWidth, scaledHeight);
        shapeRenderer.end();

        // Draw text
        batch.begin();
        float originalScaleX = font.getData().scaleX;
        float originalScaleY = font.getData().scaleY;
        float fontScale = 0.8f + (scale - 1f) * 0.9f; // smaller response
        font.getData().setScale(originalScaleX * fontScale, originalScaleY * fontScale);

        font.draw(batch, "Tower", x + padding, y + scaledHeight - rowHeight * scale);
        font.draw(batch, "Cooldown: " + (int) Math.ceil(tower.cooldown * 10), x + padding, y + scaledHeight - 2 * rowHeight * scale);
        font.draw(batch, "Range: " + tower.range, x + padding, y + scaledHeight - 3 * rowHeight * scale);
        batch.end();

        // Draw Delete button
        float buttonHeight = 20 * scale;
        float buttonWidth = scaledWidth - padding * 2;
        float buttonX = x + padding;
        float buttonY = y + padding;

        deleteButtonBounds.set(buttonX, buttonY, buttonWidth, buttonHeight);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.8f, 0f, 0f, 1f); // red button
        shapeRenderer.rect(buttonX, buttonY, buttonWidth, buttonHeight);
        shapeRenderer.end();

        font.getData().setScale(originalScaleX, originalScaleY); // Reset font scale
    }

    private float getPopupScale(float zoom) {
        float target = 1f + (zoom - 1f) * 0.65f;
        target = Math.max(0.5f, Math.min(3f, target));
        popupScale = com.badlogic.gdx.math.MathUtils.lerp(popupScale, target, 0.1f);
        return popupScale;
    }

    public void handleClick(float screenX, float screenY, OrthographicCamera worldCamera) {
        Vector3 worldClick = new Vector3(screenX, screenY, 0);
        worldCamera.unproject(worldClick); // converts to world coordinates

        if (deleteButtonBounds.contains(worldClick.x, worldClick.y)) {
            deleteClickedThisFrame = true;
            System.out.println("Delete click recognised in world coords");
        }
    }

    public boolean consumeDeleteRequest() {
        if (deleteClickedThisFrame) {
            clear();
            return true;
        }
        return false;
    }

    public void clear() {
        deleteButtonBounds.set(0, 0, 0, 0);
        deleteClickedThisFrame = false;
    }
}
