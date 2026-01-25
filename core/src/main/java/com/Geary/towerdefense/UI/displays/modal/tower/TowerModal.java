package com.Geary.towerdefense.UI.displays.modal.tower;

import com.Geary.towerdefense.UI.displays.modal.Modal;
import com.Geary.towerdefense.UI.displays.modal.scrollbox.VerticalScrollBox;
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

public class TowerModal extends Modal {

    private final Tower tower;
    private final GameStateManager gameStateManager;

    private final VerticalScrollBox<BulletScrollEntry> ammoScrollBox;
    private final VerticalScrollBox<BulletScrollEntry> targetingScrollBox;
    private Rectangle selectButtonBounds = new Rectangle();

    private BulletScrollEntry selectedEntry = null;
    private BulletScrollEntry activeEntry = null;

    private final GlyphLayout layout = new GlyphLayout();

    /**
     * Layout ratios
     */
    private static class Layout {
        float padding = 0.04f;
        float splitRatio = 0.5f;
        float leftRatio = 0.45f;
        float titleScale = 1.4f;
    }

    private final Layout cfg = new Layout();

    public TowerModal(
        Tower tower,
        GameStateManager gameStateManager,
        BitmapFont font,
        OrthographicCamera camera
    ) {
        super(font, camera);
        this.tower = tower;
        this.gameStateManager = gameStateManager;

        ammoScrollBox = new VerticalScrollBox<BulletScrollEntry>(0, 0, 0, 0);
        targetingScrollBox = new VerticalScrollBox<BulletScrollEntry>(0, 0, 0, 0);

        populateAmmo();
        populateTargeting();

        this.activeEntry = ammoScrollBox.entries.stream().filter(entry -> entry.getBullet().getName().equals(tower.selectedAmmoRepr.getName())).findFirst().get();
        setActive(activeEntry);
        //what's the point of the activeentry in this class then?
    }


    @Override
    protected void layoutButtons() {
        float pad = bounds.width * cfg.padding;
        float sectionHeight = bounds.height * cfg.splitRatio - pad * 1.5f;

        float topY = bounds.y + bounds.height - pad - sectionHeight;
        float bottomY = bounds.y + pad;

        layoutSection(ammoScrollBox, topY, sectionHeight);
        layoutSection(targetingScrollBox, bottomY, sectionHeight);
    }

    private void layoutSection(VerticalScrollBox<?> box, float y, float height) {
        float pad = bounds.width * cfg.padding;
        float leftWidth = bounds.width * cfg.leftRatio;

        box.bounds.set(
            bounds.x + pad + leftWidth + pad,
            y,
            bounds.width - leftWidth - pad * 3,
            height
        );

        box.relayout();
    }

    // ------------------------------------------------------------------------
    // Drawing
    // ------------------------------------------------------------------------

    @Override
    protected void drawContent(ShapeRenderer shapeRenderer, SpriteBatch batch) {
        // Section titles (now at top of each half)
        drawSectionTitle(batch, "Ammo Selection", true);
        drawSectionTitle(batch, "Targeting Parameters", false);

        // Ammo info box (top-left quadrant)
        drawAmmoInfoBox(shapeRenderer, batch);

        // Scroll boxes (right side)
        ammoScrollBox.draw(shapeRenderer, batch, font, camera);
        targetingScrollBox.draw(shapeRenderer, batch, font, camera);
    }

    private void drawSectionTitle(SpriteBatch batch, String title, boolean top) {
        float pad = bounds.width * cfg.padding;
        float y = top
            ? bounds.y + bounds.height - pad - 10
            : bounds.y + bounds.height * 0.5f - pad - 10;

        batch.begin();

        float oldX = font.getScaleX();
        float oldY = font.getScaleY();
        font.getData().setScale(cfg.titleScale);

        layout.setText(font, title);
        font.draw(batch, layout, bounds.x + pad, y);

        font.getData().setScale(oldX, oldY);
        batch.end();
    }

