package com.Geary.towerdefense.levelSelect.generation;

import com.Geary.towerdefense.entity.Entity;
import com.Geary.towerdefense.levelSelect.LevelGridCell;

import java.util.Random;

public class LevelPathGenerator {

    private final LevelGridGenerator generator;
    private final Random rng;

    private static final int PATH_MIN_DISTANCE = 14;
    private static final int PATH_MAX_DISTANCE = 18;
    private static final int PATH_EDGE_MARGIN = 14;
    private static final double STRAIGHT_CHANCE = 0.4;

    private static final int TARGET_VERTICAL_DISTANCE = 7;

    public LevelPathGenerator(LevelGridGenerator generator) {
        this.generator = generator;
        this.rng = new Random();
    }

    public void generateSnakeLevelsPath() {
        LevelGridCell[][] grid = generator.getGrid();
        int currentX = 10;
        int currentY = LevelGridGenerator.ROW_HEIGHT / 4;
        int dirX = 1;
        int rowIndex = 0;
        int verticalBuffer = 5;

        while (rowIndex < LevelGridGenerator.ROW_COUNT) {
            int rowMinY = rowIndex * LevelGridGenerator.ROW_HEIGHT;
            int rowMaxY = rowMinY + LevelGridGenerator.ROW_HEIGHT - 1;

            currentY = generator.clamp(currentY, rowMinY + 1 + verticalBuffer, rowMaxY - 1 - verticalBuffer);
            placeLevelAt(grid, currentX, currentY, false);

            boolean branchPlaced = false;

            while (true) {
                int distance = PATH_MIN_DISTANCE + rng.nextInt(PATH_MAX_DISTANCE - PATH_MIN_DISTANCE + 1);

                for (int step = 0; step < distance; step++) {
                    currentX = generator.clamp(currentX + dirX, 1, LevelGridGenerator.GRID_WIDTH - 2);

                    if (rng.nextDouble() > STRAIGHT_CHANCE) {
                        int remainingVertical = TARGET_VERTICAL_DISTANCE - Math.abs(currentY - (rowMinY + LevelGridGenerator.ROW_HEIGHT / 2));
                        int verticalStep = 0;
                        if (remainingVertical > 0) {
                            verticalStep = rng.nextInt(3) - 1;
                            if (verticalStep > remainingVertical) verticalStep = remainingVertical;
                            if (verticalStep < -remainingVertical) verticalStep = -remainingVertical;
                        }
                        currentY = generator.clamp(currentY + verticalStep, rowMinY + 1, rowMaxY - 1);
                    }

                    grid[currentX][currentY].setPath();
                }

                placeLevelAt(grid, currentX, currentY, false);

                boolean atEdge = (dirX == 1 && currentX >= LevelGridGenerator.GRID_WIDTH - PATH_EDGE_MARGIN - 2)
                    || (dirX == -1 && currentX <= PATH_EDGE_MARGIN + 1);
                boolean safeToBranch = (dirX == 1 && currentX < LevelGridGenerator.GRID_WIDTH / 2)
                    || (dirX == -1 && currentX > LevelGridGenerator.GRID_WIDTH / 2);

                if (!branchPlaced && safeToBranch && rng.nextFloat() < 0.3f) {
                    branchPlaced = true;
                    int[] branchEnd = createBranches(rowIndex, currentX, currentY, dirX);
                    currentX = branchEnd[0];
                    currentY = branchEnd[1];
                }

                if (atEdge) {
                    rowIndex++;
                    if (rowIndex >= LevelGridGenerator.ROW_COUNT) break;

                    int targetY = rowIndex * LevelGridGenerator.ROW_HEIGHT + LevelGridGenerator.ROW_HEIGHT / 2;
                    while (currentY != targetY) {
                        grid[currentX][currentY].setPath();
                        currentY += (currentY < targetY) ? 1 : -1;
                        currentY = generator.clamp(currentY, 1, LevelGridGenerator.GRID_HEIGHT - 2);
                    }

                    placeLevelAt(grid, currentX, currentY, false);
                    dirX = -dirX;
                    break;
                }
            }
        }
    }

