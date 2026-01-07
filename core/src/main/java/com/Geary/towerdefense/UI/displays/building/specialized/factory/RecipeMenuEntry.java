package com.Geary.towerdefense.UI.displays.building.specialized.factory;

import com.Geary.towerdefense.entity.resources.Recipe;
import com.Geary.towerdefense.entity.resources.Resource;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

import java.util.Map;

/**
 * Visual representation of a FactoryRecipe inside a ScrollBox
 */
public class RecipeMenuEntry {

    public final Rectangle bounds = new Rectangle();
    private final Recipe recipe;
    public final Runnable onClick;

    public RecipeMenuEntry(Recipe recipe, float x, float y, float width, float height, Runnable onClick) {
        this.recipe = recipe;
        this.bounds.set(x, y, width, height);
        this.onClick = onClick;
    }


    public void draw(ShapeRenderer renderer, SpriteBatch batch, BitmapFont font, int index) {
        // Draw rectangle background
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(0.3f, 0.3f, 0.7f, 1f);
        renderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        renderer.end();

        batch.begin();
        float textX = bounds.x + 5;
        float textY = bounds.y + bounds.height - 5;

        // Recipe header (optional: generate simple name)
        font.draw(batch, "Recipe: "+index, textX, textY);
        textY -= font.getCapHeight() + 2;

        // Inputs
        font.draw(batch, "Inputs:", textX, textY);
        textY -= font.getCapHeight() + 2;
        for (Map.Entry<Resource.RawResourceType, Integer> e : recipe.inputs.entrySet()) {
            font.draw(batch, e.getKey().name() + " x" + e.getValue(), textX + 10, textY);
            textY -= font.getCapHeight() + 2;
        }

        batch.end();
    }


    public boolean click(float worldX, float worldY) {
        if (bounds.contains(worldX, worldY)) {
            if (onClick != null) onClick.run();
            return true;
        }
        return false;
    }
}
