package com.Geary.towerdefense.UI.displays.building.specialized.factory;

import com.Geary.towerdefense.UI.displays.modal.ScrollBox;
import com.Geary.towerdefense.UI.modal.Modal;
import com.Geary.towerdefense.entity.buildings.Factory;
import com.Geary.towerdefense.entity.resources.Recipe;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.ArrayList;
import java.util.List;

public class FactoryMenu extends Modal {

    private final Factory factory;
    private final ScrollBox scrollBox;

    public FactoryMenu(Factory factory, BitmapFont font) {
        super(font);
        this.factory = factory;

        scrollBox = new ScrollBox(0, 0, 0, 0);

        populateScrollBox();
    }

    /** Layout modal elements */
    @Override
    protected void layoutButtons() {
        float padding = 20;
        scrollBox.bounds.set(
            bounds.x + padding,
            bounds.y + 60,
            bounds.width - padding * 2,
            bounds.height - 100
        );

        float entryHeight = 40;
        for (RecipeMenuEntry entry : scrollBox.entries) {
            entry.bounds.x = scrollBox.bounds.x + 10;
            entry.bounds.width = scrollBox.bounds.width - 20;
            entry.bounds.height = entryHeight - 5;
            // Do NOT set entry.bounds.y here; ScrollBox manages Y based on scrollOffset
        }

        float spacing = 5f;
        float totalHeight = 0;

        for (int i = 0; i < scrollBox.entries.size(); i++) {
            totalHeight += scrollBox.entries.get(i).bounds.height;
            if (i < scrollBox.entries.size() - 1) {
                totalHeight += spacing;
            }
        }

        scrollBox.setContentHeight(totalHeight);
        scrollBox.relayout();
    }

    @Override
    protected void drawContent(ShapeRenderer shapeRenderer, SpriteBatch batch) {
        scrollBox.draw(shapeRenderer, batch, font);
    }

    @Override
    protected boolean handleClickInside(float x, float y) {
        if (scrollBox.contains(x, y)) {
            scrollBox.click(x, y);
            return true;
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
            entries.add(new RecipeMenuEntry(recipe, 0, 0, 0, entryHeight - 5,
                () -> System.out.println("Clicked recipe: " + recipe.inputs)));
        }

        float totalHeight = 0;
        for (int i = 0; i < entries.size(); i++) {
            totalHeight += entries.get(i).bounds.height;
            if (i < entries.size() - 1) totalHeight += entrySpacing; // spacing after each entry except last
        }

        scrollBox.setEntries(entries, totalHeight);
    }
}