    private LevelGridCell placeLevelAt(LevelGridCell[][] grid, int centerX, int centerY, boolean branch) {
        int width = 3;
        int height = 3;

        int anchorX = generator.clamp(centerX, 1, LevelGridGenerator.GRID_WIDTH - 2);
        int anchorY = generator.clamp(centerY, 1, LevelGridGenerator.GRID_HEIGHT - 2);

        LevelGridCell anchor = grid[anchorX][anchorY];
        generator.setLevel(anchor, width, height, branch);

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                int x = anchorX + dx;
                int y = anchorY + dy;
                if (x == anchorX && y == anchorY) continue;
                grid[x][y].setRegion(anchor, width, height);
            }
        }

        return anchor;
    }

    private int[] createBranches(int rowIndex, int startX, int startY, int dirX) {
        LevelGridCell[][] grid = generator.getGrid();
        int rowMinY = rowIndex * LevelGridGenerator.ROW_HEIGHT;
        int rowMaxY = rowMinY + LevelGridGenerator.ROW_HEIGHT - 1;

        int branchCount = 2 + rng.nextInt(2); // 2 or 3 branches
        if (rowIndex == 0) branchCount = 2;
        if (rowIndex == 3) branchCount = 3;

        int branchDistance = PATH_MIN_DISTANCE + rng.nextInt(PATH_MAX_DISTANCE - PATH_MIN_DISTANCE + 1);
        int[] branchEndX = new int[branchCount];
        int[] branchEndY = new int[branchCount];
        Entity.Order[] mergingOrders = new Entity.Order[2];

        for (int b = 0; b < branchCount; b++) {
            int x = startX;
            int y = startY;

            // Compute target Y based on branch type
            int targetYOffset;
            if (branchCount == 2) targetYOffset = (b == 0) ? TARGET_VERTICAL_DISTANCE : -TARGET_VERTICAL_DISTANCE;
            else if (branchCount == 3)
                targetYOffset = (b == 0) ? TARGET_VERTICAL_DISTANCE : (b == 1) ? -TARGET_VERTICAL_DISTANCE : 0;
            else targetYOffset = 0;

            int targetY = generator.clamp(startY + targetYOffset, rowMinY + 1, rowMaxY - 1);

            for (int step = 0; step < branchDistance; step++) {
                x = generator.clamp(x + dirX, 1, LevelGridGenerator.GRID_WIDTH - 2);

                if (rng.nextDouble() > STRAIGHT_CHANCE) {
                    if (y < targetY) y++;
                    else if (y > targetY) y--;
                }

                grid[x][y].setPath();
            }

            int anchorX = generator.clamp(x, 1, LevelGridGenerator.GRID_WIDTH - 2);
            int anchorY = generator.clamp(y, 1, LevelGridGenerator.GRID_HEIGHT - 2);

            if (branchCount == 3 && b == 2) {
                // centre branch: merge the two outer branches
                generator.setMergeLevel(grid[anchorX][anchorY], mergingOrders[0], mergingOrders[1], 3, 3);
                grid[anchorX][anchorY].isCentreBranch = true;
            } else {
                // upper/lower branch: normal level, no merge
                LevelGridCell anchor = generator.setLevel(grid[anchorX][anchorY], 3, 3, true);
                anchor.setRegion(3, 3);
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        int levx = anchorX + dx;
                        int levy = anchorY + dy;
                        if (levx == anchorX && levy == anchorY) continue;
                        grid[levx][levy].setRegion(anchor, 3, 3);
                    }
                }

                Entity.Order order = anchor.getPrimaryOrder();
                if (branchCount == 3) {
                    if (b == 0) mergingOrders[0] = order;
                    else if (b == 1) {
                        while (order == mergingOrders[0]) {
                            order = generator.randomOrderNonNeutral();
                            anchor.setLevel(generator.setLevel(grid[anchorX][anchorY], 3, 3, true).levelData);
                        }
                        mergingOrders[1] = order;
                    }
                } else if (branchCount == 2) {
                    mergingOrders[b] = order;
                }
            }

            branchEndX[b] = anchorX;
            branchEndY[b] = anchorY;
        }

        // Reconnect branches
        int reconnectDistance = PATH_MIN_DISTANCE + rng.nextInt(PATH_MAX_DISTANCE - PATH_MIN_DISTANCE + 1);
        int reconnectX = generator.clamp(branchEndX[0] + dirX * reconnectDistance, 1, LevelGridGenerator.GRID_WIDTH - 2);
        int reconnectY = generator.clamp(rowMinY + LevelGridGenerator.ROW_HEIGHT / 2 + rng.nextInt(7) - 3, 1, LevelGridGenerator.GRID_HEIGHT - 2);
        placeLevelAt(grid, reconnectX, reconnectY, false);

        for (int b = 0; b < branchCount; b++) {
            int x = branchEndX[b];
            int y = branchEndY[b];
            while (x != reconnectX || y != reconnectY) {
                if (x != reconnectX) x += (x < reconnectX) ? 1 : -1;
                if (y != reconnectY) y += (y < reconnectY) ? 1 : -1;
                grid[x][y].setPath();
            }
        }

        return new int[]{reconnectX, reconnectY};
    }
}
