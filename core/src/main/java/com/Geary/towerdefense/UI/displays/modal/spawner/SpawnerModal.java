package com.Geary.towerdefense.UI.displays.modal.spawner;

import com.Geary.towerdefense.UI.displays.modal.Modal;
import com.Geary.towerdefense.UI.displays.modal.scrollbox.HorizontalScrollBox;
import com.Geary.towerdefense.UI.displays.modal.scrollbox.VerticalScrollBox;
import com.Geary.towerdefense.entity.mob.Mob;
import com.Geary.towerdefense.entity.spawner.FriendlySpawner;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.*;

public class SpawnerModal extends Modal {

    private enum OrderTab {
        ALL,
        NEUTRAL,
        TECH,
        NATURE,
        DARK,
        LIGHT,
        FIRE,
        WATER
    }

    private final Map<OrderTab, Color> tabColors = new HashMap<>();
    private final FriendlySpawner spawner;
    private final VerticalScrollBox<MobMenuEntry> mobScrollBox;
    private final HorizontalScrollBox<QueueEntry> queueScrollBox;
    private final HorizontalScrollBox<QueueEntry> garrisonScrollBox;
    private final List<OrderTab> tabs = new ArrayList<>();
    private final Map<OrderTab, List<MobMenuEntry>> tabEntries = new EnumMap<>(OrderTab.class);
    private int activeTab = 0;
    private static final float TAB_HEIGHT_FRAC = 0.08f;

    private final MobMenuEntry.MobEntryListener listener = new MobMenuEntry.MobEntryListener() {
        @Override
        public void onRecruitClicked(MobMenuEntry entry) {
            add(queueScrollBox, entry.templateMob, false);
            updateQueueLeftmost();
        }

        @Override
        public void onGarrisonClicked(MobMenuEntry entry) {
            System.out.println("setting up garrison mob");
            add(queueScrollBox, entry.templateMob, true);
        }
    };

    public SpawnerModal(FriendlySpawner spawner, BitmapFont font, OrthographicCamera camera) {
        super(font, camera);
        this.spawner = spawner;

        mobScrollBox = new VerticalScrollBox<>(0, 0, 0, 0);
        queueScrollBox = new HorizontalScrollBox<>(0, 0, 0, 0);
        garrisonScrollBox = new HorizontalScrollBox<>(0, 0, 0, 0);

        buildTabs();
        populateTabColours();
        applyActiveTab();

        queueScrollBox.setEntries(new ArrayList<>());
        garrisonScrollBox.setEntries(new ArrayList<>());
    }

    private void buildTabs() {
        tabs.clear();
        tabEntries.clear();
        tabs.add(OrderTab.ALL);

        for (Mob.Order order : Mob.Order.values()) {
            tabs.add(OrderTab.valueOf(order.name()));
        }

        for (OrderTab tab : tabs) {
            List<MobMenuEntry> entries = new ArrayList<>();
            for (Mob mob : spawner.getSpawnableMobs()) {
                if (tab != OrderTab.ALL && !mob.order.name().equals(tab.name())) continue;

                MobMenuEntry entry = new MobMenuEntry(mob, 0, 0, 60, 60, listener);
                entry.onClick = () -> {};
                entries.add(entry);
            }
            tabEntries.put(tab, entries);
        }
    }

    private void populateTabColours() {
        tabColors.put(OrderTab.ALL, new Color(0.6f, 0.6f, 0.6f, 1f));
        tabColors.put(OrderTab.NEUTRAL, new Color(0.6f, 0.6f, 0.6f, 1f));
        tabColors.put(OrderTab.TECH, new Color(0.4f, 0.5f, 0.7f, 1f));
        tabColors.put(OrderTab.NATURE, new Color(0.2f, 0.7f, 0.2f, 1f));
        tabColors.put(OrderTab.DARK, new Color(0.1f, 0.1f, 0.1f, 1f));
        tabColors.put(OrderTab.LIGHT, new Color(0.8f, 0.8f, 0.5f, 1f));
        tabColors.put(OrderTab.FIRE, new Color(0.9f, 0.2f, 0.1f, 1f));
        tabColors.put(OrderTab.WATER, new Color(0.2f, 0.4f, 0.9f, 1f));
    }

