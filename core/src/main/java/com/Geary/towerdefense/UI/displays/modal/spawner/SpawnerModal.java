package com.Geary.towerdefense.UI.displays.modal.spawner;

import com.Geary.towerdefense.UI.displays.modal.Modal;
import com.Geary.towerdefense.UI.displays.modal.scrollbox.HorizontalScrollBox;
import com.Geary.towerdefense.UI.displays.modal.scrollbox.ScrollBox;
import com.Geary.towerdefense.UI.displays.modal.scrollbox.ScrollEntry;
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

    private final ScrollBox<MobMenuEntry> mobSelectionScrollBox;
    private final HorizontalScrollBox<QueueEntry> queueScrollBox;
    private final HorizontalScrollBox<QueueEntry> garrisonScrollBox;

    private final List<String> tabs = List.of("All", "Type1", "Type2");
    private int activeTabIndex = 0;
    private final Map<String, List<MobMenuEntry>> tabEntries = new HashMap<>();

    public MobMenuEntry hoveredEntry;
    public MobMenuEntry activeEntry;

    private final float tabHeightFraction = 0.08f;

    private final MobMenuEntry.MobEntryListener listener = new MobMenuEntry.MobEntryListener() {
        @Override
        public void onRecruitClicked(MobMenuEntry entry) {
            addToQueue(entry.templateMob);
        }

        @Override
        public void onGarrisonClicked(MobMenuEntry entry) {
            addToGarrison(entry.templateMob);
        }
    };

    public SpawnerModal(FriendlySpawner spawner, BitmapFont font, OrthographicCamera camera) {
        super(font, camera);
        this.spawner = spawner;

        mobSelectionScrollBox = new ScrollBox<>(0, 0, 0, 0);
        queueScrollBox = new HorizontalScrollBox<>(0, 0, 0, 0);
        garrisonScrollBox = new HorizontalScrollBox<>(0, 0, 0, 0);

        setupTabEntries();
        populateTopScrollBox();
        populateQueueAndGarrison(); // initially empty
    }

    private void setupTabEntries() {
        for (String tab : tabs) {
            List<MobMenuEntry> entries = new ArrayList<>();
            for (Mob mob : spawner.getSpawnableMobs()) {
                MobMenuEntry entry = new MobMenuEntry(mob, 0, 0, 60, 60, listener);
                entry.onClick = () -> {
                    activeEntry = entry;
                    spawner.spawn();
                };
                entries.add(entry);
            }
            tabEntries.put(tab, entries);
        }
    }

    private void populateTopScrollBox() {
        List<MobMenuEntry> entries = tabEntries.get(tabs.get(activeTabIndex));
        float totalHeight = computeVerticalContentHeight(entries);
        mobSelectionScrollBox.setEntries(entries, totalHeight);
    }

    private float computeVerticalContentHeight(List<? extends ScrollEntry> entries) {
        float totalHeight = 0f;
        float spacing = 5f;
        for (ScrollEntry entry : entries) {
            totalHeight += entry.bounds().height + spacing;
        }
        if (!entries.isEmpty()) totalHeight -= spacing;
        return totalHeight;
    }

    @Override
    protected void layoutButtons() {
        float hPad = bounds.width * 0.02f;
        float spacing = bounds.height * 0.02f;

        float topFrac = 0.6f;
        float queueFrac = 0.2f;
        float garrisonFrac = 0.18f;

        float totalSpacing = spacing * 3;
        float availableHeight = bounds.height - totalSpacing;

        float topHeight = availableHeight * topFrac;
        float queueHeight = availableHeight * queueFrac;
        float garrisonHeight = availableHeight * garrisonFrac;

        float tabHeight = bounds.height * tabHeightFraction;
        float topScrollHeight = topHeight - tabHeight;

        float garrisonY = bounds.y + spacing;
        float queueY = garrisonY + garrisonHeight + spacing;
        float topY = queueY + queueHeight + spacing;

        mobSelectionScrollBox.bounds.set(bounds.x + hPad, topY, bounds.width - 2 * hPad, topScrollHeight);
        mobSelectionScrollBox.relayout();

        queueScrollBox.bounds.set(bounds.x + hPad, queueY, bounds.width - 2 * hPad, queueHeight);
        garrisonScrollBox.bounds.set(bounds.x + hPad, garrisonY, bounds.width - 2 * hPad, garrisonHeight);
    }

    @Override
    protected void drawContent(ShapeRenderer renderer, SpriteBatch batch) {
        mobSelectionScrollBox.draw(renderer, batch, font, camera);
        queueScrollBox.draw(renderer, batch, font);
        garrisonScrollBox.draw(renderer, batch, font);
    }

    private void drawTabs(ShapeRenderer renderer, SpriteBatch batch) {
        float tabHeight = bounds.height * tabHeightFraction;
        float tabWidth = bounds.width / tabs.size();
        float tabY = bounds.y + bounds.height - tabHeight;

        renderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < tabs.size(); i++) {
            renderer.setColor(i == activeTabIndex ? 0.8f : 0.6f, i == activeTabIndex ? 0.8f : 0.6f, i == activeTabIndex ? 0.8f : 0.6f, 1f);
            renderer.rect(bounds.x + i * tabWidth, tabY, tabWidth, tabHeight);
        }
        renderer.end();

        batch.begin();
        for (int i = 0; i < tabs.size(); i++) {
            font.setColor(0.1f, 0.1f, 0.1f, 1f);
            font.draw(batch, tabs.get(i), bounds.x + i * tabWidth + 10, tabY + tabHeight * 0.7f);
        }
        batch.end();
    }

    /** --- HORIZONTAL SCROLL LOGIC --- **/

    private void addToQueue(Mob mob) {
        List<QueueEntry> entries = new ArrayList<>(queueScrollBox.getEntries());
        QueueEntry newEntry = new QueueEntry(mob, 0, 0, 50f);
        entries.add(newEntry);
        queueScrollBox.setEntries(entries); // no width needed
    }

    private void addToGarrison(Mob mob) {
        List<QueueEntry> entries = new ArrayList<>(garrisonScrollBox.getEntries());
        QueueEntry newEntry = new QueueEntry(mob, 0, 0, 50f);
        entries.add(newEntry);
        garrisonScrollBox.setEntries(entries); // no width needed
    }

    /** Layout entries in a single horizontal row, returning total content width */
    private float layoutHorizontalLine(List<? extends ScrollEntry> entries, com.badlogic.gdx.math.Rectangle bounds, float size, float spacing) {
        float xOffset = bounds.x;
        float yOffset = bounds.y + (bounds.height - size) / 2f; // vertically centered

        for (ScrollEntry entry : entries) {
            entry.bounds().set(xOffset, yOffset, size, size);
            xOffset += size + spacing;
        }

        return xOffset - bounds.x; // total width of all entries
    }

    @Override
    protected boolean handleClickInside(float x, float y) {
        float tabHeight = bounds.height * tabHeightFraction;
        if (y > bounds.y + bounds.height - tabHeight) {
            int clickedTab = (int) ((x - bounds.x) / (bounds.width / tabs.size()));
            if (clickedTab >= 0 && clickedTab < tabs.size()) {
                activeTabIndex = clickedTab;
                populateTopScrollBox();
                return true;
            }
        }

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

    /** Initialize horizontal scrollboxes empty */
    private void populateQueueAndGarrison() {
        queueScrollBox.setEntries(new ArrayList<>());
        garrisonScrollBox.setEntries(new ArrayList<>());
    }
}
