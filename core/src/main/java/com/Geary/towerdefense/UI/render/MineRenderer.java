package com.Geary.towerdefense.UI.render;

import com.Geary.towerdefense.entity.buildings.Mine;
import com.Geary.towerdefense.world.GameWorld;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class MineRenderer {

    private final GameWorld world;

    public static final float CELL_MARGIN = 8f;
    public static final float CELL_SIZE = GameWorld.cellSize - 2 * CELL_MARGIN;

    private final ShapeRenderer sr;

    public MineRenderer(GameWorld world, ShapeRenderer sr) {
        this.world = world;
        this.sr = sr;
    }

    public void drawMines() {
        for (Mine t : world.mines) {
            drawMine(t, false); // real tower
        }

        if (world.ghostMine != null) {
            drawMine(world.ghostMine, true); // ghost tower
        }
    }

    private void drawMine(Mine mine, boolean ghost) {
        if (mine.resource == null) return;

        // --- Colors ---
        Color frameColor = ghost ? new Color(0.2f, 0.4f, 0.2f, 0.4f) : Color.GREEN;
        Color fanColor   = ghost ? new Color(0.3f, 0f, 0f, 0.4f) : Color.RED;

        float centerX = mine.xPos + CELL_MARGIN + CELL_SIZE / 2f;
        float centerY = mine.yPos + CELL_MARGIN + CELL_SIZE / 2f;

        float squareSize = CELL_SIZE * 0.8f;
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

        float angleDeg = mine.animationState * 360f;

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