    private void applyActiveTab() {
        OrderTab tab = tabs.get(activeTab);
        Color tabColor = tabColors.getOrDefault(tab, new Color(0.6f, 0.6f, 0.6f, 1f));
        List<MobMenuEntry> entries = tabEntries.get(tab);

        float totalHeight = (float) entries.stream()
            .mapToDouble(e -> e.bounds().height + 5f)
            .sum();
        if (!entries.isEmpty()) totalHeight -= 5f;

        mobScrollBox.setBackgroundColor(tabColor.r, tabColor.g, tabColor.b, tabColor.a);
        mobScrollBox.setEntries(entries, totalHeight);
    }

    @Override
    protected void layoutButtons() {
        float hPad = bounds.width * 0.02f;
        float vGap = bounds.height * 0.02f;
        float tabHeight = bounds.height * TAB_HEIGHT_FRAC;

        float topFrac = 0.6f;
        float queueFrac = 0.2f;
        float garrisonFrac = 0.18f;
        float usableHeight = bounds.height - vGap * 3;
        float topHeight = usableHeight * topFrac;
        float queueHeight = usableHeight * queueFrac;
        float garrisonHeight = usableHeight * garrisonFrac;

        float garrisonY = bounds.y + vGap;
        float queueY = garrisonY + garrisonHeight + vGap;
        float topY = queueY + queueHeight + vGap;

        mobScrollBox.bounds.set(bounds.x + hPad, topY, bounds.width - hPad * 2, topHeight - tabHeight);
        mobScrollBox.relayout();

        queueScrollBox.bounds.set(bounds.x + hPad, queueY, bounds.width - hPad * 2, queueHeight);
        garrisonScrollBox.bounds.set(bounds.x + hPad, garrisonY, bounds.width - hPad * 2, garrisonHeight);
    }

    @Override
    protected void drawContent(ShapeRenderer renderer, SpriteBatch batch) {
        drawTabs(renderer, batch);
        mobScrollBox.draw(renderer, batch, font, camera);

        float delta = com.badlogic.gdx.Gdx.graphics.getDeltaTime();
        for (QueueEntry entry : queueScrollBox.getEntries()) {
            entry.update(delta);
        }
        processQueueCooldowns();

        queueScrollBox.draw(renderer, batch, font, camera);
        garrisonScrollBox.draw(renderer, batch, font, camera);
    }

    private void drawTabs(ShapeRenderer renderer, SpriteBatch batch) {
        float tabHeight = bounds.height * TAB_HEIGHT_FRAC;
        float tabWidth = bounds.width / tabs.size();
        float y = bounds.y + bounds.height - tabHeight;

        renderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < tabs.size(); i++) {
            OrderTab tab = tabs.get(i);
            Color baseColor = tabColors.getOrDefault(tab, new Color(0.6f, 0.6f, 0.6f, 1f));
            Color drawColor = new Color(baseColor);
            if (i == activeTab) {
                if (tab == OrderTab.LIGHT) drawColor.set(Color.WHITE);
                else drawColor.lerp(Color.WHITE, 0.3f);
            }
            renderer.setColor(drawColor);
            renderer.rect(bounds.x + i * tabWidth, y, tabWidth, tabHeight);
        }
        renderer.end();

