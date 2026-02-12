package com.Geary.towerdefense.UI.displays.modal.tower;

import com.Geary.towerdefense.UI.displays.modal.BaseScrollModal;
import com.Geary.towerdefense.UI.displays.modal.scrollbox.VerticalScrollBox;
import com.Geary.towerdefense.behaviour.targeting.TargetingHelper;
import com.Geary.towerdefense.entity.buildings.tower.Tower;
import com.Geary.towerdefense.entity.mob.bullet.BulletRepr;
import com.Geary.towerdefense.world.GameStateManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.List;

public class TowerModal extends BaseScrollModal {

    private final Tower tower;
    private final GameStateManager gameStateManager;

    private final VerticalScrollBox<BulletScrollEntry> ammoScrollBox;
    private final VerticalScrollBox<TargetingModeScrollEntry> targetingScrollBox;

    private BulletScrollEntry selectedAmmoEntry;
    private BulletScrollEntry activeAmmoEntry;

    private TargetingModeScrollEntry selectedTargeting;
    private TargetingModeScrollEntry activeTargeting;

    private final InfoBoxRenderer ammoInfoBox;
    private final InfoBoxRenderer targetingInfoBox;

    private final Rectangle ammoInfoBounds = new Rectangle();
    private final Rectangle targetingInfoBounds = new Rectangle();

    private final GlyphLayout layout = new GlyphLayout();

    private static class LayoutCfg {
        float padding = 0.04f;
        float splitRatio = 0.5f;
        float leftRatio = 0.45f;
        float titleScale = 1.4f;
    }

    private final LayoutCfg cfg = new LayoutCfg();

    public TowerModal(
        Tower tower,
        GameStateManager gameStateManager,
        BitmapFont font,
        OrthographicCamera camera
    ) {
        super(font, camera);
        this.tower = tower;
        this.gameStateManager = gameStateManager;

        ammoInfoBox = new InfoBoxRenderer(font);
        targetingInfoBox = new InfoBoxRenderer(font);

        ammoScrollBox = new VerticalScrollBox<>(0, 0, 0, 0);
        targetingScrollBox = new VerticalScrollBox<>(0, 0, 0, 0);

        populateAmmo();
        populateTargeting();
        restoreActiveState();
    }

    // ------------------------------------------------------------------------

    @Override
    protected void layoutButtons() {
        float pad = bounds.width * cfg.padding;
        float leftWidth = bounds.width * cfg.leftRatio;
        float halfHeight = bounds.height * 0.5f - pad * 2;
        float sectionHeight = bounds.height * cfg.splitRatio - pad * 1.5f;

        float topY = bounds.y + bounds.height - pad - sectionHeight;
        float bottomY = bounds.y + pad;

        layoutSection(ammoScrollBox, topY, sectionHeight);
        layoutSection(targetingScrollBox, bottomY, sectionHeight);

        ammoInfoBounds.set(
            bounds.x + pad,
            bounds.y + bounds.height * 0.5f,
            leftWidth,
            halfHeight
        );

        targetingInfoBounds.set(
            bounds.x + pad,
            bounds.y + pad,
            leftWidth,
            halfHeight
        );
    }

    private void layoutSection(VerticalScrollBox<?> box, float y, float height) {
        float pad = bounds.width * cfg.padding;
        float leftWidth = bounds.width * cfg.leftRatio;

        box.bounds.set(
            bounds.x + pad * 2 + leftWidth,
            y,
            bounds.width - leftWidth - pad * 3,
            height
        );

        box.relayout();
    }

    // ------------------------------------------------------------------------

    @Override
    protected void drawContent(ShapeRenderer renderer, SpriteBatch batch) {
        drawSectionTitle(batch, "Ammo Selection", true);
        drawSectionTitle(batch, "Targeting Parameters", false);

        ammoInfoBox.draw(
            renderer,
            batch,
            ammoInfoBounds,
            buildAmmoLines(),
            selectedAmmoEntry != null
        );

        targetingInfoBox.draw(
            renderer,
            batch,
            targetingInfoBounds,
            buildTargetingLines(),
            selectedTargeting != null
        );

        ammoScrollBox.draw(renderer, batch, font, camera);
        targetingScrollBox.draw(renderer, batch, font, camera);
    }

