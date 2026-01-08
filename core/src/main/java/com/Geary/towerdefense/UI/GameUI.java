package com.Geary.towerdefense.UI;

import com.Geary.towerdefense.UI.render.icons.IconStore;
import com.Geary.towerdefense.UI.render.icons.TooltipRenderer;
import com.Geary.towerdefense.UI.text.TextFormatter;
import com.Geary.towerdefense.behaviour.buildings.manager.TowerManager;
import com.Geary.towerdefense.behaviour.buildings.manager.TransportManager;
import com.Geary.towerdefense.entity.resources.Resource;
import com.Geary.towerdefense.entity.resources.mapEntity.ResourceType;
import com.Geary.towerdefense.world.GameStateManager;
import com.Geary.towerdefense.world.GameWorld;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.EnumMap;

public class GameUI {

    public static final int UI_BAR_HEIGHT = 90;

    // Button size relative to UI_BAR_HEIGHT
    private static final float DEFAULT_BUTTON_WIDTH = 0.18f; // % of viewport width
    private static final float BUTTON_HEIGHT = 0.2f * UI_BAR_HEIGHT;
    private static final float BUTTON_MARGIN = 18f;
    private static final float BUTTON_VERTICAL_SPACING = 3f;

    // --- Resource panel layout ---
    private static final float RESOURCE_PANEL_WIDTH_RATIO = 0.21f; // % of screen width
    private static final float RESOURCE_PANEL_PADDING = 4f;

    private static final float RESOURCE_PADDING = 2f;
    private static final float RESOURCE_ICON_SIZE = 20f;
    private static final float RESOURCE_ICON_TEXT_GAP = 2f;

    private final ShapeRenderer shapeRenderer;
    private final SpriteBatch batch;
    private final BitmapFont font;
    private final Viewport uiViewport;
    private final GameWorld world;
    private final TowerManager towerManager;
    private final TransportManager transportManager;
    private final GameStateManager gameStateManager;

    private TooltipRenderer tooltipRenderer;

    private final GlyphLayout glyphLayout = new GlyphLayout();

    private Rectangle placeTowerButtonBounds;
    private Rectangle transportButtonBounds;

