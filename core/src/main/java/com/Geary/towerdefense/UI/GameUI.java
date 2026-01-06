package com.Geary.towerdefense.UI;

import com.Geary.towerdefense.behaviour.buildings.manager.TowerManager;
import com.Geary.towerdefense.behaviour.buildings.manager.TransportManager;
import com.Geary.towerdefense.entity.resources.Resource;
import com.Geary.towerdefense.world.GameStateManager;
import com.Geary.towerdefense.world.GameWorld;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
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
    private static final float BUTTON_HEIGHT = 0.2f * UI_BAR_HEIGHT;
    private static final float BUTTON_MARGIN = 18f;
    private static final float BUTTON_VERTICAL_SPACING = 3f;

    // --- Resource panel layout ---
    private static final float RESOURCE_PANEL_WIDTH_RATIO = 0.21f; // % of screen width
    private static final float RESOURCE_PANEL_PADDING = 4f;

    private static final float RESOURCE_PADDING = 2f;
    private static final float RESOURCE_COLUMN_SPACING = 24f;
    private static final float RESOURCE_TOP_MARGIN = 12f;
    private static final float RESOURCE_RIGHT_MARGIN = 16f;
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

    private final GlyphLayout glyphLayout = new GlyphLayout();

    private final EnumMap<Resource.RawResourceType, Texture> resourceIcons
        = new EnumMap<>(Resource.RawResourceType.class);

    private Rectangle placeTowerButtonBounds;
    private Rectangle transportButtonBounds;

    private Vector3 lastMousePos = new Vector3();
    private Resource.RawResourceType hoveredResource = null;

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
        resourceIcons.put(Resource.RawResourceType.TIN,
            new Texture("tin.png"));
        resourceIcons.put(Resource.RawResourceType.STONE,
            new Texture("stone.png"));
        resourceIcons.put(Resource.RawResourceType.IRON,
            new Texture("iron.png"));
        resourceIcons.put(Resource.RawResourceType.COAL,
            new Texture("coal.png"));
        resourceIcons.put(Resource.RawResourceType.COPPER,
            new Texture("copper.png"));
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

        Rectangle panel = getResourcePanelBounds();
        float usableHeight = panel.height - RESOURCE_PANEL_PADDING * 2;
        float lineHeight = RESOURCE_ICON_SIZE + RESOURCE_PADDING;
        int maxRows = Math.max(1, (int) (usableHeight / lineHeight));
        float columnSpacing = RESOURCE_ICON_SIZE + RESOURCE_ICON_TEXT_GAP + 35f;

        float startX = panel.x + RESOURCE_PANEL_PADDING;
        float startY = panel.y + panel.height - RESOURCE_PANEL_PADDING;

        int row = 0;
        int column = 0;

        for (Resource.RawResourceType type : gameStateManager.getRawResourceCount().keySet()) {
            float x = startX + column * columnSpacing;
            float y = startY - row * lineHeight;
            Rectangle cell = new Rectangle(x, y - lineHeight, columnSpacing, lineHeight); // simple hitbox

            if (cell.contains(lastMousePos.x, lastMousePos.y)) {
                hoveredResource = type;
                break;
            }

            row++;
            if (row >= maxRows) {
                row = 0;
                column++;
            }
        }
    }


    public void drawUI(boolean paused, float gameSpeed) {
        uiViewport.apply();
        updateButtonBounds();

        shapeRenderer.setProjectionMatrix(uiViewport.getCamera().combined);
        batch.setProjectionMatrix(uiViewport.getCamera().combined);

        // =====================
        // 1. SHAPES ONLY (UI bars and buttons)
        // =====================
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        shapeRenderer.setColor(0.1f, 0.1f, 0.1f, 1f);
        shapeRenderer.rect(0, 0, uiViewport.getWorldWidth(), UI_BAR_HEIGHT);

        shapeRenderer.setColor(towerManager.isPlacementActive() ? 0f : 0.3f, 0.6f, 0.3f, 1f);
        shapeRenderer.rect(placeTowerButtonBounds.x, placeTowerButtonBounds.y,
            placeTowerButtonBounds.width, placeTowerButtonBounds.height);

        shapeRenderer.setColor(transportManager.isPlacementActive() ? 0f : 0.3f, 0.6f, 0.3f, 1f);
        shapeRenderer.rect(transportButtonBounds.x, transportButtonBounds.y,
            transportButtonBounds.width, transportButtonBounds.height);

        Rectangle resourcePanel = getResourcePanelBounds();
        shapeRenderer.setColor(0.15f, 0.15f, 0.15f, 1f);
        shapeRenderer.rect(resourcePanel.x, resourcePanel.y,
            resourcePanel.width, resourcePanel.height);

        shapeRenderer.end();

        // =====================
        // 2. TEXT + ICONS
        // =====================
        batch.begin();

        float baseFontScale = UI_BAR_HEIGHT / 90f;
        font.getData().setScale(baseFontScale);

        font.draw(batch, "Place Tower",
            placeTowerButtonBounds.x + 15,
            placeTowerButtonBounds.y + placeTowerButtonBounds.height / 2 + BUTTON_HEIGHT / 4);

        font.draw(batch, "Place Transport",
            transportButtonBounds.x + 10,
            transportButtonBounds.y + transportButtonBounds.height / 2 + BUTTON_HEIGHT / 4);

        font.draw(batch, "ESC = Pause", 10, UI_BAR_HEIGHT - 10);
        font.draw(batch, "gamespeed: " + gameSpeed, 400, UI_BAR_HEIGHT - 50);

        // Draw resources
        float resourceFontScale =
            Math.round(((resourcePanel.height - RESOURCE_PANEL_PADDING * 2) / 80f) * 2f) / 2f;
        font.getData().setScale(resourceFontScale);

        drawResourcesAndTooltip(batch, font, gameStateManager.getRawResourceCount());

        batch.end(); // finish all text/icons

        // =====================
        // 3. TOOLTIP (hover)
        // =====================
        if (hoveredResource != null) {
            String exactAmount = String.valueOf(gameStateManager.getRawResourceCount().get(hoveredResource).intValue());
            glyphLayout.setText(font, exactAmount);

            float tooltipPadding = 4f;
            float tooltipWidth = glyphLayout.width + tooltipPadding * 2;
            float tooltipHeight = glyphLayout.height + tooltipPadding * 2;

            float tooltipX = lastMousePos.x + 8;
            float tooltipY = lastMousePos.y - 8;

            // Draw background
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0f, 0f, 0f, 0.8f);
            shapeRenderer.rect(tooltipX, tooltipY - tooltipHeight, tooltipWidth, tooltipHeight);
            shapeRenderer.end();

            // Draw text on top
            batch.begin();
            font.draw(batch, glyphLayout, tooltipX + tooltipPadding, tooltipY - tooltipPadding);
            batch.end();
        }

        // =====================
        // 4. PAUSE
        // =====================
        if (paused) {
            batch.begin();
            font.getData().setScale(2.5f);
            font.draw(batch, "PAUSED",
                uiViewport.getWorldWidth() / 2f - 70,
                uiViewport.getWorldHeight() / 2f);
            batch.end();
        }

        font.getData().setScale(1f);
    }


    private void drawResourcesAndTooltip(SpriteBatch batch, BitmapFont font,
                                         EnumMap<Resource.RawResourceType, Double> resources) {
        if (resources == null || resources.isEmpty()) return;

        Rectangle panel = getResourcePanelBounds();

        float usableHeight = panel.height - RESOURCE_PANEL_PADDING * 2;
        float lineHeight = RESOURCE_ICON_SIZE + RESOURCE_PADDING;
        int maxRows = Math.max(1, (int) (usableHeight / lineHeight));

        float columnSpacing = RESOURCE_ICON_SIZE + RESOURCE_ICON_TEXT_GAP + 35f;
        float startX = panel.x + RESOURCE_PANEL_PADDING;
        float startY = panel.y + panel.height - RESOURCE_PANEL_PADDING;

        int row = 0;
        int column = 0;

        // Draw icons and text
        for (Resource.RawResourceType type : resources.keySet()) {
            Texture icon = resourceIcons.get(type);
            if (icon == null) continue;

            String text = formatResourceAmount(resources.get(type));
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

        // Draw tooltip separately using ShapeRenderer first, then batch for text
        if (hoveredResource != null) {
            String exactAmount = String.valueOf(resources.get(hoveredResource).intValue());
            glyphLayout.setText(font, exactAmount);

            float tooltipPadding = 4f;
            float tooltipWidth = glyphLayout.width + tooltipPadding * 2;
            float tooltipHeight = glyphLayout.height + tooltipPadding * 2;

            float tooltipX = lastMousePos.x + 8;
            float tooltipY = lastMousePos.y - 8;

            // ShapeRenderer for background
            batch.end(); // end batch before using ShapeRenderer
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(0f, 0f, 0f, 0.8f);
            shapeRenderer.rect(tooltipX, tooltipY - tooltipHeight, tooltipWidth, tooltipHeight);
            shapeRenderer.end();

            // Back to batch for text
            batch.begin();
            font.draw(batch, glyphLayout, tooltipX + tooltipPadding, tooltipY - tooltipPadding);
            // Do NOT end batch here; caller will end batch
        }
    }


    private Rectangle getResourcePanelBounds() {
        float panelWidth = uiViewport.getWorldWidth() * RESOURCE_PANEL_WIDTH_RATIO;

        return new Rectangle(
            uiViewport.getWorldWidth() - panelWidth,
            0,
            panelWidth,
            UI_BAR_HEIGHT
        );
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

    private String formatResourceAmount(double amount) {
        if (amount >= 1_000_000) {
            double value = amount / 1_000_000;
            if (value >= 10) {
                return String.format("%dM", Math.round(value)); // no decimals above 10M
            } else if (value == (int) value) {
                return String.format("%dM", (int) value);
            } else {
                return String.format("%.1fM", value);
            }
        } else if (amount >= 1_000) {
            double value = amount / 1_000;
            if (value >= 10) {
                return String.format("%dK", Math.round(value)); // no decimals above 10k
            } else if (value == (int) value) {
                return String.format("%dK", (int) value);
            } else {
                return String.format("%.1fK", value);
            }
        } else {
            return String.valueOf((int) amount); // under 1k, just whole numbers
        }
    }

}
