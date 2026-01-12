package com.Geary.towerdefense.UI.displays.building;

import com.Geary.towerdefense.UI.GameUI;
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

public abstract class EntityUI {

    protected final GameWorld world;
    protected final ShapeRenderer shapeRenderer;
    protected final SpriteBatch batch;
    protected final BitmapFont font;

    private float popupScale = 1f;
    private final Rectangle deleteButtonBounds = new Rectangle();
    private boolean deleteClickedThisFrame = false;

    protected final List<UIButton> extraButtons = new ArrayList<>();
    protected float layoutCursorY;

    public EntityUI(GameWorld world, ShapeRenderer shapeRenderer, SpriteBatch batch, BitmapFont font) {
        this.world = world;
        this.shapeRenderer = shapeRenderer;
        this.batch = batch;
        this.font = font;
    }

    public void drawPopup(Entity entity, float worldCameraZoom) {
        if (entity == null) return;

        float baseWidth = 140;
        float baseHeight = 120;
        float padding = 8;
        float scale = getPopupScale(worldCameraZoom);

        float scaledWidth = baseWidth * scale;
        float minHeight = baseHeight * scale;
        float rowHeight = 24 * scale;

        float x = resolvePopupX(entity, scaledWidth);
        float preferredY = getPopupY(entity);

// ---- MEASURE PASS ----
        layoutCursorY = padding + 20 * scale + 6 * scale;
        extraButtons.clear();
        addExtraButtons(entity, x, 0, scaledWidth, minHeight, scale);

        float contentHeight = layoutCursorY + padding;
        float finalHeight = Math.max(minHeight, contentHeight);

// ---- RESOLVE Y ----
        float y = resolvePopupY(preferredY, finalHeight);

// ---- LAYOUT PASS ----
        float baseY = y + padding + 20 * scale + 6 * scale;
        float cursor = baseY;

        for (UIButton button : extraButtons) {
            button.bounds.y = cursor;
            cursor += button.bounds.height + 6 * scale;
        }
//        y = resolvePopupY(y, finalHeight);

        // Background
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0f, 0f, 0f, 0.8f);
        shapeRenderer.rect(x, y, scaledWidth, finalHeight);
        shapeRenderer.end();

        // Text
        batch.begin();
        float originalScaleX = font.getData().scaleX;
        float originalScaleY = font.getData().scaleY;

        font.setColor(entity.getInfoTextColor());
        font.getData().setScale(originalScaleX * scale * 1.3f, originalScaleY * scale * 1.3f);

        List<String> infoLines = entity.getInfoLines();
        float textTopY = y + minHeight - padding * scale;
        for (int i = 0; i < infoLines.size(); i++) {
            if (infoLines.get(i) != null) {
                font.draw(batch, infoLines.get(i), x + padding * scale, textTopY - i * rowHeight);
            }
        }
        batch.end();

        // Delete button
        if (shouldDrawDeleteButton(entity)) {
            drawDeleteButton(x, y, scaledWidth, scale);
        }

        drawExtraButtons();

        font.getData().setScale(originalScaleX, originalScaleY);
        font.setColor(Color.WHITE);
    }

    protected float getPopupX(Entity entity) {
        // default: next to entity (handles mobs too)
        return entity.xPos + (entity instanceof Mob mob ? mob.size : world.cellSize) + 5;
    }

    protected float getPopupY(Entity entity) {
        return entity.yPos + (entity instanceof Mob mob ? mob.size : world.cellSize);
    }

    protected abstract boolean shouldDrawDeleteButton(Entity entity);

    protected void addExtraButtons(Entity entity, float popupX, float unusedPopupY, float popupWidth, float popupHeight, float scale) {
        // override in subclasses
    }

    protected void addStackedButton(
        String label,
        float popupX,
        float popupWidth,
        float scale,
        float r, float g, float b,
        Runnable onClick
    ) {
        float padding = 8 * scale;
        float height = 20 * scale;
        float width = popupWidth - padding * 2;

        UIButton button = new UIButton(label, r, g, b, onClick);

        button.bounds.set(
            popupX + padding,
            layoutCursorY,
            width,
            height
        );
        layoutCursorY += height + 6 * scale;
        extraButtons.add(button);
    }


    private void drawExtraButtons() {
        for (UIButton button : extraButtons) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(button.r, button.g, button.b, 1f);
            shapeRenderer.rect(button.bounds.x, button.bounds.y, button.bounds.width, button.bounds.height);
            shapeRenderer.end();

            batch.begin();
            font.draw(batch, button.label, button.bounds.x + 4, button.bounds.y + button.bounds.height - 4);
            batch.end();
        }
    }

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

    public void handleClick(float screenX, float screenY, OrthographicCamera worldCamera) {
        Vector3 worldClick = new Vector3(screenX, screenY, 0);
        worldCamera.unproject(worldClick);

        if (deleteButtonBounds.contains(worldClick.x, worldClick.y)) deleteClickedThisFrame = true;
        for (UIButton button : extraButtons) button.handleClick(worldClick.x, worldClick.y);
    }

    protected float resolvePopupX(Entity entity, float popupWidth) {
        OrthographicCamera camera = world.getWorldCamera();

        float preferredX = getPopupX(entity);

        float worldLeft = camera.position.x - camera.viewportWidth * 0.5f * camera.zoom;
        float worldRight = camera.position.x + camera.viewportWidth * 0.5f * camera.zoom;

        float minX = worldLeft + 5;
        float maxX = worldRight - popupWidth - 5;

        return MathUtils.clamp(preferredX, minX, maxX);
    }

    protected float resolvePopupY(float preferredY, float popupHeight) {
        OrthographicCamera camera = world.getWorldCamera();

        float worldBottom = camera.position.y - camera.viewportHeight * 0.5f * camera.zoom;
        float worldTop = camera.position.y + camera.viewportHeight * 0.5f * camera.zoom;

        float minY = worldBottom + 5 + (GameUI.UI_BAR_HEIGHT * camera.zoom);
        float maxY = worldTop - popupHeight - 5;

        // Clamp safely into view
        return MathUtils.clamp(preferredY, minY, maxY);
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

    protected float getPopupScale(float zoom) {
        float target = 1f + (zoom - 1f) * 0.65f;
        target = MathUtils.clamp(target, 0.5f, 3f);
        popupScale = MathUtils.lerp(popupScale, target, 0.1f);
        return popupScale;
    }

    // Button class
    public static class UIButton {
        public final Rectangle bounds = new Rectangle();
        public final String label;
        public final Runnable onClick;
        public final float r, g, b;

        public UIButton(String label, float r, float g, float b, Runnable onClick) {
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
