package com.Geary.towerdefense.UI.displays.building;

import com.Geary.towerdefense.entity.buildings.Building;
import com.Geary.towerdefense.world.GameWorld;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.List;

public class BuildingUI {

    protected final GameWorld world;
    protected final ShapeRenderer shapeRenderer;
    protected final SpriteBatch batch;
    protected final BitmapFont font;

    private float popupScale = 1f;
    private Rectangle deleteButtonBounds = new Rectangle();
    private boolean deleteClickedThisFrame = false;

    // Extendable button list
    protected final List<BuildingUIButton> extraButtons = new ArrayList<>();

    public BuildingUI(GameWorld world, ShapeRenderer shapeRenderer, SpriteBatch batch, BitmapFont font) {
        this.world = world;
        this.shapeRenderer = shapeRenderer;
        this.batch = batch;
        this.font = font;
    }

    // ===================== Draw UI =====================
    public void drawBuildingPopup(Building building, float worldCameraZoom) {
        if (building == null) return;

        float baseWidth = 140;
        float baseHeight = 120;
        float padding = 8;
        float scale = getPopupScale(worldCameraZoom);

        float scaledWidth = baseWidth * scale;
        float scaledHeight = baseHeight * scale;
        float rowHeight = 24;

        float x = building.xPos + world.cellSize + 5;
        float y = building.yPos + world.cellSize;

        // Draw building highlight
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1f, 1f, 0f, 1f);
        shapeRenderer.rect(building.xPos, building.yPos, world.cellSize, world.cellSize);
        shapeRenderer.end();

        // Draw popup background
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0f, 0f, 0f, 0.8f);
        shapeRenderer.rect(x, y, scaledWidth, scaledHeight);
        shapeRenderer.end();

        // Draw info text
        batch.begin();
        float originalScaleX = font.getData().scaleX;
        float originalScaleY = font.getData().scaleY;
        font.setColor(building.getInfoTextColor());
        font.getData().setScale(originalScaleX * scale * 1.3f, originalScaleY * scale * 1.3f);

        List<String> infoLines = building.getInfoLines();
        for (int i = 0; i < infoLines.size(); i++) {
            font.draw(batch, infoLines.get(i), x + padding, y + scaledHeight - (i + 2) * rowHeight * scale);
        }
        batch.end();

        // Draw Delete button
        addBaseDeleteButton(x, y, scaledWidth, scale);

        // Draw extra buttons (defined by subclasses)
        addExtraButtons(building, x, y, scaledWidth, scaledHeight, scale);
        drawExtraButtons();

        // Reset font
        font.getData().setScale(originalScaleX, originalScaleY);
        font.setColor(Color.WHITE);
    }

    private void addBaseDeleteButton(float x, float y, float width, float scale) {
        float padding = 8;
        float buttonHeight = 20 * scale;
        float buttonWidth = width - padding * 2;
        float buttonX = x + padding;
        float buttonY = y + padding;

        deleteButtonBounds.set(buttonX, buttonY, buttonWidth, buttonHeight);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.8f, 0f, 0f, 1f);
        shapeRenderer.rect(buttonX, buttonY, buttonWidth, buttonHeight);
        shapeRenderer.end();

        batch.begin();
        font.draw(batch, "Delete", buttonX + 4, buttonY + buttonHeight - 4);
        batch.end();
    }

    /** Subclasses override this to add building-specific buttons */
    protected void addExtraButtons(Building building, float popupX, float popupY, float popupWidth, float popupHeight, float scale) {
        extraButtons.clear(); // default implementation: none
    }

    private void drawExtraButtons() {
        for (BuildingUIButton button : extraButtons) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(button.r, button.g, button.b, 1f);
            shapeRenderer.rect(button.bounds.x, button.bounds.y, button.bounds.width, button.bounds.height);
            shapeRenderer.end();

            batch.begin();
            font.draw(batch, button.label, button.bounds.x + 4, button.bounds.y + button.bounds.height - 4);
            batch.end();
        }
    }

    // ===================== Input =====================
    public void handleClick(float screenX, float screenY, OrthographicCamera worldCamera) {
        Vector3 worldClick = new Vector3(screenX, screenY, 0);
        worldCamera.unproject(worldClick);

        if (deleteButtonBounds.contains(worldClick.x, worldClick.y)) {
            deleteClickedThisFrame = true;
        }

        for (BuildingUIButton button : extraButtons) {
            button.handleClick(worldClick.x, worldClick.y);
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
        extraButtons.clear();
    }

    // ===================== Helpers =====================
    protected float getPopupScale(float zoom) {
        float target = 1f + (zoom - 1f) * 0.65f;
        target = Math.max(0.5f, Math.min(3f, target));
        popupScale = com.badlogic.gdx.math.MathUtils.lerp(popupScale, target, 0.1f);
        return popupScale;
    }

    // ===================== Inner Button Class =====================
    public static class BuildingUIButton {
        public final Rectangle bounds = new Rectangle();
        public final String label;
        public final Runnable onClick;
        public final float r, g, b;

        public BuildingUIButton(String label, float r, float g, float b, Runnable onClick) {
            this.label = label;
            this.onClick = onClick;
            this.r = r;
            this.g = g;
            this.b = b;
        }

        public void handleClick(float x, float y) {
            if (bounds.contains(x, y) && onClick != null) {
                onClick.run();
            }
        }
    }
}