    // ------------------------------------------------------------------------
    // Input
    // ------------------------------------------------------------------------

    @Override
    protected boolean handleClickInside(float x, float y) {
        if (ammoScrollBox.contains(x, y)) {
            BulletScrollEntry clicked = ammoScrollBox.click(x, y);
            if (clicked != null) {
                setSelected(clicked);
            }
            return true;
        }

        if (targetingScrollBox.contains(x, y)) {
            targetingScrollBox.click(x, y);
            return true;
        }

        // Select button click
        if (isSelectButtonClicked(x, y)) {
            if (selectedEntry != null) {
                setActive(selectedEntry);
            }
            return true;
        }

        return false;
    }

    private boolean isSelectButtonClicked(float x, float y) {
        return selectButtonBounds.contains(x, y);
    }

    @Override
    protected boolean handleScrollInside(float x, float y, float amountY) {
        if (ammoScrollBox.contains(x, y)) {
            ammoScrollBox.scroll(amountY * 10f);
            return true;
        }

        if (targetingScrollBox.contains(x, y)) {
            targetingScrollBox.scroll(amountY * 10f);
            return true;
        }

        return false;
    }

    private void setSelected(BulletScrollEntry entry) {
        if (selectedEntry != null) {
            selectedEntry.selected = false;
        }
        selectedEntry = entry;
        selectedEntry.selected = true;
    }

    private void setActive(BulletScrollEntry entry) {
        if (activeEntry != null) {
            activeEntry.active = false;
        }
        activeEntry = entry;
        activeEntry.active = true;

        tower.configureSelectedAmmo(entry.getBullet());
    }

    private void drawAmmoInfoBox(ShapeRenderer renderer, SpriteBatch batch) {
        float pad = bounds.width * cfg.padding;
        float leftWidth = bounds.width * cfg.leftRatio;

        float boxX = bounds.x + pad;
        float boxY = bounds.y + bounds.height * 0.5f;
        float boxW = leftWidth;
        float boxH = bounds.height * 0.5f - pad * 2;

        // Background
        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(0.14f, 0.14f, 0.14f, 1f);
        renderer.rect(boxX, boxY, boxW, boxH);
        renderer.end();

        if (selectedEntry == null) return;

        BulletRepr b = selectedEntry.getBullet();

        batch.begin();
        font.draw(batch, "Name: " + b.getName(), boxX + 10, boxY + boxH - 20);
        font.draw(batch, "Damage: " + b.getDamage(), boxX + 10, boxY + boxH - 45);
        font.draw(batch, "Speed: " + b.getSpeed(), boxX + 10, boxY + boxH - 70);
        batch.end();

        drawSelectButton(renderer, batch, boxX, boxY, boxW);
    }

    private void drawSelectButton(ShapeRenderer renderer, SpriteBatch batch, float boxX, float boxY, float boxW) {
        float btnW = boxW * 0.5f;
        float btnH = 32f;
        float btnX = boxX + (boxW - btnW) / 2f;
        float btnY = boxY + 10f;

        selectButtonBounds.set(btnX, btnY, btnW, btnH);

        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(0.2f, 0.4f, 0.2f, 1f);
        renderer.rect(btnX, btnY, btnW, btnH);
        renderer.end();

        batch.begin();
        layout.setText(font, "Select");
        font.draw(batch, layout,
            btnX + (btnW - layout.width) / 2f,
            btnY + (btnH + layout.height) / 2f - 4
        );
        batch.end();
    }

    private void populateAmmo() {
        List<BulletScrollEntry> entries = new ArrayList<>();
        for (BulletRepr bullet : this.tower.supportedAmmoRepr) {
            entries.add(new BulletScrollEntry(bullet));
        }
        ammoScrollBox.setEntries(entries);
    }

    private void populateTargeting() {
        List<BulletScrollEntry> entries = new ArrayList<>();
        targetingScrollBox.setEntries(entries);
    }
}
