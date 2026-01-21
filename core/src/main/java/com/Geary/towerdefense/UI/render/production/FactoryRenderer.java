package com.Geary.towerdefense.UI.render.production;

import com.Geary.towerdefense.entity.buildings.factory.Manufacturing;
import com.Geary.towerdefense.world.GameWorld;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class FactoryRenderer {

    private final GameWorld world;

    public static final float CELL_MARGIN = 8f;
    public static final float CELL_SIZE = GameWorld.cellSize - 2 * CELL_MARGIN;

    private final ShapeRenderer sr;

    public FactoryRenderer(GameWorld world, ShapeRenderer sr) {
        this.world = world;
        this.sr = sr;
    }

    public void drawFactories() {
        // Draw real factories
        for (Manufacturing f : world.factories) {
            drawFactory(f, false);
        }

        // Draw ghost factory
        if (world.ghostManufacturing != null) {
            drawFactory(world.ghostManufacturing, true);
        }
    }

    private void drawFactory(Manufacturing manufacturing, boolean ghost) {
        float centerX = manufacturing.getCentreX();
        float centerY = manufacturing.getCentreY();

        float frameSize = manufacturing.size; // use building’s size
        float gap = 3f;

        FactoryAppearance appearance = manufacturing.getAppearance();

        Color frameColor = ghost
            ? appearance.frameGhost
            : (manufacturing.isConnectedToNetwork ? appearance.frameConnected : appearance.frameDisconnected);

        Color gearColor = ghost
            ? appearance.gearGhost
            : (manufacturing.isConnectedToNetwork ? appearance.gearConnected : appearance.gearDisconnected);

        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.setColor(frameColor);
        sr.rect(centerX - frameSize / 2f, centerY - frameSize / 2f, frameSize, frameSize);
        sr.rect(centerX - frameSize / 2f + gap, centerY - frameSize / 2f + gap,
            frameSize - 2 * gap, frameSize - 2 * gap);
        sr.end();

        // Draw gears (offsets from center)
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(gearColor);

        // Gear definitions: offset from center and radius
        float[][] gears = {
            {-frameSize * 0.15f, frameSize * 0.23f, frameSize * 0.18f, -1.333f, 6, 20},
            {0f, 0f, frameSize * 0.2f, 1f, 8, 0},
            {frameSize * 0.18f, -frameSize * 0.12f, frameSize * 0.09f, -1.6f, 5, 60}
        };

        for (float[] gear : gears) {
            float gx = centerX + gear[0];
            float gy = centerY + gear[1];
            float radius = gear[2];
            float speedMultiplier = gear[3];
            int numSpokes = (int) gear[4];

            float spokeLength = radius * 0.8f;
            float spokeWidth = 2f;

            float rotation = manufacturing.isConnectedToNetwork ? manufacturing.animationState * 360f * speedMultiplier : 0f;

            for (int i = 0; i < numSpokes; i++) {
                float angleDeg = rotation + i * (360f / numSpokes) + gear[5];
                sr.circle(gx, gy,spokeLength*0.8f);
                sr.rectLine(
                    gx, gy,
                    gx + spokeLength * (float)Math.cos(Math.toRadians(angleDeg)),
                    gy + spokeLength * (float)Math.sin(Math.toRadians(angleDeg)),
                    spokeWidth
                );
            }

            sr.circle(gx, gy, radius * 0.2f, 12);
        }
        sr.end();
    }

}
