package com.Geary.towerdefense.UI.render;

import com.Geary.towerdefense.entity.buildings.production.Production;
import com.Geary.towerdefense.world.GameWorld;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class MineRenderer {

    private final GameWorld world;
    private final ShapeRenderer sr;

    public MineRenderer(GameWorld world, ShapeRenderer sr) {
        this.world = world;
        this.sr = sr;
    }

    public void drawMines() {
        for (Production production : world.productions) {
            drawMine(production, false); // real mine
        }

        if (world.ghostProduction != null) {
            drawMine(world.ghostProduction, true); // ghost mine
        }
    }

    private void drawMine(Production production, boolean ghost) {
        if (production.resource == null) return;

        // --- Colors ---
        Color frameColor = ghost ? new Color(0.2f, 0.4f, 0.2f, 0.4f) : Color.GREEN;
        Color fanColor   = ghost ? new Color(0.3f, 0f, 0f, 0.4f) : Color.RED;

        float centerX = production.xPos; // now xPos is center
        float centerY = production.yPos; // now yPos is center

        float squareSize = production.size; // use mine’s size
        float frameSize  = squareSize / 2f;

        // --- Draw frame ---
        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.setColor(frameColor);
        sr.rect(centerX - frameSize, centerY - frameSize, squareSize, squareSize);
        sr.rect(centerX - frameSize + 2, centerY - frameSize + 2, squareSize - 4, squareSize - 4);
        sr.end();

        // --- Draw fan ---
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(fanColor);

        float armLength = squareSize * 0.4f;
        float armWidth  = 4f;

        float angleDeg = production.animationState * 360f;

        for (int i = 0; i < 4; i++) {
            sr.rect(
                centerX - armWidth / 2f,
                centerY,
                armWidth / 2f,   // originX
                0f,              // originY
                armWidth,
                armLength,
                1f, 1f,         // scaleX, scaleY
                angleDeg + i * 90f
            );
        }
        sr.end();
    }
}
