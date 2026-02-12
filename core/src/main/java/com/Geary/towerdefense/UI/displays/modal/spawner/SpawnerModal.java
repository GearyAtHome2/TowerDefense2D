package com.Geary.towerdefense.UI.displays.modal.spawner;

import com.Geary.towerdefense.UI.displays.modal.BaseScrollModal;
import com.Geary.towerdefense.UI.displays.modal.scrollbox.HorizontalScrollBox;
import com.Geary.towerdefense.UI.displays.modal.scrollbox.VerticalScrollBox;
import com.Geary.towerdefense.entity.mob.Mob;
import com.Geary.towerdefense.entity.spawner.FriendlySpawner;
import com.Geary.towerdefense.world.GameStateManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class SpawnerModal extends BaseScrollModal {

    private static final float TAB_HEIGHT_FRAC = 0.08f;

    private final com.badlogic.gdx.graphics.g2d.GlyphLayout glyphLayout
        = new com.badlogic.gdx.graphics.g2d.GlyphLayout();

    private final FriendlySpawner spawner;
    private final GameStateManager gameStateManager;

    private final VerticalScrollBox<MobMenuEntry> mobScrollBox;
    private final HorizontalScrollBox<QueueEntry> queueScrollBox;
    private final HorizontalScrollBox<QueueEntry> garrisonScrollBox;

    private final SpawnerTabs tabs;
    private final SpawnerTabRenderer<SpawnerTabs.OrderTab> tabRenderer;

    private final Rectangle deployGarrisonButton = new Rectangle();
    private boolean deployHovered;

    private MobMenuEntry selectedMob;  // NEW: currently selected mob in top list
    private final Rectangle infoBox = new Rectangle(); // NEW: left half info box
    private final Rectangle infoRecruitButton = new Rectangle();
    private final Rectangle infoGarrisonButton = new Rectangle();

    public SpawnerModal(
        FriendlySpawner spawner,
        GameStateManager gameStateManager,
        BitmapFont font,
        OrthographicCamera camera
    ) {
        super(font, camera);
        this.spawner = spawner;
        this.gameStateManager = gameStateManager;

        mobScrollBox = new VerticalScrollBox<>(0, 0, 0, 0);
        queueScrollBox = new HorizontalScrollBox<>(0, 0, 0, 0);
        garrisonScrollBox = new HorizontalScrollBox<>(0, 0, 0, 0);

        queueScrollBox.setEntries(new ArrayList<>());
        garrisonScrollBox.setEntries(new ArrayList<>());

        tabs = new SpawnerTabs(spawner, mobEntryListener);
        tabRenderer = new SpawnerTabRenderer<>(font, camera);

        applyActiveTab();
        registerScrollBox(mobScrollBox);
        registerScrollBox(queueScrollBox);
        registerScrollBox(garrisonScrollBox);
    }

    private final MobMenuEntry.MobEntryListener mobEntryListener =
        new MobMenuEntry.MobEntryListener() {
            @Override
            public void onRecruitClicked(MobMenuEntry entry, int amount) {
                setSelectedMob(entry);
                for (int i = 0; i < amount; i++) {
                    if (!gameStateManager.canAfford(entry.templateMob)) break;
                    gameStateManager.consumeCost(entry.templateMob);
                    add(queueScrollBox, entry.templateMob, false);
                }
                updateQueueLeftmost();
                applyActiveTab();
            }

            @Override
            public void onGarrisonClicked(MobMenuEntry entry, int amount) {
                setSelectedMob(entry);
                for (int i = 0; i < amount; i++) {
                    if (!gameStateManager.canAfford(entry.templateMob)) break;
                    gameStateManager.consumeCost(entry.templateMob);
                    add(queueScrollBox, entry.templateMob, true);
                }
                applyActiveTab();
            }
        };

    private void setSelectedMob(MobMenuEntry entry) {
        if (selectedMob != null) {
            selectedMob.selected = false;
        }

        selectedMob = entry;

        if (selectedMob != null) {
            selectedMob.selected = true;
        }
    }

    private void applyActiveTab() {
        Color color = tabs.getActiveTabColor();
        List<MobMenuEntry> entries = tabs.getActiveEntries(gameStateManager);
        mobScrollBox.setBackgroundColor(color.r, color.g, color.b, color.a);
        mobScrollBox.setEntries(entries);
    }

    @Override
    protected void layoutButtons() {
        float hPad = bounds.width * 0.02f;
        float vGap = bounds.height * 0.02f;
        float tabHeight = bounds.height * TAB_HEIGHT_FRAC;

        float usableHeight = bounds.height - vGap * 3;
        float topHeight = usableHeight * 0.6f;
        float queueHeight = usableHeight * 0.2f;
        float garrisonHeight = usableHeight * 0.18f;

        float garrisonY = bounds.y + vGap;
        float queueY = garrisonY + garrisonHeight + vGap;
        float topY = queueY + queueHeight + vGap;

        // --- Top section split ---
        float infoBoxWidth = (bounds.width - hPad * 3) * 0.5f;
        infoBox.set(bounds.x + hPad, topY, infoBoxWidth, topHeight - tabHeight);

        float infoPad = infoBox.height * 0.05f;
        float buttonH = infoBox.height * 0.12f; // ~1/3 of previous 0.18 → smaller
        float buttonGap = infoPad * 0.5f;        // gap between buttons
        float buttonW = (infoBox.width - infoPad * 2 - buttonGap) * 0.5f; // half width each
        float buttonY = infoBox.y + infoPad;

        infoRecruitButton.set(
            infoBox.x + infoPad,
            buttonY,
            buttonW,
            buttonH
        );

        infoGarrisonButton.set(
            infoBox.x + infoPad + buttonW + buttonGap,
            buttonY,
            buttonW,
            buttonH
        );

        mobScrollBox.bounds.set(
            bounds.x + hPad * 2 + infoBoxWidth, // right half
            topY,
            bounds.width - hPad * 3 - infoBoxWidth,
            topHeight - tabHeight
        );
        mobScrollBox.relayout();

        // --- Bottom scrollboxes unchanged ---
        queueScrollBox.bounds.set(bounds.x + hPad, queueY, bounds.width - hPad * 2, queueHeight);
        garrisonScrollBox.bounds.set(bounds.x + hPad, garrisonY, bounds.width - hPad * 2, garrisonHeight);

        float buttonWidth = garrisonScrollBox.bounds.width * 0.35f;
        float buttonHeight = garrisonScrollBox.bounds.height * 0.25f;
        deployGarrisonButton.set(
            garrisonScrollBox.bounds.x + hPad * 0.5f,
            garrisonScrollBox.bounds.y + garrisonScrollBox.bounds.height - buttonHeight - vGap * 0.5f,
            buttonWidth,
            buttonHeight
        );
    }

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

        // --- Info box background ---
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(0.14f, 0.14f, 0.14f, 1f);
        renderer.rect(infoBox.x, infoBox.y, infoBox.width, infoBox.height);
        renderer.end();

        if (selectedMob != null) {
            drawInfoText(batch);
            drawInfoButtons(renderer, batch);
        }

        // --- Scrollboxes ---
        mobScrollBox.draw(renderer, batch, font, camera);
        queueScrollBox.draw(renderer, batch, font, camera);
        garrisonScrollBox.draw(renderer, batch, font, camera);

        // --- Deploy button ---
        deployHovered = deployGarrisonButton.contains(
            Gdx.input.getX(),
            Gdx.graphics.getHeight() - Gdx.input.getY()
        );

        Color bg = deployHovered
            ? new Color(0.25f, 0.25f, 0.25f, 1f)
            : new Color(0.18f, 0.18f, 0.18f, 1f);

        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(bg);
        renderer.rect(
            deployGarrisonButton.x,
            deployGarrisonButton.y,
            deployGarrisonButton.width,
            deployGarrisonButton.height
        );
        renderer.end();

        batch.begin();
        font.setColor(Color.WHITE);
        font.draw(
            batch,
            "DEPLOY",
            deployGarrisonButton.x + deployGarrisonButton.width * 0.3f,
            deployGarrisonButton.y + deployGarrisonButton.height * 0.65f
        );
        batch.end();
    }

    @Override
    protected boolean handleClickInside(float x, float y) {

        float tabHeight = bounds.height * TAB_HEIGHT_FRAC;
        float tabY = bounds.y + bounds.height - tabHeight;

        // --- Tabs ---
        if (y >= tabY) {
            int idx = (int) ((x - bounds.x) / (bounds.width / tabs.getTabs().size()));
            tabs.setActiveTabIndex(idx);
            mobScrollBox.resetScroll();
            applyActiveTab();
            return true;
        }

        // --- Info Box Buttons ---
        if (selectedMob != null) {

            boolean shift =
                Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) ||
                    Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);

            int amount = shift ? 5 : 1;

            if (infoRecruitButton.contains(x, y)) {
                mobEntryListener.onRecruitClicked(selectedMob, amount);
                return true;
            }

            if (infoGarrisonButton.contains(x, y)) {
                mobEntryListener.onGarrisonClicked(selectedMob, amount);
                return true;
            }
        }

        // --- Mob selection ---
        if (mobScrollBox.contains(x, y)) {
            MobMenuEntry clicked = mobScrollBox.click(x, y);
            if (clicked != null) {
                setSelectedMob(clicked);
            }
            return true;
        }

        // --- Garrison scrollbox ---
        if (garrisonScrollBox.contains(x, y)) {
            garrisonScrollBox.click(x, y);
            return true;
        }

        // --- Queue scrollbox ---
        if (queueScrollBox.contains(x, y)) {
            QueueEntry clicked = queueScrollBox.click(x, y);
            if (clicked != null) {
                removeFromRecruitQueue(clicked);
            }
            return true;
        }

        // --- Deploy button ---
        if (deployGarrisonButton.contains(x, y)) {
            deployGarrison();
            return true;
        }

        return false;
    }

    void updateAffordability() {
        applyActiveTab();
    }

    private void drawInfoText(SpriteBatch batch) {
        float pad = infoBox.height * 0.05f;

        float topY = infoBox.y + infoBox.height - pad;
        float bottomY = infoBox.y + infoRecruitButton.height + pad * 1.5f;

        float flavourY = infoBox.y + infoBox.height*0.25f;
        drawNameAndStats(batch, topY);
        drawEffectText(batch, topY-44f, bottomY + infoBox.height * 0.25f);
        drawFlavourText(batch, flavourY);
    }

    private void drawNameAndStats(SpriteBatch batch, float topY) {
        Mob mob = selectedMob.templateMob;

        batch.begin();
        font.setColor(Color.WHITE);

        // Name
        font.draw(batch, mob.name, infoBox.x + 10, topY);

        // Health + armour
        float statsY = topY - 22;
        String healthText = "Health: " + mob.health;

        font.setColor(Color.WHITE);
        font.draw(batch, healthText, infoBox.x + 10, statsY);

        glyphLayout.setText(font, healthText);
        float healthWidth = glyphLayout.width;

        if (mob.armour > 0) {
            font.setColor(Color.LIGHT_GRAY);
            font.draw(
                batch,
                "[+" + mob.armour + "]",
                infoBox.x + 10 + healthWidth + 6, // small gap
                statsY
            );
        }

        batch.end();
    }

    private void drawEffectText(SpriteBatch batch, float topY, float bottomY) {
        Mob mob = selectedMob.templateMob;

        float width = infoBox.width - 20;
        float maxHeight = topY - bottomY;
        if (maxHeight <= 0) return;

        glyphLayout.setText(
            font,
            mob.effectText,
            Color.WHITE,
            width,
            com.badlogic.gdx.utils.Align.left,
            true
        );

        float textHeight = glyphLayout.height;
        float drawY = topY;

        // If text is taller than allowed space, just draw as much as fits
        if (textHeight > maxHeight) {
            drawY = bottomY + maxHeight;
        }

        batch.begin();
        font.setColor(Color.WHITE);
        font.draw(
            batch,
            glyphLayout,
            infoBox.x + 10,
            drawY
        );
        batch.end();
    }

    private void drawFlavourText(SpriteBatch batch, float bottomY) {
        Mob mob = selectedMob.templateMob;

        float width = infoBox.width - 20;

        // Measure wrapped flavour text
        glyphLayout.setText(
            font,
            mob.flavourText,
            new Color(1f, 1f, 1f, 0.5f),
            width,
            com.badlogic.gdx.utils.Align.left,
            true
        );

        float textHeight = glyphLayout.height;

        // Shift upward so bottom line stays anchored
        float drawY = bottomY + textHeight;

        batch.begin();
        font.setColor(1f, 1f, 1f, 0.5f);
        font.draw(
            batch,
            glyphLayout,
            infoBox.x + 10,
            drawY
        );
        batch.end();
    }

    private void add(HorizontalScrollBox<QueueEntry> box, Mob mob, boolean toGarrison) {
        List<QueueEntry> entries = new ArrayList<>(box.getEntries());
        entries.add(new QueueEntry(mob, box.bounds.height * 0.8f, toGarrison));

        if (box == queueScrollBox && !entries.isEmpty()) {
            entries.get(0).isLeftmost = true;
            if (entries.get(0).cooldownElapsed == 0f) entries.get(0).resetCooldown();
        }

        box.setEntries(entries);
    }

    private void updateQueueLeftmost() {
        List<QueueEntry> entries = queueScrollBox.getEntries();
        for (int i = 0; i < entries.size(); i++) entries.get(i).isLeftmost = i == 0;
    }

    public void processQueueCooldowns(float delta) {
        for (QueueEntry entry : queueScrollBox.getEntries()) entry.update(delta);

        List<QueueEntry> queue = new ArrayList<>(queueScrollBox.getEntries());
        if (queue.isEmpty()) return;

        QueueEntry first = queue.get(0);
        if (first.isLeftmost && first.cooldownElapsed >= first.mob.spawnTime) {
            queue.remove(0);

            if (first.isToGarrison) {
                List<QueueEntry> garrison = new ArrayList<>(garrisonScrollBox.getEntries());
                garrison.add(new QueueEntry(first.mob, garrisonScrollBox.bounds.height * 0.65f, true));
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

        boolean wasLeftmost = entry.isLeftmost;
        queue.remove(idx);

        if (wasLeftmost && !queue.isEmpty()) {
            QueueEntry next = queue.get(0);
            next.isLeftmost = true;
            if (next.cooldownElapsed == 0f) next.resetCooldown();
        }

        queueScrollBox.setEntries(queue);
    }

    private void deployGarrison() {
        List<QueueEntry> garrison = new ArrayList<>(garrisonScrollBox.getEntries());
        if (garrison.isEmpty()) return;

        List<Mob> mobs = new ArrayList<>();
        for (QueueEntry entry : garrison) mobs.add(entry.mob);

        spawner.requestSpawn(mobs);
        garrisonScrollBox.setEntries(new ArrayList<>());
    }

    private void drawInfoButtons(ShapeRenderer renderer, SpriteBatch batch) {
        boolean affordable = gameStateManager.canAfford(selectedMob.templateMob);

        renderer.begin(ShapeRenderer.ShapeType.Filled);

        renderer.setColor(
            affordable ? 0.1f : 0.15f,
            affordable ? 0.7f : 0.35f,
            affordable ? 0.1f : 0.15f,
            1f
        );
        renderer.rect(
            infoRecruitButton.x,
            infoRecruitButton.y,
            infoRecruitButton.width,
            infoRecruitButton.height
        );

        renderer.setColor(
            affordable ? 0.5f : 0.25f,
            affordable ? 0.5f : 0.25f,
            affordable ? 0.5f : 0.25f,
            1f
        );
        renderer.rect(
            infoGarrisonButton.x,
            infoGarrisonButton.y,
            infoGarrisonButton.width,
            infoGarrisonButton.height
        );

        renderer.end();

        batch.begin();
        font.setColor(Color.WHITE);

        boolean shift = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)
            || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);

        font.draw(
            batch,
            shift ? "Recruit x5" : "Recruit",
            infoRecruitButton.x + 6,
            infoRecruitButton.y + infoRecruitButton.height * 0.65f
        );

        font.draw(
            batch,
            shift ? "Garrison x5" : "Garrison",
            infoGarrisonButton.x + 6,
            infoGarrisonButton.y + infoGarrisonButton.height * 0.65f
        );

        batch.end();
    }

}
