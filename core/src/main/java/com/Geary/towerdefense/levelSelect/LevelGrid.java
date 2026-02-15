package com.Geary.towerdefense.levelSelect;

import com.Geary.towerdefense.entity.Entity.Order;

import java.util.Random;

public class LevelGrid {

    private final int width;
    private final int height;

    private final LevelGridCell[][] cells;

    private final Random random = new Random();

    public LevelGrid(int width, int height) {
        this.width = width;
        this.height = height;

        cells = new LevelGridCell[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                cells[x][y] = new LevelGridCell(x, y);
            }
        }

        generateOrderClusters();
    }

    public LevelGridCell get(int x, int y) {
        return cells[x][y];
    }

    public int getWidth() { return width; }
    public int getHeight() { return height; }

    // ---------------------------------------
    // CLUSTER GENERATION
    // ---------------------------------------

    private void generateOrderClusters() {

        Order[] orders = Order.values();

        int clusterCount = 25; // tweak later

        for (int i = 0; i < clusterCount; i++) {

            Order order =
                orders[random.nextInt(orders.length - 1) + 1];
            // skip NEUTRAL

            int centerX = random.nextInt(width);
            int centerY = random.nextInt(height);

            int radius = 10 + random.nextInt(40);

            applyCluster(centerX, centerY, radius, order);
        }
    }

    private void applyCluster(int cx, int cy, int radius, Order order) {

        int radiusSq = radius * radius;

        for (int x = cx - radius; x <= cx + radius; x++) {
            for (int y = cy - radius; y <= cy + radius; y++) {

                if (!inBounds(x, y)) continue;

                int dx = x - cx;
                int dy = y - cy;

                if (dx * dx + dy * dy <= radiusSq) {
                    cells[x][y].setOrder(order);
                }
            }
        }
    }

    private boolean inBounds(int x, int y) {
        return x >= 0 && y >= 0 && x < width && y < height;
    }
}
