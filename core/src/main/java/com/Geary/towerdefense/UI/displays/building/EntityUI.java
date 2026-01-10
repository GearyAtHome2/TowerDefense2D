package com.Geary.towerdefense.UI.displays.building;

import com.Geary.towerdefense.entity.Entity;
import com.Geary.towerdefense.entity.mob.Mob;
import com.Geary.towerdefense.world.GameWorld;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.List;

public class EntityUI {

    protected final GameWorld world;
    protected final ShapeRenderer shapeRenderer;
    protected final SpriteBatch batch;
    protected final BitmapFont font;

    private float popupScale = 1f;
    private final Rectangle deleteButtonBounds = new Rectangle();
    private boolean deleteClickedThisFrame = false;

    protected final List<BuildingUIButton> extraButtons = new ArrayList<>();

    // cursor for stacking buttons vertically
    protected float layoutCursorY;

    public EntityUI(GameWorld world, ShapeRenderer shapeRenderer, SpriteBatch batch, BitmapFont font) {
        this.world = world;
        this.shapeRenderer = shapeRenderer;
        this.batch = batch;
        this.font = font;
    }

    // ===================== DRAW =====================
    public void drawPopup(Entity entity, float worldCameraZoom) {
        if (entity == null) return;
        float baseWidth = 140;
        float baseHeight = 120;
        float padding = 8;
        float scale = getPopupScale(worldCameraZoom);

        float scaledWidth = baseWidth * scale;
        float minHeight = baseHeight * scale;
        float rowHeight = 24 * scale;

        float x;
        float y;
        if (entity instanceof Mob){
            x = entity.xPos+((Mob) entity).size;
            y = entity.yPos+((Mob) entity).size;
        } else {
            x = entity.xPos + world.cellSize + 5;
            y = entity.yPos + world.cellSize;
        }

        // ---- LAYOUT START ----
        float deleteButtonHeight = 20 * scale;
        layoutCursorY = y + padding + deleteButtonHeight + 6 * scale;

        // Clear + rebuild extra buttons
        extraButtons.clear();
        addExtraButtons(entity, x, y, scaledWidth, minHeight, scale);

        // ---- POPUP HEIGHT ----
        float contentHeight = layoutCursorY - y + padding;
        float finalHeight = Math.max(minHeight, contentHeight);

        // ---- POPUP BACKGROUND ----
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0f, 0f, 0f, 0.8f);
        shapeRenderer.rect(x, y, scaledWidth, finalHeight);
        shapeRenderer.end();

        // ---- TEXT ----
        batch.begin();
        float originalScaleX = font.getData().scaleX;
        float originalScaleY = font.getData().scaleY;

        font.setColor(entity.getInfoTextColor());
        font.getData().setScale(originalScaleX * scale * 1.3f, originalScaleY * scale * 1.3f);

        List<String> infoLines = entity.getInfoLines();
        float textTopY = y + minHeight - padding * scale;

        for (int i = 0; i < infoLines.size(); i++) {
            System.out.println("Attempting to draw line: "+infoLines.get(i));
            font.draw(batch, infoLines.get(i), x + padding * scale, textTopY - i * rowHeight);
        }
        batch.end();

        // ---- DELETE BUTTON ----
        drawDeleteButton(x, y, scaledWidth, scale);

        // ---- EXTRA BUTTONS ----
        drawExtraButtons();

        // Reset font
        font.getData().setScale(originalScaleX, originalScaleY);
        font.setColor(Color.WHITE);
    }

    // ===================== BUTTONS =====================
    protected void drawDeleteButton(float x, float y, float width, float scale) {
        float padding = 8 * scale;
        float height = 20 * scale;
        float buttonX = x + padding;
        float buttonY = y + padding;
        float buttonWidth = width - padding * 2;

        deleteButtonBounds.set(buttonX, buttonY, buttonWidth, height);

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.8f, 0f, 0f, 1f);
        shapeRenderer.rect(buttonX, buttonY, buttonWidth, height);
        shapeRenderer.end();

        batch.begin();
        font.draw(batch, "Delete", buttonX + 4 * scale, buttonY + height - 4 * scale);
        batch.end();
    }

    /** Subclasses override this */
    protected void addExtraButtons(Entity entity, float popupX, float popupY, float popupWidth, float popupHeight, float scale) {
        // default: none
    }

    /** CURSOR-BASED STACKING (restores FactoryUI support) */
    protected void addStackedButton(String label, float popupX, float popupWidth, float scale, float r, float g, float b, Runnable onClick) {
        float padding = 8 * scale;
        float height = 20 * scale;
        float width = popupWidth - padding * 2;

        BuildingUIButton button = new BuildingUIButton(label, r, g, b, onClick);
        button.bounds.set(popupX + padding, layoutCursorY, width, height);

        layoutCursorY += height + 6 * scale;
        extraButtons.add(button);
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

    // ===================== INPUT =====================
    public void handleClick(float screenX, float screenY, OrthographicCamera worldCamera) {
        Vector3 worldClick = new Vector3(screenX, screenY, 0);
        worldCamera.unproject(worldClick);

        if (deleteButtonBounds.contains(worldClick.x, worldClick.y)) deleteClickedThisFrame = true;

        for (BuildingUIButton button : extraButtons) button.handleClick(worldClick.x, worldClick.y);
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

    // ===================== HELPERS =====================
    protected float getPopupScale(float zoom) {
        float target = 1f + (zoom - 1f) * 0.65f;
        target = MathUtils.clamp(target, 0.5f, 3f);
        popupScale = MathUtils.lerp(popupScale, target, 0.1f);
        return popupScale;
    }

    // ===================== BUTTON CLASS =====================
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
            if (bounds.contains(x, y) && onClick != null) onClick.run();
        }
    }
}
