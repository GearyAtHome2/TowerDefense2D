package com.Geary.towerdefense.UI.displays.building.specialized.factory;

import com.Geary.towerdefense.UI.displays.modal.ScrollBox;
import com.Geary.towerdefense.UI.modal.Modal;
import com.Geary.towerdefense.UI.render.icons.TooltipRenderer;
import com.Geary.towerdefense.entity.buildings.Factory;
import com.Geary.towerdefense.entity.resources.Recipe;
import com.Geary.towerdefense.entity.resources.mapEntity.ResourceType;
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

public class FactoryMenu extends Modal {

    private final Factory factory;
    private final ScrollBox scrollBox;
    public RecipeMenuEntry activeMenuEntry;

    private RecipeMenuEntry hoveredEntry;
    private ResourceType hoveredResource = null;
    private int resourceQuantity;
    private final TooltipRenderer tooltipRenderer;
    private float mouseX, mouseY;

    public FactoryMenu(Factory factory, BitmapFont font) {
        super(font);
        this.factory = factory;
        this.tooltipRenderer = new TooltipRenderer(font);
        scrollBox = new ScrollBox(0, 0, 0, 0);
        populateScrollBox();
    }

    /**
     * Layout modal elements
     */
    @Override
    protected void layoutButtons() {
        float padding = 20f;

        // Compute scrollbox positions based on current bounds
        float scrollboxBottom = bounds.y + bounds.height * 0.1f;
        float scrollboxTop    = bounds.y + bounds.height * 0.9f;
        float scrollboxHeight = scrollboxTop - scrollboxBottom;

        scrollBox.bounds.set(
            bounds.x + padding,
            scrollboxBottom,
            bounds.width - padding * 2,
            scrollboxHeight
        );

        // Layout entries
        float entryHeight = 40;
        for (RecipeMenuEntry entry : scrollBox.entries) {
            entry.bounds.x = scrollBox.bounds.x + 10;
            entry.bounds.width = scrollBox.bounds.width - 20;
            entry.bounds.height = entryHeight - 5;
        }

        // Compute total content height
        float spacing = 5f;
        float totalHeight = 0;
        for (int i = 0; i < scrollBox.entries.size(); i++) {
            totalHeight += scrollBox.entries.get(i).bounds.height;
            if (i < scrollBox.entries.size() - 1) totalHeight += spacing;
        }

        scrollBox.setContentHeight(totalHeight);
        scrollBox.relayout();
    }

    @Override
    protected void drawContent(ShapeRenderer shapeRenderer, SpriteBatch batch) {
        scrollBox.draw(shapeRenderer, batch, font);

        // Draw top hood
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.12f, 0.12f, 0.12f, 1f);
        shapeRenderer.rect(scrollBox.bounds.x, scrollBox.bounds.y + scrollBox.bounds.height + 1,
            scrollBox.bounds.width, bounds.height / 11f); // 15 px hood
        // Draw bottom hood
        shapeRenderer.rect(scrollBox.bounds.x, scrollBox.bounds.y - bounds.height / 10f,
            scrollBox.bounds.width, bounds.height / 10f);
        shapeRenderer.end();

        // Draw tooltip if hovering
        if (hoveredEntry != null) {
            drawTooltip(batch, shapeRenderer, hoveredEntry);
        }
    }

    public void updateHover(float screenX, float screenY, Viewport uiViewport) {
        Vector3 uiPos = uiViewport.unproject(new Vector3(screenX, screenY, 0));
        mouseX = uiPos.x;
        mouseY = uiPos.y;

        hoveredEntry = null;
        hoveredResource = null;

        for (RecipeMenuEntry entry : scrollBox.entries) {
            if (entry.bounds.contains(mouseX, mouseY)) {
                hoveredEntry = entry;
                for (Map.Entry<ResourceType, Rectangle> hit : entry.resourceHitboxes.entrySet()) {
                    if (hit.getValue().contains(mouseX, mouseY)) {
                        hoveredResource = hit.getKey();
                        resourceQuantity = entry.recipe.inputs.containsKey(hoveredResource)
                            ? entry.recipe.inputs.get(hoveredResource)
                            : entry.recipe.outputs.get(hoveredResource);
                        return;
                    }
                }
                break;
            }
        }
    }

    // Helper: approximate width of "xN" text for hit detection
    private float getTextWidth(int amount) {
        layout.setText(font, "x" + amount);
        return layout.width;
    }


    @Override
    protected boolean handleClickInside(float x, float y) {
        if (!scrollBox.contains(x, y)) return false;

        RecipeMenuEntry clicked = scrollBox.click(x, y);
        if (clicked != null) {
            setActiveEntry(clicked);
        }
        return true;
    }

    @Override
    protected boolean handleScrollInside(float x, float y, float amountY) {
        if (scrollBox.contains(x, y)) {
            scrollBox.scroll(amountY * 10f);
            return true;
        }
        return false;
    }

    private void populateScrollBox() {
        float entryHeight = 40;
        float entrySpacing = 5f; // buffer between entries
        List<RecipeMenuEntry> entries = new ArrayList<>();

        for (Recipe recipe : factory.recipes) {

            RecipeMenuEntry entry = new RecipeMenuEntry(
                recipe,
                0, 0, 0, entryHeight - 5,
                null
            );

            entry.onClick = () -> setActiveEntry(entry);

            entries.add(entry);
        }

        float totalHeight = 0;
        for (int i = 0; i < entries.size(); i++) {
            totalHeight += entries.get(i).bounds.height;
            if (i < entries.size() - 1) totalHeight += entrySpacing; // spacing after each entry except last
        }

        scrollBox.setEntries(entries, totalHeight);
    }

    public void setActiveEntry(RecipeMenuEntry entry) {
        if (activeMenuEntry != null) {
            activeMenuEntry.active = false;
        }

        activeMenuEntry = entry;
        activeMenuEntry.active = true;

        factory.activeRecipe = entry.recipe;
    }

    // ---------------- Tooltip rendering ----------------
    private final GlyphLayout layout = new GlyphLayout();

    private void drawTooltip(SpriteBatch batch, ShapeRenderer shapeRenderer, RecipeMenuEntry entry) {
        if (hoveredResource == null) return;

        String text = hoveredResource.getName() + ": " + resourceQuantity; // display only the hovered resource
        float padding = 6f;

        float tooltipX = mouseX + 8;
        float tooltipY = mouseY - 8;

        float menuRight = bounds.x + bounds.width;
        layout.setText(font, text);
        float tooltipWidth = layout.width + padding * 2;
        float tooltipHeight = layout.height + padding * 2;

        // Clamp to right edge
        if (tooltipX + tooltipWidth > menuRight) {
            tooltipX = mouseX - tooltipWidth - 8;
        }

        tooltipRenderer.drawTooltip(batch, shapeRenderer, text, tooltipX, tooltipY, padding);
    }

}