    private List<String> buildAmmoLines() {
        if (selectedAmmoEntry == null) return null;

        BulletRepr bullet = selectedAmmoEntry.getBullet();
        return List.of(
            "Name: " + bullet.getName(),
            "Damage: " + bullet.getDamage(),
            "Speed: " + bullet.getSpeed()
        );
    }

    private List<String> buildTargetingLines() {
        if (selectedTargeting == null) return null;

        return switch (selectedTargeting.getStrategy()) {
            case CLOSEST -> List.of(
                "Targets nearest enemy",
                "Fast reaction time"
            );
            case FURTHEST_PROGRESSED -> List.of(
                "Targets furthest enemy",
                "Prevents leaks"
            );
            case LARGEST -> List.of(
                "Targets highest HP",
                "Best for burst damage"
            );
        };
    }

    // ------------------------------------------------------------------------

    private void drawSectionTitle(SpriteBatch batch, String title, boolean top) {
        float pad = bounds.width * cfg.padding;
        float y = top
            ? bounds.y + bounds.height - pad - 10
            : bounds.y + bounds.height * 0.5f - pad - 10;

        batch.begin();
        font.getData().setScale(cfg.titleScale);
        layout.setText(font, title);
        font.draw(batch, layout, bounds.x + pad, y);
        font.getData().setScale(1f);
        batch.end();
    }

    // ------------------------------------------------------------------------

    @Override
    protected boolean handleClickInside(float x, float y) {

        if (ammoScrollBox.contains(x, y)) {
            BulletScrollEntry clicked = ammoScrollBox.click(x, y);
            if (clicked != null) setSelectedAmmo(clicked);
            return true;
        }

        if (targetingScrollBox.contains(x, y)) {
            TargetingModeScrollEntry clicked = targetingScrollBox.click(x, y);
            if (clicked != null) setSelectedTargeting(clicked);
            return true;
        }

        if (selectedAmmoEntry != null &&
            ammoInfoBox.getSelectButtonBounds().contains(x, y)) {
            setActiveAmmo(selectedAmmoEntry);
            return true;
        }

        if (selectedTargeting != null &&
            targetingInfoBox.getSelectButtonBounds().contains(x, y)) {
            setActiveTargeting(selectedTargeting);
            return true;
        }

        return false;
    }

    // ------------------------------------------------------------------------

    private void setSelectedAmmo(BulletScrollEntry entry) {
        if (selectedAmmoEntry != null) selectedAmmoEntry.selected = false;
        selectedAmmoEntry = entry;
        selectedAmmoEntry.selected = true;
    }

    private void setActiveAmmo(BulletScrollEntry entry) {
        if (activeAmmoEntry != null) activeAmmoEntry.active = false;
        activeAmmoEntry = entry;
        activeAmmoEntry.active = true;
        tower.configureSelectedAmmo(entry.getBullet());
    }

    private void setSelectedTargeting(TargetingModeScrollEntry entry) {
        if (selectedTargeting != null) selectedTargeting.selected = false;
        selectedTargeting = entry;
        selectedTargeting.selected = true;
    }

    private void setActiveTargeting(TargetingModeScrollEntry entry) {
        if (activeTargeting != null) activeTargeting.active = false;
        activeTargeting = entry;
        activeTargeting.active = true;
        tower.setTargetingStrategy(entry.getStrategy());
    }

    // ------------------------------------------------------------------------

    private void populateAmmo() {
        List<BulletScrollEntry> entries = new ArrayList<>();
        for (BulletRepr bullet : tower.supportedAmmoRepr) {
            entries.add(new BulletScrollEntry(bullet));
        }
        ammoScrollBox.setEntries(entries);
    }

    private void populateTargeting() {
        List<TargetingModeScrollEntry> entries = new ArrayList<>();
        for (TargetingHelper.TargetingStrategy strategy :
            TargetingHelper.TargetingStrategy.values()) {
            entries.add(new TargetingModeScrollEntry(strategy));
        }
        targetingScrollBox.setEntries(entries);
    }

    private void restoreActiveState() {
        for (BulletScrollEntry entry : ammoScrollBox.entries) {
            if (entry.getBullet().getName()
                .equals(tower.selectedAmmoRepr.getName())) {
                setActiveAmmo(entry);
                break;
            }
        }

        for (TargetingModeScrollEntry entry : targetingScrollBox.entries) {
            if (entry.getStrategy() == tower.targetingStrategy) {
                setActiveTargeting(entry);
                break;
            }
        }
    }
}
