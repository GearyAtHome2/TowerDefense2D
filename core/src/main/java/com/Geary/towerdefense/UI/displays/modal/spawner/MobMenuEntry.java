package com.Geary.towerdefense.UI.displays.modal.spawner;

import com.Geary.towerdefense.UI.displays.modal.scrollbox.ScrollEntry;
import com.Geary.towerdefense.UI.render.icons.IconStore;
import com.Geary.towerdefense.entity.mob.Mob;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

public class MobMenuEntry implements ScrollEntry {

    public interface MobEntryListener {
        void onRecruitClicked(MobMenuEntry entry, int amount);

        void onGarrisonClicked(MobMenuEntry entry, int amount);
    }

    private boolean affordable = true;
    public final Mob templateMob;
    public final String name;
    public final Rectangle bounds = new Rectangle();
    public Runnable onClick;

    private final Rectangle recruitButton = new Rectangle();
    private final Rectangle garrisonButton = new Rectangle();

    private final MobEntryListener listener;

    private static final GlyphLayout layout = new GlyphLayout();

    public MobMenuEntry(Mob templateMob, float x, float y, float width, float height, MobEntryListener listener) {
        this.templateMob = templateMob;
        this.name = templateMob.name;
        this.listener = listener;
        bounds.set(x, y, width, height);
    }

    public void draw(ShapeRenderer renderer, SpriteBatch batch, BitmapFont font, Camera camera) {
        float x = bounds.x;
        float y = bounds.y;
        float w = bounds.width;
        float h = bounds.height;

        /* ================= Background ================= */

        renderer.begin(ShapeRenderer.ShapeType.Filled);
        renderer.setColor(computeBackgroundColor(templateMob.order));
        renderer.rect(x, y, w, h);
        renderer.end();

        /* ================= Layout ================= */

        float iconSize = h - 8f;
        float nameX = x + h + 6f;

        layout.setText(font, name);
        float nameW = layout.width;

        float statsX = nameX + nameW + 12f;
        float statsW = w * 0.22f;

        float costX = statsX + statsW + 6f;
        float costW = w * 0.16f;

        float buttonW = 90f;
        float buttonH = (h - 6f) / 2f;
        float buttonX = x + w - buttonW - 6f;

        float effectX = costX + costW + 6f;
        float effectW = buttonX - effectX - 6f;

        /* ================= Text + Icons ================= */

        batch.begin();

        var icon = IconStore.mob(templateMob.name);
        if (icon != null) {
            batch.draw(icon, x + 4f, y + 4f, iconSize, iconSize);
        }

        font.draw(batch, name, nameX, y + h * 0.7f);

        String statsText =
            "Health: " + templateMob.health + "\n" +
                "Damage: " + templateMob.damage +
                (templateMob.armour > 0 ? "\nArmour: " + templateMob.armour : "");

        drawScaledText(font, batch, statsText, statsX, y, statsW, h, 0.7f, 1f);

        // Dim cost text slightly
        Color old = font.getColor();
        font.setColor(old.r, old.g, old.b, 0.85f);
        float costAlpha = affordable ? 0.85f : 0.35f;
        font.setColor(1f, 1f, 1f, costAlpha);

        drawScaledText(
            font,
            batch,
            templateMob.getCostText(),
            costX,
            y,
            costW,
            h,
            0.6f,
            1f
        );

        font.setColor(old);

        drawScaledText(
            font,
            batch,
            templateMob.effectText,
            effectX,
            y,
            effectW,
            h,
            0.7f,
            1f
        );

        batch.end();

        /* ================= Buttons ================= */

        float buttonYTop = y + h - buttonH - 3f;
        float buttonYBottom = y + 3f;

        recruitButton.set(buttonX, buttonYTop, buttonW, buttonH);
        garrisonButton.set(buttonX, buttonYBottom, buttonW, buttonH);

        renderer.begin(ShapeRenderer.ShapeType.Filled);
        if (affordable) {
            renderer.setColor(0.1f, 0.7f, 0.1f, 1f);     // bright green
        } else {
            renderer.setColor(0.15f, 0.35f, 0.15f, 1f);  // dim green
        }
        renderer.rect(recruitButton.x, recruitButton.y, recruitButton.width, recruitButton.height);
        if (affordable) {
            renderer.setColor(0.5f, 0.5f, 0.5f, 1f);     // normal grey
        } else {
            renderer.setColor(0.25f, 0.25f, 0.25f, 1f);  // dim grey
        }
        renderer.rect(garrisonButton.x, garrisonButton.y, garrisonButton.width, garrisonButton.height);
        renderer.end();

        batch.begin();

        boolean shift = isShiftHeld();

        String recruitText = shift ? "Recruit x5" : "Recruit";
        String garrisonText = shift ? "Garrison x5" : "Garrison";
        drawScaledText(
            font,
            batch,
            recruitText,
            recruitButton.x + 2f,
            recruitButton.y,
            buttonW,
            buttonH,
            0.5f,
            1f
        );
        drawScaledText(
            font,
            batch,
            garrisonText,
            garrisonButton.x + 2f,
            garrisonButton.y,
            buttonW,
            buttonH,
            0.5f,
            0.7f
        );
        batch.end();

    }

    public boolean click(float x, float y) {
        if (!affordable) return false;

        int amount = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)
            || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)
            ? 5
            : 1;

        if (recruitButton.contains(x, y)) {
            if (listener != null) {
                listener.onRecruitClicked(this, amount);
            }
            return true;
        }

        if (garrisonButton.contains(x, y)) {
            if (listener != null) {
                listener.onGarrisonClicked(this, amount);
            }
            return true;
        }

        if (bounds.contains(x, y)) {
            if (onClick != null) onClick.run();
            return true;
        }

        return false;
    }


    private void drawScaledText(
        BitmapFont font,
        SpriteBatch batch,
        String text,
        float x,
        float y,
        float width,
        float height,
        float minScale,
        float alpha
    ) {
        font.getData().setScale(1f);

        layout.setText(font, text, font.getColor(), width, com.badlogic.gdx.utils.Align.left, true);
        if (layout.height == 0) return;

        float scale = Math.min(1f, Math.max(minScale, (height * 0.85f) / layout.height));
        font.getData().setScale(scale);

        layout.setText(font, text, font.getColor(), width, com.badlogic.gdx.utils.Align.left, true);

        Color c = font.getColor();
        font.setColor(c.r, c.g, c.b, alpha);

        float textY = y + (height + layout.height) * 0.5f;
        font.draw(batch, layout, x, textY);

        font.setColor(c);
        font.getData().setScale(1f);
    }

    private boolean isShiftHeld() {
        return Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)
            || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT);
    }

    public void setAffordable(boolean affordable) {
        this.affordable = affordable;
    }

    @Override
    public Rectangle bounds() {
        return bounds;
    }

    private Color computeBackgroundColor(Mob.Order order) {
        return switch (order) {
            case NEUTRAL -> new Color(0.45f, 0.45f, 0.45f, 1f);
            case TECH -> new Color(0.3f, 0.35f, 0.5f, 1f);
            case NATURE -> new Color(0.15f, 0.55f, 0.15f, 1f);
            case DARK -> new Color(0.05f, 0.05f, 0.05f, 1f);
            case LIGHT -> new Color(0.65f, 0.65f, 0.45f, 1f);
            case FIRE -> new Color(0.65f, 0.15f, 0.1f, 1f);
            case WATER -> new Color(0.15f, 0.3f, 0.65f, 1f);
        };
    }
}
