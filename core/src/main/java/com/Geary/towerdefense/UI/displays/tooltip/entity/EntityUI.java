package com.Geary.towerdefense.UI.displays.tooltip.entity;

import com.Geary.towerdefense.UI.GameUI;
import com.Geary.towerdefense.entity.Entity;
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

        float baseWidth = 10;   // Minimum width
        float padding = 8;
        float scale = getPopupScale(worldCameraZoom);
        float rowHeight = 24 * scale;

        // ---- Measure widest text line with proper scale ----
        List<String> infoLines = entity.getInfoLines();
        com.badlogic.gdx.graphics.g2d.GlyphLayout layout = new com.badlogic.gdx.graphics.g2d.GlyphLayout();
        float widestText = 0;
        for (String line : infoLines) {
            if (line != null) {
                layout.setText(font, line);
                float scaledLineWidth = layout.width * scale * 1.3f; // match drawing scale
                widestText = Math.max(widestText, scaledLineWidth);
            }
        }

        float scaledWidth = Math.max(baseWidth, widestText + padding * 2);
        float x = resolvePopupX(entity, scaledWidth);

        // ---- Measure total height ----
        layoutCursorY = padding;
        float textHeight = infoLines.size() * rowHeight;
        layoutCursorY += textHeight + padding; // padding below text

        extraButtons.clear();
        addExtraButtons(entity, x, 0, scaledWidth, 0, scale); // buttons increment layoutCursorY internally

        if (shouldDrawDeleteButton(entity)) {
            layoutCursorY += 20 * scale + padding;
        }

        float finalHeight = layoutCursorY;
        float preferredY = getPopupY(entity);
        float y = resolvePopupY(preferredY, finalHeight);

        // ---- LAYOUT PASS ----
        float cursorY = y + finalHeight - padding;

        // Draw background
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0f, 0f, 0f, 0.8f);
        shapeRenderer.rect(x, y, scaledWidth, finalHeight);
        shapeRenderer.end();

        // Draw text
        batch.begin();
        float originalScaleX = font.getData().scaleX;
        float originalScaleY = font.getData().scaleY;
        font.setColor(entity.getInfoTextColor());
        font.getData().setScale(originalScaleX * scale * 1.3f, originalScaleY * scale * 1.3f);

        for (String line : infoLines) {
            if (line != null) {
                font.draw(batch, line, x + padding, cursorY);
                cursorY -= rowHeight;
            }
        }
        batch.end();

        // Draw extra buttons stacked below text
        cursorY -= padding; // spacing between text and buttons
        for (UIButton button : extraButtons) {
            button.bounds.y = cursorY - button.bounds.height;
            cursorY -= button.bounds.height + 6 * scale;
        }

        drawExtraButtons();

        // Draw delete button at bottom
        if (shouldDrawDeleteButton(entity)) {
            drawDeleteButton(x, y, scaledWidth, scale);
        }

        // Reset font
        font.getData().setScale(originalScaleX, originalScaleY);
        font.setColor(Color.WHITE);
    }



    protected float getPopupX(Entity entity) {
        // default: next to entity (handles mobs too)
        return entity.xPos  + 5;
    }

    protected float getPopupY(Entity entity) {
        return entity.yPos;
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