    private Vector3 lastMousePos = new Vector3();
    private ResourceType hoveredResource = null;

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
        this.tooltipRenderer = new TooltipRenderer(font);
        updateButtonBounds();
    }

    private void updateButtonBounds() {
        float viewportWidth = uiViewport.getWorldWidth();

        float placeTowerX = BUTTON_MARGIN;
        float placeTowerY = (UI_BAR_HEIGHT - BUTTON_HEIGHT) / 2f;

        float placeTowerWidthPixels = viewportWidth * DEFAULT_BUTTON_WIDTH;
        float transportWidthPixels = viewportWidth * DEFAULT_BUTTON_WIDTH;

        placeTowerButtonBounds = new Rectangle(placeTowerX, placeTowerY, placeTowerWidthPixels, BUTTON_HEIGHT);
        transportButtonBounds = new Rectangle(placeTowerX, placeTowerY - BUTTON_HEIGHT - BUTTON_VERTICAL_SPACING, transportWidthPixels, BUTTON_HEIGHT);
    }

    public void updateHover(float screenX, float screenY) {
        uiViewport.unproject(lastMousePos.set(screenX, screenY, 0));
        hoveredResource = null;

        // Check raw resources
        hoveredResource = getHoveredResourceInPanel(
            getRawResourcePanelBounds(),
            gameStateManager.getRawResourceCount()
        );

        if (hoveredResource != null) return;

        // Check refined resources
        hoveredResource = getHoveredResourceInPanel(
            getRefinedResourcePanelBounds(),
            gameStateManager.getRefinedResourceCount()
        );
    }


    public void drawUI(boolean paused, float gameSpeed) {
        uiViewport.apply();
        updateButtonBounds();

        // 1️⃣ Draw UI shapes
        shapeRenderer.setProjectionMatrix(uiViewport.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Background bar
        shapeRenderer.setColor(0.1f, 0.1f, 0.1f, 1f);
        shapeRenderer.rect(0, 0, uiViewport.getWorldWidth(), UI_BAR_HEIGHT);

        // Tower button
        shapeRenderer.setColor(towerManager.isPlacementActive() ? 0f : 0.3f, 0.6f, 0.3f, 1f);
        shapeRenderer.rect(placeTowerButtonBounds.x, placeTowerButtonBounds.y,
            placeTowerButtonBounds.width, placeTowerButtonBounds.height);

        // Transport button
        shapeRenderer.setColor(transportManager.isPlacementActive() ? 0f : 0.3f, 0.6f, 0.3f, 1f);
        shapeRenderer.rect(transportButtonBounds.x, transportButtonBounds.y,
            transportButtonBounds.width, transportButtonBounds.height);

        // Resource panel
        Rectangle resourcePanel = getRawResourcePanelBounds();
        shapeRenderer.setColor(0.15f, 0.15f, 0.15f, 1f);
        shapeRenderer.rect(resourcePanel.x, resourcePanel.y, resourcePanel.width, resourcePanel.height);

        Rectangle refinedPanel = getRefinedResourcePanelBounds();
        shapeRenderer.setColor(0.2f, 0.15f, 0.3f, 1f); // different color (purple-ish)
        shapeRenderer.rect(refinedPanel.x, refinedPanel.y, refinedPanel.width, refinedPanel.height);
        shapeRenderer.end();

        // 2️⃣ Draw all text, icons, and tooltips in a single batch
        batch.setProjectionMatrix(uiViewport.getCamera().combined);
        batch.begin();

        // Reset font scale for buttons
        float baseFontScale = UI_BAR_HEIGHT / 90f;
        font.getData().setScale(baseFontScale);

        // Button labels
        font.draw(batch, "Place Tower",
            placeTowerButtonBounds.x + 15,
            placeTowerButtonBounds.y + placeTowerButtonBounds.height / 2 + BUTTON_HEIGHT / 4);

        font.draw(batch, "Place Transport",
            transportButtonBounds.x + 10,
            transportButtonBounds.y + transportButtonBounds.height / 2 + BUTTON_HEIGHT / 4);

        // Misc UI
        font.draw(batch, "ESC = Pause", 10, UI_BAR_HEIGHT - 10);
        font.draw(batch, "gamespeed: " + gameSpeed, 400, UI_BAR_HEIGHT - 50);
        // Draw resources
        float resourceFontScale = Math.round(((resourcePanel.height - RESOURCE_PANEL_PADDING * 2) / 80f) * 2f) / 2f;
        font.getData().setScale(resourceFontScale);
        drawResources(batch, font);
        batch.end();
        if (hoveredResource != null) {
            double amount;

            if (hoveredResource instanceof Resource.RawResourceType) {
                amount = gameStateManager
                    .getRawResourceCount()
                    .get((Resource.RawResourceType) hoveredResource);
            } else {
                amount = gameStateManager
                    .getRefinedResourceCount()
                    .get((Resource.RefinedResourceType) hoveredResource);
            }

            tooltipRenderer.drawTooltip(
                batch,
                shapeRenderer,
                hoveredResource.getName() + ": " + (int) amount,
                lastMousePos.x,
                lastMousePos.y,
                4f
            );
        }


        if (paused) {
            batch.begin();
            font.getData().setScale(2.5f);
            font.draw(batch, "PAUSED",
                uiViewport.getWorldWidth() / 2f - 70,
                uiViewport.getWorldHeight() / 2f);
            batch.end();
        }

        // Reset font scale for safety
        font.getData().setScale(1f);
    }

    private void drawResources(SpriteBatch batch, BitmapFont font) {
        // --- Draw Raw Resources ---
        drawResourceSet(batch, font, gameStateManager.getRawResourceCount(),
            IconStore::rawResource, getRawResourcePanelBounds());

        // --- Draw Refined Resources ---
        drawResourceSet(batch, font, gameStateManager.getRefinedResourceCount(),
            IconStore::refinedResource, getRefinedResourcePanelBounds());
    }

    // Generic helper to draw resources inside a panel
    private <T extends Enum<T>> void drawResourceSet(SpriteBatch batch, BitmapFont font,
                                                     EnumMap<T, Double> resources,
                                                     java.util.function.Function<T, TextureRegion> iconProvider,
                                                     Rectangle panel) {
        if (resources == null || resources.isEmpty()) return;

        float usableHeight = panel.height - RESOURCE_PANEL_PADDING * 2;
        float lineHeight = RESOURCE_ICON_SIZE + RESOURCE_PADDING;
        int maxRows = Math.max(1, (int) (usableHeight / lineHeight));
        float columnSpacing = RESOURCE_ICON_SIZE + RESOURCE_ICON_TEXT_GAP + 35f;

        float startX = panel.x + RESOURCE_PANEL_PADDING;
        float startY = panel.y + panel.height - RESOURCE_PANEL_PADDING;

        int row = 0;
        int column = 0;
        for (T type : resources.keySet()) {
            TextureRegion icon = iconProvider.apply(type);
            if (icon == null) continue;

            String text = TextFormatter.formatResourceAmount(resources.get(type));
            glyphLayout.setText(font, text);

            float x = startX + column * columnSpacing;
            float y = startY - row * lineHeight;

            float iconY = y - RESOURCE_ICON_SIZE + (lineHeight - RESOURCE_ICON_SIZE) / 2f;
            batch.draw(icon, x, iconY, RESOURCE_ICON_SIZE, RESOURCE_ICON_SIZE);
            font.draw(batch, glyphLayout, x + RESOURCE_ICON_SIZE + RESOURCE_ICON_TEXT_GAP, y);

            row++;
            if (row >= maxRows) {
                row = 0;
                column++;
            }
        }
    }

    private Rectangle getRawResourcePanelBounds() {
        float panelWidth = uiViewport.getWorldWidth() * RESOURCE_PANEL_WIDTH_RATIO;

        return new Rectangle(
            uiViewport.getWorldWidth() - panelWidth,
            0,
            panelWidth,
            UI_BAR_HEIGHT
        );
    }

    private Rectangle getRefinedResourcePanelBounds() {
        float panelWidth = uiViewport.getWorldWidth() * RESOURCE_PANEL_WIDTH_RATIO;
        float panelHeight = UI_BAR_HEIGHT;

        float panelX = uiViewport.getWorldWidth() - 2 * panelWidth; // left of raw panel
        float panelY = 0;

        return new Rectangle(panelX, panelY, panelWidth, panelHeight);
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
            transportManager.togglePlacementClick(uiClick,
                placeTowerButtonBounds.x,
                placeTowerButtonBounds.y,
                placeTowerButtonBounds.width,
                placeTowerButtonBounds.height);
            return true;
        }
        return false;
    }

    private <T extends Enum<T>> T getHoveredResourceInPanel(
        Rectangle panel,
        EnumMap<T, Double> resources
    ) {
        float usableHeight = panel.height - RESOURCE_PANEL_PADDING * 2;
        float lineHeight = RESOURCE_ICON_SIZE + RESOURCE_PADDING;
        int maxRows = Math.max(1, (int) (usableHeight / lineHeight));
        float columnSpacing = RESOURCE_ICON_SIZE + RESOURCE_ICON_TEXT_GAP + 35f;

        float startX = panel.x + RESOURCE_PANEL_PADDING;
        float startY = panel.y + panel.height - RESOURCE_PANEL_PADDING;

        int row = 0;
        int column = 0;

        for (T type : resources.keySet()) {
            float x = startX + column * columnSpacing;
            float y = startY - row * lineHeight;

            Rectangle cell = new Rectangle(x, y - lineHeight, columnSpacing, lineHeight);
            if (cell.contains(lastMousePos.x, lastMousePos.y)) {
                return type;
            }

            row++;
            if (row >= maxRows) {
                row = 0;
                column++;
            }
        }
        return null;
    }

}
