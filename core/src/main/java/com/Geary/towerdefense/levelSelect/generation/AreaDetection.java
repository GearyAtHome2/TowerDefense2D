package com.Geary.towerdefense.levelSelect.generation;

import com.Geary.towerdefense.UI.render.icons.IconStore;
import com.Geary.towerdefense.entity.Entity;
import com.Geary.towerdefense.levelSelect.LevelGridCell;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AreaDetection {

    /**
     * Detect areas with rotation (default behavior)
     */
    public List<OrderAssetRenderer.Area> detectRotatedAreas(LevelGridCell[][] grid,
                                                            Entity.Order order,
                                                            float t4, float t3, float t2, float t1,
                                                            float skipChance,
                                                            Random rng) {
        return detectAreas(grid, order, t4, t3, t2, t1, skipChance, rng, true);
    }

    /**
     * Detect areas without rotation, using unique asset placeholders
     */
    public List<OrderAssetRenderer.Area> detectNonRotatedAreas(LevelGridCell[][] grid,
                                                               Entity.Order order,
                                                               float t4, float t3, float t2, float t1,
                                                               float skipChance,
                                                               Random rng) {
        return detectAreas(grid, order, t4, t3, t2, t1, skipChance, rng, false);
    }

    /**
     * Unified detection routine
     */
    private List<OrderAssetRenderer.Area> detectAreas(LevelGridCell[][] grid,
                                                      Entity.Order targetOrder,
                                                      float t4, float t3, float t2, float t1,
                                                      float skipChance,
                                                      Random rng,
                                                      boolean allowRotation) {

        List<OrderAssetRenderer.Area> output = new ArrayList<>();
        boolean[][] used = new boolean[LevelGridGenerator.GRID_WIDTH][LevelGridGenerator.GRID_HEIGHT];

        int[] sizes = {4, 3, 2, 1};
        float[] thresholds = {t4, t3, t2, t1};

        for (int i = 0; i < sizes.length; i++) {
            detectAreaOfSize(grid, targetOrder, sizes[i], thresholds[i], skipChance, rng, allowRotation, used, output);
        }

        return output;
    }

    /**
     * Detect all candidate NxN areas of a given size
     */
    private void detectAreaOfSize(LevelGridCell[][] grid,
                                  Entity.Order targetOrder,
                                  int size,
                                  float threshold,
                                  float skipChance,
                                  Random rng,
                                  boolean allowRotation,
                                  boolean[][] used,
                                  List<OrderAssetRenderer.Area> output) {

        int w = LevelGridGenerator.GRID_WIDTH;
        int h = LevelGridGenerator.GRID_HEIGHT;
        int centerX = w / 2;
        int centerY = h / 2;

        List<int[]> candidates = new ArrayList<>();
        for (int x = 0; x <= w - size; x++)
            for (int y = 0; y <= h - size; y++)
                candidates.add(new int[]{x, y});

        // prioritize center first
        candidates.sort((a, b) -> Double.compare(
            Math.pow(a[0] - centerX, 2) + Math.pow(a[1] - centerY, 2),
            Math.pow(b[0] - centerX, 2) + Math.pow(b[1] - centerY, 2)
        ));

        for (int[] pos : candidates) {
            int x = pos[0];
            int y = pos[1];

            if (used[x][y]) continue;

            boolean valid = true;

            outerLoop:
            for (int dx = 0; dx < size; dx++) {
                for (int dy = 0; dy < size; dy++) {
                    int gx = x + dx;
                    int gy = y + dy;

                    if (used[gx][gy]) {
                        valid = false;
                        break outerLoop;
                    }

                    LevelGridCell cell = grid[gx][gy];
                    if (cell.isLevel() || cell.isPath()) {
                        valid = false;
                        break outerLoop;
                    }

                    if (cell.getDominantOrder() != targetOrder) {
                        valid = false;
                        break outerLoop;
                    }

                    if (cell.getInfluence(targetOrder) < threshold) {
                        valid = false;
                        break outerLoop;
                    }
                }
            }

            // Apply skip chance consistently
            if (valid && rng != null && skipChance > 0f && rng.nextFloat() < skipChance) {
                valid = false;
            }

            if (!valid) continue;

            for (int dx = 0; dx < size; dx++)
                for (int dy = 0; dy < size; dy++)
                    used[x + dx][y + dy] = true;

            float rotation = allowRotation && rng != null ? 90f * rng.nextInt(4) : 0f;

            // Placeholder icon selection: non-rotated detection could later use unique NxN assets
            TextureRegion icon = null;
            if (targetOrder == Entity.Order.WATER){
                if (size == 1) {
                    icon = IconStore.levelSelectTileNxN(targetOrder, size);
                }
            }
            if (targetOrder == Entity.Order.NATURE){
                icon = IconStore.levelSelectTileNxN(targetOrder, size);
            }
//            TextureRegion icon = IconStore.levelSelectTileNxN(targetOrder, size);

            output.add(new OrderAssetRenderer.Area(x, y, size, targetOrder, rotation, icon));
        }
    }
}
