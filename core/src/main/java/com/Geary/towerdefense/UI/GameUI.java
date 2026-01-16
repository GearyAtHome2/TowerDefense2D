package com.Geary.towerdefense.UI;

import com.Geary.towerdefense.UI.render.icons.IconStore;
import com.Geary.towerdefense.UI.render.icons.TooltipRenderer;
import com.Geary.towerdefense.UI.text.TextFormatter;
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

    // --- Main button layout ---
    private static final float BUTTON_START_X_RATIO = 0.1f;
    private static final float BUTTON_END_X_RATIO = 0.8f;
    private static final int BUTTON_COUNT = 5;

    private static final String[] BUTTON_LABELS = {
        "transport",
        "tower",
        "production",
        "manufacturing",
        "other"
    };

    // --- Resource panel layout ---
    private static final float RESOURCE_PANEL_WIDTH_RATIO = 0.08f;
    private static final float RESOURCE_PANEL_PADDING = 4f;

    private static final float RESOURCE_PADDING = 2f;
    private static final float RESOURCE_ICON_SIZE = 20f;
    private static final float RESOURCE_ICON_TEXT_GAP = 2f;
    private static final float COIN_PADDING = 3f;

    private final ShapeRenderer shapeRenderer;
    private final SpriteBatch batch;
    private final BitmapFont font;
    private final Viewport uiViewport;
    private final GameWorld world;


    private final GameStateManager gameStateManager;
    private final TransportManager transportManager;

    private TooltipRenderer tooltipRenderer;
    private final GlyphLayout glyphLayout = new GlyphLayout();

    private Rectangle[] mainButtons;
    private Vector3 lastMousePos = new Vector3();
    private ResourceType hoveredResource = null;

    public GameUI(
        ShapeRenderer shapeRenderer,
        SpriteBatch batch,
        BitmapFont font,
        Viewport uiViewport,
        GameWorld world,
        GameStateManager gameStateManager,
        TransportManager transportManager
    ) {
        this.shapeRenderer = shapeRenderer;
        this.batch = batch;
        this.font = font;
        this.uiViewport = uiViewport;
        this.world = world;
        this.gameStateManager = gameStateManager;
        this.transportManager = transportManager;
        this.tooltipRenderer = new TooltipRenderer(font);

        updateButtonBounds();
    }

    private void updateButtonBounds() {
        float width = uiViewport.getWorldWidth();

        float startX = width * BUTTON_START_X_RATIO;
        float endX = width * BUTTON_END_X_RATIO;
        float usableWidth = endX - startX;

        float padding = 10f; // small, fixed gap between buttons
        float buttonHeight = UI_BAR_HEIGHT * 0.7f;

        float buttonWidth =
            (usableWidth - padding * (BUTTON_COUNT - 1)) / BUTTON_COUNT;

        float y = (UI_BAR_HEIGHT - buttonHeight) / 2f;

        mainButtons = new Rectangle[BUTTON_COUNT];

        for (int i = 0; i < BUTTON_COUNT; i++) {
            float x = startX + i * (buttonWidth + padding);
            mainButtons[i] = new Rectangle(x, y, buttonWidth, buttonHeight);
        }
    }

    public void updateHover(float screenX, float screenY) {
        uiViewport.unproject(lastMousePos.set(screenX, screenY, 0));
        hoveredResource = null;

        hoveredResource = getHoveredResourceInPanel(
            getRawResourcePanelBounds(),
            gameStateManager.getRawResourceCount()
        );

        if (hoveredResource != null) return;

        hoveredResource = getHoveredResourceInPanel(
            getRefinedResourcePanelBounds(),
            gameStateManager.getRefinedResourceCount()
        );
    }

    public void drawUI(boolean paused, float gameSpeed) {
        uiViewport.apply();
        updateButtonBounds();

        shapeRenderer.setProjectionMatrix(uiViewport.getCamera().combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        // Background bar
        shapeRenderer.setColor(0.1f, 0.1f, 0.1f, 1f);
        shapeRenderer.rect(0, 0, uiViewport.getWorldWidth(), UI_BAR_HEIGHT);

        // Main buttons
//        shapeRenderer.setColor(0.25f, 0.5f, 0.35f, 1f);
//        for (Rectangle r : mainButtons) {
//            shapeRenderer.rect(r.x, r.y, r.width, r.height);
//        }

        // Resource panels
        Rectangle raw = getRawResourcePanelBounds();
        shapeRenderer.setColor(0.15f, 0.15f, 0.15f, 1f);
        shapeRenderer.rect(raw.x, raw.y, raw.width, raw.height);

        Rectangle refined = getRefinedResourcePanelBounds();
        shapeRenderer.setColor(0.2f, 0.15f, 0.3f, 1f);
        shapeRenderer.rect(refined.x, refined.y, refined.width, refined.height);

        shapeRenderer.end();

        batch.setProjectionMatrix(uiViewport.getCamera().combined);

        drawButtons();
//        for (int i = 0; i < mainButtons.length; i++) {
//            Rectangle r = mainButtons[i];
//
//            if (i == 0 && transportManager.isPlacementActive()) {
//                shapeRenderer.setColor(0f, 0.6f, 0.3f, 1f); // active
//            } else {
//                shapeRenderer.setColor(0.25f, 0.5f, 0.35f, 1f);
//            }
//
//            shapeRenderer.rect(r.x, r.y, r.width, r.height);
//        }


        batch.begin();

        float baseFontScale = UI_BAR_HEIGHT / 90f;
        font.getData().setScale(baseFontScale);
        font.draw(batch, "ESC = Pause", 10, UI_BAR_HEIGHT - 10);
        drawButtonLabels();
//        for (int i = 0; i < mainButtons.length; i++) {
//            Rectangle r = mainButtons[i];
//
//            glyphLayout.setText(font, BUTTON_LABELS[i]);
//
//            float textX = r.x + (r.width - glyphLayout.width) / 2f;
//            float textY = r.y + r.height / 2f + glyphLayout.height / 2f;
//
//            font.draw(batch, glyphLayout, textX, textY);
//        }

        batch.end();

        batch.begin();
        drawCoins(batch, font);
        drawResources(batch, font);

        batch.end();

        if (hoveredResource != null) {
            double amount = hoveredResource instanceof Resource.RawResourceType
                ? gameStateManager.getRawResourceCount().get(hoveredResource)
                : gameStateManager.getRefinedResourceCount().get(hoveredResource);

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
            font.draw(
                batch,
                "PAUSED",
                uiViewport.getWorldWidth() / 2f - 70,
                uiViewport.getWorldHeight() / 2f
            );
            batch.end();
        }

        font.getData().setScale(1f);
    }

    private void drawButtons() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (int i = 0; i < mainButtons.length; i++) {
            Rectangle r = mainButtons[i];

            if (i == 0 && transportManager.isPlacementActive()) {
                shapeRenderer.setColor(0f, 0.6f, 0.3f, 1f);
            } else {
                shapeRenderer.setColor(0.25f, 0.5f, 0.35f, 1f);
            }

            shapeRenderer.rect(r.x, r.y, r.width, r.height);
        }

        shapeRenderer.end();
    }

    private void drawButtonLabels() {
        for (int i = 0; i < mainButtons.length; i++) {
            Rectangle r = mainButtons[i];

            glyphLayout.setText(font, BUTTON_LABELS[i]);

            float textX = r.x + (r.width - glyphLayout.width) / 2f;
            float textY = r.y + r.height / 2f + glyphLayout.height / 2f;

            font.draw(batch, glyphLayout, textX, textY);
        }
    }

    private void drawResources(SpriteBatch batch, BitmapFont font) {
        drawResourceSet(batch, font, gameStateManager.getRawResourceCount(),
            IconStore::rawResource, getRawResourcePanelBounds());

        drawResourceSet(batch, font, gameStateManager.getRefinedResourceCount(),
            IconStore::refinedResource, getRefinedResourcePanelBounds());
    }

    private void drawCoins(SpriteBatch batch, BitmapFont font) {
        int coins = gameStateManager.gameState.getCoins();

        Rectangle refinedPanel = getRefinedResourcePanelBounds();
        float coinWidth = 40f;
        float coinHeight = refinedPanel.height - RESOURCE_PANEL_PADDING * 2;
        float coinX = refinedPanel.x - coinWidth - 4f;
        float coinY = refinedPanel.y + RESOURCE_PANEL_PADDING;

        font.getData().setScale(RESOURCE_ICON_SIZE / 20f);

        String text = String.valueOf(coins);
        glyphLayout.setText(font, text);

        float textX = coinX + (coinWidth - glyphLayout.width) / 2f;
        float textY = coinY + coinHeight - (coinHeight - glyphLayout.height) / 2f - 4f;

        font.draw(batch, glyphLayout, textX, textY);
    }

    private <T extends Enum<T>> void drawResourceSet(
        SpriteBatch batch,
        BitmapFont font,
        EnumMap<T, Double> resources,
        java.util.function.Function<T, TextureRegion> iconProvider,
        Rectangle panel
    ) {
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
        return new Rectangle(uiViewport.getWorldWidth() - panelWidth, 0, panelWidth, UI_BAR_HEIGHT);
    }

    private Rectangle getRefinedResourcePanelBounds() {
        float panelWidth = uiViewport.getWorldWidth() * RESOURCE_PANEL_WIDTH_RATIO;
        return new Rectangle(
            uiViewport.getWorldWidth() - 2 * panelWidth,
            0,
            panelWidth,
            UI_BAR_HEIGHT
        );
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

    public boolean handleUiClick(Vector3 uiClick) {
        // Transport button = index 0
        Rectangle transportButton = mainButtons[0];

        if (transportButton.contains(uiClick.x, uiClick.y)) {
            transportManager.togglePlacementClick(
                uiClick,
                transportButton.x,
                transportButton.y,
                transportButton.width,
                transportButton.height
            );
            return true;
        }

        return false;
    }
}
