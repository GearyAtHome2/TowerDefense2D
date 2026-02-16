package com.Geary.towerdefense.levelSelect;

import com.Geary.towerdefense.entity.Entity;

import java.util.Random;

import static com.Geary.towerdefense.levelSelect.LevelGridGenerator.GRID_WIDTH;

public class RowGenerator {

    private final LevelGridCell[][] grid;
    private final int gridWidth;
    private final int gridHeight;
    private final int rowHeight;
    private final Random rng = new Random();

    public RowGenerator(LevelGridCell[][] grid,
                        int gridWidth,
                        int gridHeight,
                        int rowHeight) {
        this.grid = grid;
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.rowHeight = rowHeight;
    }

    public void populateRow(int rowIndex) {
        int rowMinY = rowIndex * rowHeight;
        int rowMaxY = rowMinY + rowHeight - 1;

        // Base cluster count for bottom row
        int baseClusters = 12;
        int extraClusters = 5 * rowIndex; // gentle increase per row
        int clusterCount = baseClusters + extraClusters + rng.nextInt(3); // small random variance

        for (int i = 0; i < clusterCount; i++) {
            int cx = rng.nextInt(gridWidth);
            int cy = rowMinY + rng.nextInt(rowHeight);

            int radius;
            if (rng.nextFloat() < 0.25f) {
                radius = 4 + rng.nextInt(3); // medium clusters
            } else {
                radius = 2 + rng.nextInt(2); // small clusters
            }

            Entity.Order order = randomOrder();
            createCluster(cx, cy, radius, order, rowMinY, rowMaxY);
        }
    }

    private void createCluster(int cx, int cy, int radius, Entity.Order order, int rowMinY, int rowMaxY) {
        int outerRadius = radius + 6; // falloff influence

        for (int dx = -outerRadius; dx <= outerRadius; dx++) {
            for (int dy = -outerRadius; dy <= outerRadius; dy++) {
                int gx = cx + dx;
                int gy = cy + dy;

                if (gx < 0 || gx >= GRID_WIDTH) continue;
                if (gy < rowMinY || gy > rowMaxY) continue;

                float dist = (float) Math.sqrt(dx * dx + dy * dy);
                if (dist <= outerRadius) {
                    float influence = 1f - (dist / outerRadius);
                    influence = Math.max(0f, influence);

                    LevelGridCell cell = grid[gx][gy];
                    cell.addInfluence(order, influence);
                }
            }
        }
    }

    private Entity.Order randomOrder() {
        Entity.Order[] values = Entity.Order.values();
        Entity.Order order;
        do {
            order = values[rng.nextInt(values.length)];
        } while (order == Entity.Order.NEUTRAL);
        return order;
    }
}
