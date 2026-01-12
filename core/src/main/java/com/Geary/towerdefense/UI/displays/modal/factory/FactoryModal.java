package com.Geary.towerdefense.UI.displays.modal.factory;

import com.Geary.towerdefense.UI.displays.modal.Modal;
import com.Geary.towerdefense.UI.displays.modal.ScrollBox;
import com.Geary.towerdefense.UI.render.icons.TooltipRenderer;
import com.Geary.towerdefense.entity.buildings.Factory;
import com.Geary.towerdefense.entity.resources.Recipe;
import com.Geary.towerdefense.entity.resources.mapEntity.ResourceType;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FactoryModal extends Modal {

    private final Factory factory;
    private final ScrollBox<RecipeMenuEntry> scrollBox;
    public RecipeMenuEntry activeMenuEntry;

    private RecipeMenuEntry hoveredEntry;
    private ResourceType hoveredResource = null;
    private int resourceQuantity;
    private final TooltipRenderer tooltipRenderer;
    private final GlyphLayout layout = new GlyphLayout();
    private float mouseX, mouseY;

    /** Layout configuration: all ratios relative to modal or scrollbox */
    private static class LayoutConfig {
        float xPaddingRatio = 0.05f;           // fraction of modal width
        float scrollboxBottomRatio = 0.1f;     // fraction of modal height
        float scrollboxTopRatio = 0.7f;        // fraction of modal height
        float entryHeightRatio = 0.12f;        // max fraction of scrollbox height per entry
        float entrySpacingRatio = 0.01f;       // fraction of scrollbox height
        float hoodHeightRatio = 0.12f;         // fraction of scrollbox height
        float entrySidePaddingRatio = 0.05f;   // fraction of scrollbox width
        float tooltipOffsetXRatio = 0.02f;     // fraction of modal width
        float tooltipOffsetYRatio = 0.02f;     // fraction of modal height
        float tooltipPaddingRatio = 0.015f;    // fraction of modal width
        float hoodOverlapRatio = 0.002f;       // fraction of modal height
        float titleHeightRatio = 0.12f;     // fraction of modal height
        float titlePaddingYRatio = 0.02f;   // vertical padding inside title area
        float titleScale = 1.6f;
    }

    private final LayoutConfig layoutCfg = new LayoutConfig();

    public FactoryModal(Factory factory, BitmapFont font, OrthographicCamera camera) {
        super(font, camera);
        this.factory = factory;
        this.tooltipRenderer = new TooltipRenderer(font);
        scrollBox = new ScrollBox(0, 0, 0, 0);
        populateScrollBox();
    }

    @Override
    protected void layoutButtons() {
        layoutScrollBox();
        layoutScrollEntries();
    }

    @Override
    protected void drawContent(ShapeRenderer shapeRenderer, SpriteBatch batch) {
        scrollBox.draw(shapeRenderer, batch, font, camera);
        drawScrollHoods(shapeRenderer);
        drawTitle(batch);
        if (hoveredEntry != null) {
            drawTooltip(batch, shapeRenderer, hoveredEntry);
        }
    }

    public void updateHover(float screenX, float screenY, Viewport uiViewport) {
        Vector3 uiPos = uiViewport.unproject(new Vector3(screenX, screenY, 0));
        mouseX = uiPos.x;
        mouseY = uiPos.y;

        updateHoveredEntry(mouseX, mouseY);
    }

    @Override
    protected boolean handleClickInside(float x, float y) {
        if (!scrollBox.contains(x, y)) return false;
        RecipeMenuEntry clicked = scrollBox.click(x, y);
        if (clicked != null) setActiveEntry(clicked);
        return true;
    }

    @Override
    protected boolean handleScrollInside(float x, float y, float amountY) {
        if (scrollBox.contains(x, y)) {
            scrollBox.scroll(amountY * 10f); // scroll speed
            return true;
        }
        return false;
    }

    private void populateScrollBox() {
        List<RecipeMenuEntry> entries = new ArrayList<>();

        for (Recipe recipe : factory.recipes) {
            RecipeMenuEntry entry = new RecipeMenuEntry(
                recipe,
                0, 0, 0, 0,
                null
            );

            entry.onClick = () -> setActiveEntry(entry);

            if (factory.activeRecipe == recipe) {
                entry.active = true;
                activeMenuEntry = entry;
            }

            entries.add(entry);
        }

        scrollBox.setEntries(entries, entries.size() * 40f);
    }


    public void setActiveEntry(RecipeMenuEntry entry) {
        if (activeMenuEntry != null) activeMenuEntry.active = false;
        activeMenuEntry = entry;
        activeMenuEntry.active = true;
        factory.activeRecipe = entry.recipe;
    }

    private void drawTooltip(SpriteBatch batch, ShapeRenderer shapeRenderer, RecipeMenuEntry entry) {
        if (hoveredResource == null) return;

        String text = hoveredResource.getName() + ": " + resourceQuantity;
        float offsetX = bounds.width * layoutCfg.tooltipOffsetXRatio;
        float offsetY = bounds.height * layoutCfg.tooltipOffsetYRatio;
        float padding = bounds.width * layoutCfg.tooltipPaddingRatio;

        float tooltipX = mouseX + offsetX;
        float tooltipY = mouseY - offsetY;

        layout.setText(font, text);
        float tooltipWidth = layout.width + padding * 2;
        float tooltipHeight = layout.height + padding * 2;

        float menuRight = bounds.x + bounds.width;
        float menuTop = bounds.y + bounds.height;

        // Clamp tooltip within modal bounds
        if (tooltipX + tooltipWidth > menuRight) tooltipX = mouseX - tooltipWidth - offsetX;
        if (tooltipY < bounds.y) tooltipY = bounds.y;
        if (tooltipY + tooltipHeight > menuTop) tooltipY = menuTop - tooltipHeight;

        tooltipRenderer.drawTooltip(batch, shapeRenderer, text, tooltipX, tooltipY, padding);
    }

    private void layoutScrollBox() {
        float xPadding = modalXPadding();

        scrollBox.bounds.set(
            bounds.x + xPadding,
            scrollboxBottom(),
            bounds.width - xPadding * 2,
            scrollboxHeight()
        );
    }

    private void layoutScrollEntries() {
        float entryHeight = scrollBox.bounds.height * layoutCfg.entryHeightRatio;
        float entrySpacing = scrollBox.bounds.height * layoutCfg.entrySpacingRatio;
        float sidePadding = scrollBox.bounds.width * layoutCfg.entrySidePaddingRatio;

        float totalHeight = 0f;

        for (RecipeMenuEntry entry : scrollBox.entries) {
            entry.bounds.x = scrollBox.bounds.x + sidePadding;
            entry.bounds.width = scrollBox.bounds.width - sidePadding * 2;
            entry.bounds.height = entryHeight;

            totalHeight += entryHeight + entrySpacing;
        }

        if (!scrollBox.entries.isEmpty()) {
            totalHeight -= entrySpacing;
        }

        // bottom padding for hood masking
        float bottomPadding = scrollBox.bounds.height * layoutCfg.hoodHeightRatio;
        totalHeight += bottomPadding * 2;

        scrollBox.setContentHeight(totalHeight);
        scrollBox.relayout();
    }

    private void drawScrollHoods(ShapeRenderer shapeRenderer) {
        float hoodHeight = scrollBox.bounds.height * layoutCfg.hoodHeightRatio;
        float hoodOverlap = bounds.height * layoutCfg.hoodOverlapRatio;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.12f, 0.12f, 0f, 1f);

        // Top hood
        shapeRenderer.rect(
            scrollBox.bounds.x,
            scrollBox.bounds.y + scrollBox.bounds.height + hoodOverlap,
            scrollBox.bounds.width,
            hoodHeight
        );

        // Bottom hood
        shapeRenderer.rect(
            scrollBox.bounds.x,
            scrollBox.bounds.y - hoodHeight,
            scrollBox.bounds.width,
            hoodHeight
        );

        shapeRenderer.end();
    }

    private void drawTitle(SpriteBatch batch) {
        batch.begin();
        String title = factory.name;

        float oldScaleX = font.getScaleX();
        float oldScaleY = font.getScaleY();

        font.getData().setScale(layoutCfg.titleScale);

        layout.setText(font, title);

        float x = bounds.x + (bounds.width - layout.width) * 0.5f;
        float y = titleTextY();

        font.draw(batch, layout, x, y);

        font.getData().setScale(oldScaleX, oldScaleY);
        batch.end();
    }


    private void updateHoveredEntry(float x, float y) {
        hoveredEntry = null;
        hoveredResource = null;

        for (RecipeMenuEntry entry : scrollBox.entries) {
            if (entry.bounds.contains(x, y)) {
                hoveredEntry = entry;

                for (Map.Entry<ResourceType, Rectangle> hit : entry.resourceHitboxes.entrySet()) {
                    if (hit.getValue().contains(x, y)) {
                        hoveredResource = hit.getKey();
                        resourceQuantity =
                            entry.recipe.inputs.containsKey(hoveredResource)
                                ? entry.recipe.inputs.get(hoveredResource)
                                : entry.recipe.outputs.get(hoveredResource);
                        return;
                    }
                }
                return;
            }
        }
    }

    private float titleHeight() {
        return bounds.height * layoutCfg.titleHeightRatio;
    }

    private float titleBottomY() {
        return bounds.y + bounds.height - titleHeight();
    }

    private float titleTextY() {
        return titleBottomY()
            + titleHeight() * 0.5f
            + bounds.height * layoutCfg.titlePaddingYRatio;
    }


    private float modalXPadding() {
        return bounds.width * layoutCfg.xPaddingRatio;
    }

    private float scrollboxBottom() {
        return bounds.y + bounds.height * layoutCfg.scrollboxBottomRatio;
    }

    private float scrollboxTop() {
        return bounds.y + bounds.height * layoutCfg.scrollboxTopRatio;
    }

    private float scrollboxHeight() {
        return scrollboxTop() - scrollboxBottom();
    }
}
