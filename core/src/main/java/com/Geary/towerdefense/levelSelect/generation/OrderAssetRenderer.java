package com.Geary.towerdefense.levelSelect.generation;

import com.Geary.towerdefense.entity.Entity;
import com.Geary.towerdefense.levelSelect.LevelGridCell;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class OrderAssetRenderer {

    // --- Influence thresholds ---
    private static final float THRESHOLD_4x4 = 0.7f;
    private static final float THRESHOLD_3x3 = 0.5f;
    private static final float THRESHOLD_2x2 = 0.2f;
    private static final float SKIP_CHANCE = 0.15f; // 15% chance to skip a square

    private Random rng = new Random();
    // --- List of detected areas ---
    public static class Area {
        public final int x, y, size;
        public Area(int x, int y, int size) {
            this.x = x;
            this.y = y;
            this.size = size;
        }
    }
    private final List<Area> detectedAreas = new ArrayList<>();

    // --- Render buffer (pixels) ---
    private static final float BUFFER_PIXELS = 2f;

    public OrderAssetRenderer() {
    }

    public void scanGrid(LevelGridCell[][] grid) {
        detectedAreas.clear();
        int w = LevelGridGenerator.GRID_WIDTH;
        int h = LevelGridGenerator.GRID_HEIGHT;
        boolean[][] assigned = new boolean[w][h];

        // Scan decreasing sizes
        detectAreas(grid, w, h, 4, THRESHOLD_4x4, assigned);
        detectAreas(grid, w, h, 3, THRESHOLD_3x3, assigned);
        detectAreas(grid, w, h, 2, THRESHOLD_2x2, assigned);
    }

    private void detectAreas(LevelGridCell[][] grid, int gridWidth, int gridHeight, int size, float threshold, boolean[][] used) {
        // Generate all candidate bottom-left positions
        List<int[]> candidates = new ArrayList<>();
        int centerX = gridWidth / 2;
        int centerY = gridHeight / 2;

        for (int x = 0; x <= gridWidth - size; x++) {
            for (int y = 0; y <= gridHeight - size; y++) {
                candidates.add(new int[]{x, y});
            }
        }

        // Sort candidates by distance to center (nearest first)
        candidates.sort((a, b) -> {
            double d1 = Math.pow(a[0] - centerX, 2) + Math.pow(a[1] - centerY, 2);
            double d2 = Math.pow(b[0] - centerX, 2) + Math.pow(b[1] - centerY, 2);
            return Double.compare(d1, d2);
        });

        for (int[] pos : candidates) {
            int x = pos[0], y = pos[1];

            if (used[x][y]) continue;

            boolean valid = true;

            outerLoop:
            for (int dx = 0; dx < size; dx++) {
                for (int dy = 0; dy < size; dy++) {
                    int gx = x + dx, gy = y + dy;
                    if (used[gx][gy]) { valid = false; break outerLoop; }

                    LevelGridCell cell = grid[gx][gy];
                    if (cell.isLevel() || cell.isPath()) { valid = false; break outerLoop; }

                    float maxInf = 0;
                    for (Entity.Order order : Entity.Order.values()) {
                        if (order == Entity.Order.NEUTRAL) continue;
                        maxInf = Math.max(maxInf, cell.getInfluence(order));
                    }
                    if (maxInf < threshold) { valid = false; break outerLoop; }
                }
            }

            // Random skip
            if (valid && rng.nextFloat() < SKIP_CHANCE) {
                valid = false;
            }

            if (valid) {
                for (int dx = 0; dx < size; dx++)
                    for (int dy = 0; dy < size; dy++)
                        used[x + dx][y + dy] = true;

                detectedAreas.add(new Area(x, y, size));
            }
        }
    }

    public void renderAreas(ShapeRenderer shapeRenderer) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.YELLOW);
        for (Area area : detectedAreas) {
            float cellSize = LevelGridGenerator.CELL_SIZE;
            float x = area.x * cellSize + BUFFER_PIXELS;
            float y = area.y * cellSize + BUFFER_PIXELS;
            float sizePx = area.size * cellSize - 2 * BUFFER_PIXELS;
            shapeRenderer.rect(x, y, sizePx, sizePx);
        }

        shapeRenderer.end();
    }

    public List<Area> getDetectedAreas() {
        return detectedAreas;
    }
}
