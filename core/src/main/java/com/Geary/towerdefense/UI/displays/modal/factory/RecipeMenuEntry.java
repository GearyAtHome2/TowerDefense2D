package com.Geary.towerdefense.UI.displays.modal.factory;

import com.Geary.towerdefense.UI.render.icons.IconStore;
import com.Geary.towerdefense.UI.text.TextFormatter;
import com.Geary.towerdefense.entity.resources.Recipe;
import com.Geary.towerdefense.entity.resources.Resource;
import com.Geary.towerdefense.entity.resources.mapEntity.ResourceType;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

import java.util.HashMap;
import java.util.Map;

public class RecipeMenuEntry {

    public final Rectangle bounds = new Rectangle();
    final Recipe recipe;
    public Runnable onClick;
    public boolean active = false;

    // ---- layout constants ----
    private static final float PADDING = 6f;
    private static final float ICON_SIZE = 18f;
    private static final float ICON_SPACING = 6f;
    private static final float GROUP_SPACING = 14f;
    private static final float ARROW_SIZE = 26f;
    private static final float ARROW_ALPHA = 0.85f;

    public final Map<ResourceType, Rectangle> resourceHitboxes = new HashMap<>();

    private static final GlyphLayout layout = new GlyphLayout();

    public RecipeMenuEntry(Recipe recipe, float x, float y, float width, float height, Runnable onClick) {
        this.recipe = recipe;
        this.bounds.set(x, y, width, height);
        this.onClick = onClick;
    }

    public void draw(ShapeRenderer renderer, SpriteBatch batch, BitmapFont font) {
        // Clear previous hitboxes
        resourceHitboxes.clear();

        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(active ? 0.2f : 0.3f, active ? 0.6f : 0.3f, 0.7f, 1f);
        renderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
        renderer.end();



        batch.begin();
        float centerY = bounds.y + bounds.height / 2f;
        float textY = centerY + font.getCapHeight() / 2f;

        float x = bounds.x + PADDING;

        // Recipe name
        layout.setText(font, recipe.name);
        font.draw(batch, layout, x, textY);
        x += layout.width + GROUP_SPACING;

        float inputsEndX;
        float outputsStartX;

        for (Map.Entry<ResourceType, Integer> e : recipe.inputs.entrySet()) {
            ResourceType type = e.getKey();
            TextureRegion icon = null;

            if (type instanceof Resource.RawResourceType) {
                icon = IconStore.rawResource((Resource.RawResourceType) type);
            } else if (type instanceof Resource.RefinedResourceType) {
                icon = IconStore.refinedResource((Resource.RefinedResourceType) type);
            }
            String count = "x" + TextFormatter.formatResourceAmount(e.getValue());
            layout.setText(font, count);

            float iconX = x;
            float iconY = centerY - ICON_SIZE / 2f;
            if (icon != null) batch.draw(icon, iconX, iconY, ICON_SIZE, ICON_SIZE);

            float textX = iconX + ICON_SIZE + 2f;
            font.draw(batch, layout, textX, textY);

            // Register hitbox (icon + text)
            float width = ICON_SIZE + 2f + layout.width;
            float height = ICON_SIZE; // align with icon height
            resourceHitboxes.put(e.getKey(), new Rectangle(iconX, iconY, width, height));

            x = textX + layout.width + ICON_SPACING;
        }
        inputsEndX = x;
        float outX = bounds.x + bounds.width - PADDING;
        outputsStartX = outX;
        TextureRegion arrowIcon = IconStore.getSymbol(IconStore.Icon.ARROW_SYMBOL);
        if (arrowIcon != null) {
            float arrowCenterX = (inputsEndX + outputsStartX) * 0.5f;
            float arrowX = arrowCenterX - ARROW_SIZE / 2f;
            float arrowY = centerY - ARROW_SIZE / 2f;

            batch.setColor(1f, 1f, 1f, ARROW_ALPHA);
            batch.draw(arrowIcon, arrowX, arrowY, ARROW_SIZE, ARROW_SIZE);
            batch.setColor(1f, 1f, 1f, 1f);
        }
        for (Map.Entry<ResourceType, Integer> e : recipe.outputs.entrySet()) {
            String count = "x" + TextFormatter.formatResourceAmount(e.getValue());
            layout.setText(font, count);
            outX -= layout.width;
            float textX = outX;
            font.draw(batch, layout, textX, textY);
            outX -= 2f;

            ResourceType type = e.getKey();
            TextureRegion icon = null;

            if (type instanceof Resource.RawResourceType) {
                icon = IconStore.rawResource((Resource.RawResourceType) type);
            } else if (type instanceof Resource.RefinedResourceType) {
                icon = IconStore.refinedResource((Resource.RefinedResourceType) type);
            }
            if (icon != null) {
                outX -= ICON_SIZE;
                batch.draw(icon, outX, centerY - ICON_SIZE / 2f, ICON_SIZE, ICON_SIZE);
            }

            // Register hitbox (icon + text)
            float width = ICON_SIZE + 2f + layout.width;
            float height = ICON_SIZE;
            resourceHitboxes.put(e.getKey(), new Rectangle(outX, centerY - ICON_SIZE / 2f, width, height));

            outX -= ICON_SPACING;
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
