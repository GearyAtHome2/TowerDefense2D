package com.Geary.towerdefense.UI.displays.modal.spawner;

import com.Geary.towerdefense.UI.displays.modal.Modal;
import com.Geary.towerdefense.UI.displays.modal.scrollbox.HorizontalScrollBox;
import com.Geary.towerdefense.UI.displays.modal.scrollbox.VerticalScrollBox;
import com.Geary.towerdefense.entity.mob.Mob;
import com.Geary.towerdefense.entity.spawner.FriendlySpawner;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.ArrayList;
import java.util.List;

public class SpawnerModal extends Modal {

    private static final float TAB_HEIGHT_FRAC = 0.08f;

    private final FriendlySpawner spawner;
    private final VerticalScrollBox<MobMenuEntry> mobScrollBox;
    private final HorizontalScrollBox<QueueEntry> queueScrollBox;
    private final HorizontalScrollBox<QueueEntry> garrisonScrollBox;

    private final SpawnerTabs tabs;
    private final SpawnerTabRenderer<SpawnerTabs.OrderTab> tabRenderer;

    public SpawnerModal(FriendlySpawner spawner, BitmapFont font, OrthographicCamera camera) {
        super(font, camera);
        this.spawner = spawner;

        mobScrollBox = new VerticalScrollBox<>(0, 0, 0, 0);
        queueScrollBox = new HorizontalScrollBox<>(0, 0, 0, 0);
        garrisonScrollBox = new HorizontalScrollBox<>(0, 0, 0, 0);

        queueScrollBox.setEntries(new ArrayList<>());
        garrisonScrollBox.setEntries(new ArrayList<>());

        tabs = new SpawnerTabs(spawner, listener);
        tabRenderer = new SpawnerTabRenderer<>(font, camera);

        applyActiveTab();
    }

    /* =========================
       Mob entry callbacks
       ========================= */

    private final MobMenuEntry.MobEntryListener listener = new MobMenuEntry.MobEntryListener() {
        @Override
        public void onRecruitClicked(MobMenuEntry entry) {
            add(queueScrollBox, entry.templateMob, false);
            updateQueueLeftmost();
        }

        @Override
        public void onGarrisonClicked(MobMenuEntry entry) {
            add(queueScrollBox, entry.templateMob, true);
        }
    };

    private void applyActiveTab() {
        float totalHeight = tabs.getActiveEntriesTotalHeight(5f);
        Color c = tabs.getActiveTabColor();
        mobScrollBox.setBackgroundColor(c.r, c.g, c.b, c.a);
        mobScrollBox.setEntries(tabs.getActiveEntries(), totalHeight);
    }

    /* =========================
       Layout
       ========================= */

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

        mobScrollBox.bounds.set(
            bounds.x + hPad,
            topY,
            bounds.width - hPad * 2,
            topHeight - tabHeight
        );
        mobScrollBox.relayout();

        queueScrollBox.bounds.set(bounds.x + hPad, queueY, bounds.width - hPad * 2, queueHeight);
        garrisonScrollBox.bounds.set(bounds.x + hPad, garrisonY, bounds.width - hPad * 2, garrisonHeight);
    }

    /* =========================
       Draw
       ========================= */

    @Override
    protected void drawContent(ShapeRenderer renderer, SpriteBatch batch) {
        tabRenderer.draw(
            renderer,
            batch,
            bounds,
            TAB_HEIGHT_FRAC,
            tabs.getTabs(),
            tabs.getActiveTabIndex(),
            tabs.getTabColors()
        );

        mobScrollBox.draw(renderer, batch, font, camera);

        float delta = com.badlogic.gdx.Gdx.graphics.getDeltaTime();
        for (QueueEntry entry : queueScrollBox.getEntries()) {
            entry.update(delta);
        }
        processQueueCooldowns();

        queueScrollBox.draw(renderer, batch, font, camera);
        garrisonScrollBox.draw(renderer, batch, font, camera);
    }

    /* =========================
       Input
       ========================= */

    @Override
    protected boolean handleClickInside(float x, float y) {
        float tabHeight = bounds.height * TAB_HEIGHT_FRAC;
        float tabY = bounds.y + bounds.height - tabHeight;

        if (y >= tabY) {
            int idx = (int)((x - bounds.x) / (bounds.width / tabs.getTabs().size()));
            tabs.setActiveTabIndex(idx);
            applyActiveTab();
            return true;
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

    /* =========================
       Queue logic
       ========================= */

    private void add(HorizontalScrollBox<QueueEntry> box, Mob mob, boolean toGarrison) {
        List<QueueEntry> entries = new ArrayList<>(box.getEntries());
        entries.add(new QueueEntry(mob, 0, 0, 50f, toGarrison));

        if (box == queueScrollBox && !entries.isEmpty()) {
            entries.get(0).isLeftmost = true;
            if (entries.get(0).cooldownElapsed == 0f) entries.get(0).resetCooldown();
        }

        box.setEntries(entries);
    }

    private void updateQueueLeftmost() {
        List<QueueEntry> entries = queueScrollBox.getEntries();
        for (int i = 0; i < entries.size(); i++) entries.get(i).isLeftmost = (i == 0);
    }

    private void processQueueCooldowns() {
        List<QueueEntry> queue = new ArrayList<>(queueScrollBox.getEntries());
        if (queue.isEmpty()) return;

        QueueEntry first = queue.get(0);
        if (first.isLeftmost && first.cooldownElapsed >= first.mob.spawnTime) {
            queue.remove(0);

            if (first.isToGarrison) {
                List<QueueEntry> garrison = new ArrayList<>(garrisonScrollBox.getEntries());
                garrison.add(new QueueEntry(first.mob, 0, 0, 50f, true));
                garrisonScrollBox.setEntries(garrison);
            }

            if (!queue.isEmpty()) queue.get(0).isLeftmost = true;
            queueScrollBox.setEntries(queue);
        }
    }
}
