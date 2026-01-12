package com.Geary.towerdefense.UI.displays.modal.spawner;

import com.Geary.towerdefense.UI.displays.modal.Modal;
import com.Geary.towerdefense.UI.displays.modal.ScrollBox;
import com.Geary.towerdefense.entity.mob.Mob;
import com.Geary.towerdefense.entity.spawner.FriendlySpawner;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SpawnerModal extends Modal {

    private final FriendlySpawner spawner;
    // Three scrollboxes
    private final ScrollBox<MobMenuEntry> mobSelectionScrollBox;
    private final ScrollBox<MobMenuEntry> queueScrollBox;
    private final ScrollBox<MobMenuEntry> garrisonScrollBox;

    // Top area tabs
    private final List<String> tabs = List.of("All", "Type1", "Type2");
    private int activeTabIndex = 0;
    private final Map<String, List<MobMenuEntry>> tabEntries = new HashMap<>();

    public MobMenuEntry hoveredEntry;
    public MobMenuEntry activeEntry;

    private final float tabHeightFraction = 0.08f; // 8% of modal height

    public SpawnerModal(FriendlySpawner spawner, BitmapFont font, OrthographicCamera camera) {
        super(font, camera);
        this.spawner = spawner;

        mobSelectionScrollBox = new ScrollBox<>(0, 0, 0, 0);
        queueScrollBox = new ScrollBox<>(0, 0, 0, 0);
        garrisonScrollBox = new ScrollBox<>(0, 0, 0, 0);

        setupTabEntries();
        populateScrollBoxes();
    }

    private void setupTabEntries() {
        // Create entries for each tab (here just splitting example by "type")
        for (String tab : tabs) {
            List<MobMenuEntry> entries = new ArrayList<>();
            for (Mob mob : spawner.getSpawnableMobs()) {
                // For now all mobs go into all tabs; type filtering can be added later
                MobMenuEntry entry = new MobMenuEntry(mob, 0, 0, 60, 60);
                entry.onClick = () -> {
                    activeEntry = entry;
                    spawner.spawn(); // spawn mob by MobStats
                };
                entries.add(entry);
            }
            tabEntries.put(tab, entries);
        }
    }

    private void populateScrollBoxes() {
        // Top area: active tab entries
        updateTopScrollBox();

        // Queue area: empty for now, but compute content height dynamically
        List<MobMenuEntry> queueEntries = new ArrayList<>();
        float queueTotalHeight = computeContentHeight(queueEntries);
        queueScrollBox.setEntries(queueEntries, queueTotalHeight);

        // Garrison area: empty for now
        List<MobMenuEntry> garrisonEntries = new ArrayList<>();
        float garrisonTotalHeight = computeContentHeight(garrisonEntries);
        garrisonScrollBox.setEntries(garrisonEntries, garrisonTotalHeight);
    }

    /** Compute content height from entry list */
    private float computeContentHeight(List<MobMenuEntry> entries) {
        float totalHeight = 0f;
        float spacing = 5f; // must match ScrollBox.updateEntryPositions()
        for (MobMenuEntry entry : entries) {
            totalHeight += entry.bounds.height + spacing;
        }
        if (!entries.isEmpty()) totalHeight -= spacing; // remove last spacing
        return totalHeight;
    }

    /** Call this when setting tab or initially populating top scrollbox */
    private void updateTopScrollBox() {
        List<MobMenuEntry> entries = tabEntries.get(tabs.get(activeTabIndex));
        float totalHeight = computeContentHeight(entries);
        mobSelectionScrollBox.setEntries(entries, totalHeight);
    }


    @Override
    protected void layoutButtons() {
        float horizontalPadding = bounds.width * 0.02f;
        float spacingFraction = 0.02f;
        float spacing = bounds.height * spacingFraction;

        float topFraction = 0.6f;
        float queueFraction = 0.2f;
        float garrisonFraction = 0.18f;

        float totalSpacing = spacing * 3;
        float availableHeight = bounds.height - totalSpacing;
        float topHeight = availableHeight * topFraction;
        float queueHeight = availableHeight * queueFraction;
        float garrisonHeight = availableHeight * garrisonFraction;

        // Reserve tab area above top scrollbox
        float tabHeight = bounds.height * tabHeightFraction;
        float topScrollHeight = topHeight - tabHeight; // actual scrollbox height

        float garrisonY = bounds.y + spacing;
        float queueY = garrisonY + garrisonHeight + spacing;
        float topY = queueY + queueHeight + spacing;

        // Top scrollbox (mob selection), shifted down by tabHeight
        mobSelectionScrollBox.bounds.set(
            bounds.x + horizontalPadding,
            topY, // y is bottom-left
            bounds.width - 2 * horizontalPadding,
            topScrollHeight
        );
        mobSelectionScrollBox.relayout();

        // Queue area
        queueScrollBox.bounds.set(
            bounds.x + horizontalPadding,
            queueY,
            bounds.width - 2 * horizontalPadding,
            queueHeight
        );
        queueScrollBox.relayout();

        // Garrison area
        garrisonScrollBox.bounds.set(
            bounds.x + horizontalPadding,
            garrisonY,
            bounds.width - 2 * horizontalPadding,
            garrisonHeight
        );
        garrisonScrollBox.relayout();
    }

    @Override
    protected void drawContent(ShapeRenderer renderer, SpriteBatch batch) {
        mobSelectionScrollBox.draw(renderer, batch, font, camera);
        queueScrollBox.draw(renderer, batch, font, camera);
        garrisonScrollBox.draw(renderer, batch, font, camera);

        // shapeRenderer calls remain outside for backgrounds
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        // draw tabs or other backgrounds
        renderer.end();
    }

    private void drawTabs(ShapeRenderer renderer, SpriteBatch batch) {
        float tabHeight = bounds.height * tabHeightFraction;
        float tabWidth = bounds.width / tabs.size();

        // Draw tabs slightly higher: aligned to top of modal
        float tabY = bounds.y + bounds.height - tabHeight;

        // Draw tab backgrounds (optional: highlight active tab lightly)
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < tabs.size(); i++) {
            if (i == activeTabIndex) {
                renderer.setColor(0.8f, 0.8f, 0.8f, 1f); // just a subtle highlight
            } else {
                renderer.setColor(0.6f, 0.6f, 0.6f, 1f);
            }

            renderer.rect(bounds.x + i * tabWidth, tabY, tabWidth, tabHeight);
        }
        renderer.end();
        switch (activeTabIndex) {
            case 0 -> mobSelectionScrollBox.setBackgroundColor(0.2f, 0.2f, 0.3f, 1f);
            case 1 -> mobSelectionScrollBox.setBackgroundColor(0.3f, 0.2f, 0.2f, 1f);
            case 2 -> mobSelectionScrollBox.setBackgroundColor(0.2f, 0.3f, 0.2f, 1f);
        }

        // Draw labels
        batch.begin();
        for (int i = 0; i < tabs.size(); i++) {
            String tab = tabs.get(i);
            font.setColor(0.1f, 0.1f, 0.1f, 1f); // dark label for contrast
            font.draw(batch, tab,
                bounds.x + i * tabWidth + 10,
                tabY + tabHeight * 0.7f);
        }
        batch.end();

        // Now draw the top scrollbox background based on active tab
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        switch (activeTabIndex) {
            case 0: renderer.setColor(0.9f, 0.9f, 1f, 1f); break; // All tab
            case 1: renderer.setColor(0.9f, 1f, 0.9f, 1f); break; // Type1 tab
            case 2: renderer.setColor(1f, 0.9f, 0.9f, 1f); break; // Type2 tab
            default: renderer.setColor(0.9f, 0.9f, 0.9f, 1f); break;
        }
        renderer.rect(
            mobSelectionScrollBox.bounds.x,
            mobSelectionScrollBox.bounds.y,
            mobSelectionScrollBox.bounds.width,
            mobSelectionScrollBox.bounds.height
        );
        renderer.end();
    }

    @Override
    protected boolean handleClickInside(float x, float y) {
        // Check tabs first
        float tabHeight = bounds.height * tabHeightFraction;
        if (y > bounds.y + bounds.height - tabHeight) {
            int clickedTab = (int) ((x - bounds.x) / (bounds.width / tabs.size()));
            if (clickedTab >= 0 && clickedTab < tabs.size()) {
                activeTabIndex = clickedTab;
                updateTopScrollBox(); // recompute entries + contentHeight
                return true;
            }
        }

        // Check scrollboxes
        if (mobSelectionScrollBox.contains(x, y) && mobSelectionScrollBox.click(x, y) != null) return true;
        if (queueScrollBox.contains(x, y) && queueScrollBox.click(x, y) != null) return true;
        if (garrisonScrollBox.contains(x, y) && garrisonScrollBox.click(x, y) != null) return true;

        return false;
    }

    @Override
    protected boolean handleScrollInside(float x, float y, float amountY) {
        if (mobSelectionScrollBox.contains(x, y)) mobSelectionScrollBox.scroll(amountY * 10f);
        if (queueScrollBox.contains(x, y)) queueScrollBox.scroll(amountY * 10f);
        if (garrisonScrollBox.contains(x, y)) garrisonScrollBox.scroll(amountY * 10f);
        return true;
    }
}
