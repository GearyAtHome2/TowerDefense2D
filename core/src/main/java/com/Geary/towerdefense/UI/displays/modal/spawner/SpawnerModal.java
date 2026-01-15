package com.Geary.towerdefense.UI.displays.modal.spawner;

import com.Geary.towerdefense.UI.displays.modal.Modal;
import com.Geary.towerdefense.UI.displays.modal.scrollbox.HorizontalScrollBox;
import com.Geary.towerdefense.UI.displays.modal.scrollbox.VerticalScrollBox;
import com.Geary.towerdefense.entity.mob.Mob;
import com.Geary.towerdefense.entity.resources.Resource;
import com.Geary.towerdefense.entity.spawner.FriendlySpawner;
import com.Geary.towerdefense.world.GameStateManager;
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
    private final GameStateManager gameStateManager;
    private final VerticalScrollBox<MobMenuEntry> mobScrollBox;
    private final HorizontalScrollBox<QueueEntry> queueScrollBox;
    private final HorizontalScrollBox<QueueEntry> garrisonScrollBox;

    private final SpawnerTabs tabs;
    private final SpawnerTabRenderer<SpawnerTabs.OrderTab> tabRenderer;


    public SpawnerModal(FriendlySpawner spawner, GameStateManager gameStateManager, BitmapFont font, OrthographicCamera camera) {
        super(font, camera);
        this.spawner = spawner;
        this.gameStateManager = gameStateManager;

        mobScrollBox = new VerticalScrollBox<>(0, 0, 0, 0);
        queueScrollBox = new HorizontalScrollBox<>(0, 0, 0, 0);
        garrisonScrollBox = new HorizontalScrollBox<>(0, 0, 0, 0);

        queueScrollBox.setEntries(new ArrayList<>());
        garrisonScrollBox.setEntries(new ArrayList<>());

        tabs = new SpawnerTabs(spawner, listener);
        tabRenderer = new SpawnerTabRenderer<>(font, camera);

        applyActiveTab();
    }

    private final MobMenuEntry.MobEntryListener listener = new MobMenuEntry.MobEntryListener() {
        @Override
        public void onRecruitClicked(MobMenuEntry entry, int amount) {
            System.out.println("recruiting "+amount+" "+entry.name+"s");
            for (int i = 0; i < amount; i++) {
                if (!gameStateManager.canAfford(entry.templateMob)) break;

                gameStateManager.consumeCost(entry.templateMob);
                add(queueScrollBox, entry.templateMob, false);
            }

            updateQueueLeftmost();
            updateAffordability();
        }

        @Override
        public void onGarrisonClicked(MobMenuEntry entry, int amount) {
            for (int i = 0; i < amount; i++) {
                if (!gameStateManager.canAfford(entry.templateMob)) break;

                gameStateManager.consumeCost(entry.templateMob);
                add(queueScrollBox, entry.templateMob, true);
            }

            updateAffordability();
        }
    };

    private void applyActiveTab() {
        float totalHeight = tabs.getActiveEntriesTotalHeight(5f);
        Color c = tabs.getActiveTabColor();

        List<MobMenuEntry> entries = tabs.getActiveEntries();

        for (MobMenuEntry entry : entries) {
            entry.setAffordable(canAfford(entry.templateMob));
        }

        mobScrollBox.setBackgroundColor(c.r, c.g, c.b, c.a);
        mobScrollBox.setEntries(entries, totalHeight);
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


//        processQueueCooldowns();

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
            int idx = (int) ((x - bounds.x) / (bounds.width / tabs.getTabs().size()));
            tabs.setActiveTabIndex(idx);
            applyActiveTab();
            return true;
        }

        if (mobScrollBox.contains(x, y) && mobScrollBox.click(x, y) != null) return true;
        if (garrisonScrollBox.contains(x, y) && garrisonScrollBox.click(x, y) != null) return true;

        if (queueScrollBox.contains(x, y)) {
            QueueEntry clicked = queueScrollBox.click(x, y);
            if (clicked != null) {
                removeFromRecruitQueue(clicked);
                return true;
            }
        }

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

    public void processQueueCooldowns(float delta) {
        for (QueueEntry entry : queueScrollBox.getEntries()) {
            entry.update(delta);
        }
        List<QueueEntry> queue = new ArrayList<>(queueScrollBox.getEntries());
        if (queue.isEmpty()) return;

        QueueEntry first = queue.get(0);
        if (first.isLeftmost && first.cooldownElapsed >= first.mob.spawnTime) {
            queue.remove(0);

            if (first.isToGarrison) {
                List<QueueEntry> garrison = new ArrayList<>(garrisonScrollBox.getEntries());
                garrison.add(new QueueEntry(first.mob, 0, 0, 50f, true));
                garrisonScrollBox.setEntries(garrison);
            } else {
                spawner.requestSpawn(List.of(first.mob));
            }

            if (!queue.isEmpty()) queue.get(0).isLeftmost = true;
            queueScrollBox.setEntries(queue);
        }
    }

    private void removeFromRecruitQueue(QueueEntry entry) {
        List<QueueEntry> queue = new ArrayList<>(queueScrollBox.getEntries());

        int idx = queue.indexOf(entry);
        if (idx == -1) return;

        // Do not allow removing garrison entries
//        if (entry.isToGarrison) return;

        boolean wasLeftmost = entry.isLeftmost;

        queue.remove(idx);

        // Fix leftmost + cooldown if we removed the front
        if (wasLeftmost && !queue.isEmpty()) {
            QueueEntry next = queue.get(0);
            next.isLeftmost = true;
            if (next.cooldownElapsed == 0f) {
                next.resetCooldown();
            }
        }

        queueScrollBox.setEntries(queue);
    }

    private boolean canAfford(Mob mob) {
        // Coins
        if (gameStateManager.gameState.getCoins() < mob.coinCost) return false;

        // Raw resources
        for (Resource.RawResourceType type : mob.rawResourceCost.keySet()) {
            if (gameStateManager.getRawResourceCount().get(type) < mob.rawResourceCost.get(type)) return false;
        }

        for (Resource.RefinedResourceType type : mob.refinedResourceCost.keySet()) {
            if (gameStateManager.getRefinedResourceCount().get(type) < mob.refinedResourceCost.get(type)) return false;
        }

        return true;
    }

    void updateAffordability() {
        for (MobMenuEntry entry : mobScrollBox.getEntries()) {
            boolean affordable = gameStateManager.canAfford(entry.templateMob);
            entry.setAffordable(affordable);
        }
    }
}
