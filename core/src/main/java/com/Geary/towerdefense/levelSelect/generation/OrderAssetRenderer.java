package com.Geary.towerdefense.levelSelect.generation;

import com.Geary.towerdefense.entity.Entity;
import com.Geary.towerdefense.levelSelect.LevelGridCell;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OrderAssetRenderer {

    private static final float THRESHOLD_4x4 = 0.7f;
    private static final float THRESHOLD_3x3 = 0.5f;
    private static final float THRESHOLD_2x2 = 0.2f;
    private static final float THRESHOLD_1x1 = 0.2f;
    private static final float SKIP_CHANCE = 0.15f;

    private static final float BUFFER_PIXELS = 0f;

    private final Random rng = new Random();
    private final List<Area> detectedAreas = new ArrayList<>();
    private final AreaDetection detectionHelper = new AreaDetection();

    public static class Area {
        public final int x, y, size;
        public final Entity.Order order;
        public final TextureRegion icon;
        public final float rotation;

        public Area(int x, int y, int size, Entity.Order order, float rotation, TextureRegion icon) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.order = order;
            this.rotation = rotation;
            this.icon = icon;
        }
    }

    public OrderAssetRenderer() { }

    /** Scan the grid: only NATURE and WATER for now */
    public void scanGrid(LevelGridCell[][] grid) {
        detectedAreas.clear();

        // NATURE uses rotation-aware detection
        detectedAreas.addAll(detectionHelper.detectRotatedAreas(grid, Entity.Order.NATURE,
            THRESHOLD_4x4, THRESHOLD_3x3, THRESHOLD_2x2, THRESHOLD_1x1, SKIP_CHANCE, rng));

        // WATER uses non-rotated detection with skip chance applied
        detectedAreas.addAll(detectionHelper.detectNonRotatedAreas(grid, Entity.Order.WATER,
            THRESHOLD_4x4, THRESHOLD_3x3, THRESHOLD_2x2, THRESHOLD_1x1, SKIP_CHANCE, rng));
    }

    /* ========================================================= */
    /* ====================== RENDERING ======================= */
    /* ========================================================= */

    public void renderAreas(ShapeRenderer shapeRenderer) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.YELLOW);
        shapeRenderer.end();
    }

    public void render(SpriteBatch batch) {
        batch.begin();
        float cellSize = LevelGridGenerator.CELL_SIZE;

        for (Area area : detectedAreas) {
            if (area.icon == null) continue;

            float worldX = area.x * cellSize + BUFFER_PIXELS;
            float worldY = area.y * cellSize + BUFFER_PIXELS;
            float worldSize = area.size * cellSize - (BUFFER_PIXELS * 2f);
            float origin = worldSize / 2f;

            batch.draw(area.icon, worldX, worldY, origin, origin, worldSize, worldSize, 1f, 1f, area.rotation);
        }

        batch.end();
    }

    public List<Area> getDetectedAreas() {
        return detectedAreas;
    }
}