        batch.begin();
        for (int i = 0; i < tabs.size(); i++) {
            OrderTab tab = tabs.get(i);
            String text = tab.name();

            GlyphLayout layout = new GlyphLayout(font, text);
            float maxTextWidth = tabWidth - 10;
            float scale = Math.min(1f, maxTextWidth / layout.width);
            font.getData().setScale(scale);

            Color baseColor = tabColors.getOrDefault(tab, new Color(0.6f, 0.6f, 0.6f, 1f));
            float brightness = 0.299f * baseColor.r + 0.587f * baseColor.g + 0.114f * baseColor.b;
            if (i == activeTab) {
                Color c = new Color(baseColor).lerp(Color.WHITE, 0.3f);
                float activeBrightness = 0.299f * c.r + 0.587f * c.g + 0.114f * c.b;
                font.setColor(activeBrightness > 0.6f ? Color.BLACK : Color.WHITE);
            } else {
                font.setColor(brightness > 0.6f ? Color.BLACK : Color.WHITE);
            }

            float textX = bounds.x + i * tabWidth + (tabWidth - layout.width * scale) / 2f;
            float textY = y + tabHeight * 0.7f;
            font.draw(batch, text, textX, textY);
        }
        batch.end();
        font.getData().setScale(1f);
    }

    private void add(HorizontalScrollBox<QueueEntry> box, Mob mob, boolean toGarrison) {
        List<QueueEntry> entries = new ArrayList<>(box.getEntries());
        QueueEntry newEntry = new QueueEntry(mob, 0, 0, 50f, toGarrison);
        entries.add(newEntry);

        if (box == queueScrollBox) {
            for (int i = 0; i < entries.size(); i++) {
                entries.get(i).isLeftmost = (i == 0);
            }
            if (!entries.isEmpty() && entries.get(0).cooldownElapsed == 0f) {
                entries.get(0).resetCooldown();
            }
        }
        box.setEntries(entries);
    }

    private void updateQueueLeftmost() {
        List<QueueEntry> entries = queueScrollBox.getEntries();
        for (int i = 0; i < entries.size(); i++) {
            entries.get(i).isLeftmost = (i == 0);
        }
    }

    private void processQueueCooldowns() {
        List<QueueEntry> queue = new ArrayList<>(queueScrollBox.getEntries());
        if (queue.isEmpty()) return;

        QueueEntry first = queue.get(0);
        if (first.isLeftmost && first.cooldownElapsed >= first.mob.spawnTime) {
            queue.remove(0);

            if (first.isToGarrison) {
                List<QueueEntry> garrison = new ArrayList<>(garrisonScrollBox.getEntries());
                QueueEntry garrisonEntry = new QueueEntry(first.mob, 0, 0, 50f, true);
                garrison.add(garrisonEntry);
                garrisonScrollBox.setEntries(garrison);
            }

            first.resetCooldown();

            if (!queue.isEmpty()) queue.get(0).isLeftmost = true;
            queueScrollBox.setEntries(queue);
        }
    }

    @Override
    protected boolean handleClickInside(float x, float y) {
        float tabHeight = bounds.height * TAB_HEIGHT_FRAC;
        float tabY = bounds.y + bounds.height - tabHeight;

        if (y >= tabY) {
            int idx = (int) ((x - bounds.x) / (bounds.width / tabs.size()));
            if (idx >= 0 && idx < tabs.size()) {
                activeTab = idx;
                applyActiveTab();
                return true;
            }
        }

        if (mobScrollBox.contains(x, y) && mobScrollBox.click(x, y) != null) return true;
        if (queueScrollBox.contains(x, y) && queueScrollBox.click(x, y) != null) return true;
        if (garrisonScrollBox.contains(x, y) && garrisonScrollBox.click(x, y) != null) return true;

        return false;
    }

    @Override
    protected boolean handleScrollInside(float x, float y, float amountY) {
        if (mobScrollBox.contains(x, y)) mobScrollBox.scroll(amountY * 10f);
        if (queueScrollBox.contains(x, y)) queueScrollBox.scroll(amountY * 10f);
        if (garrisonScrollBox.contains(x, y)) garrisonScrollBox.scroll(amountY * 10f);
        return true;
    }
}
