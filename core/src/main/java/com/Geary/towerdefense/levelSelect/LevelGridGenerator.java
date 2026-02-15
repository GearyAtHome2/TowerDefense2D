package com.Geary.towerdefense.levelSelect;

import com.Geary.towerdefense.entity.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LevelGridGenerator {

    public static final int GRID_WIDTH = 128;
    public static final int GRID_HEIGHT = 128;
    public static final float CELL_SIZE = 10f;

    private static final int MIN_LEVEL_DISTANCE = 10;
    private static final int MAX_LEVEL_DISTANCE = 25;
    private static final int CLUSTER_RADIUS = 6;

    public enum CellType { BACKGROUND, PATH, LEVEL }

    public static class LevelGridCell {
        public final int xIndex;
        public final int yIndex;

        public CellType type;
        public Entity.Order order;
        public float clusterFalloff;

        public LevelGridCell(int x, int y) {
            this.xIndex = x;
            this.yIndex = y;
            this.type = CellType.BACKGROUND;
            this.order = Entity.Order.NEUTRAL;
            this.clusterFalloff = 1f;
        }
    }

    private LevelGridCell[][] grid;

    public void generateGrid(List<LevelData> levels) {

        grid = new LevelGridCell[GRID_WIDTH][GRID_HEIGHT];
        for (int x = 0; x < GRID_WIDTH; x++)
            for (int y = 0; y < GRID_HEIGHT; y++)
                grid[x][y] = new LevelGridCell(x, y);

        Random rng = new Random();

        List<int[]> levelPositions = new ArrayList<>();
        List<Entity.Order> levelOrders = new ArrayList<>();

        // 1️⃣ Place first level at bottom-left corner
        int currentX = 5; // offset from left edge
        int currentY = 5; // offset from bottom edge

        for (int i = 0; i < levels.size(); i++) {

            Entity.Order order = randomOrder(rng);

            levelPositions.add(new int[]{currentX, currentY});
            levelOrders.add(order);

            if (i == levels.size() - 1)
                break;

            int distance = MIN_LEVEL_DISTANCE +
                rng.nextInt(MAX_LEVEL_DISTANCE - MIN_LEVEL_DISTANCE + 1);

            int targetX = currentX;
            int targetY = currentY;

            // Choose an overall outward bias direction
            int biasX = rng.nextInt(3) - 1;
            int biasY = rng.nextInt(3) - 1;
            while (biasX == 0 && biasY == 0) {
                biasX = rng.nextInt(3) - 1;
                biasY = rng.nextInt(3) - 1;
            }

            int lastDirX = biasX;
            int lastDirY = biasY;

            for (int step = 0; step < distance; step++) {

                int moveX;
                int moveY;

                if (rng.nextFloat() < 0.7f) { // continue generally outward
                    moveX = biasX;
                    moveY = biasY;
                } else { // wander slightly
                    moveX = rng.nextInt(3) - 1;
                    moveY = rng.nextInt(3) - 1;
                    if (moveX == 0 && moveY == 0) {
                        moveX = lastDirX;
                        moveY = lastDirY;
                    }
                }

                // Prevent backtracking
                if (moveX == -lastDirX && moveY == -lastDirY) {
                    moveX = lastDirX;
                    moveY = lastDirY;
                }

                targetX = MathUtils.clamp(targetX + moveX, 2, GRID_WIDTH - 3);
                targetY = MathUtils.clamp(targetY + moveY, 2, GRID_HEIGHT - 3);

                if (grid[targetX][targetY].type != CellType.LEVEL) {
                    grid[targetX][targetY].type = CellType.PATH;
                }

                lastDirX = moveX;
                lastDirY = moveY;
            }

            currentX = targetX;
            currentY = targetY;
        }

        // 2️⃣ Place LEVEL tiles
        for (int i = 0; i < levelPositions.size(); i++) {
            int[] pos = levelPositions.get(i);
            placeLevel(pos[0], pos[1], levelOrders.get(i));
        }

        // 3️⃣ Build clusters after all paths exist
        for (int i = 0; i < levelPositions.size(); i++) {
            int[] pos = levelPositions.get(i);
            createCluster(pos[0], pos[1], CLUSTER_RADIUS, levelOrders.get(i));
        }
    }

    private void placeLevel(int x, int y, Entity.Order order) {
        LevelGridCell cell = grid[x][y];
        cell.type = CellType.LEVEL;
        cell.order = order;
        cell.clusterFalloff = 0f;
    }

    private void createCluster(int cx, int cy, int radius, Entity.Order order) {
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {

                int gx = cx + dx;
                int gy = cy + dy;

                if (gx < 0 || gx >= GRID_WIDTH || gy < 0 || gy >= GRID_HEIGHT)
                    continue;

                LevelGridCell cell = grid[gx][gy];

                if (cell.type == CellType.LEVEL)
                    continue;

                float dist = (float) Math.sqrt(dx * dx + dy * dy);

                if (dist <= radius) {
                    cell.order = order;
                    cell.clusterFalloff = MathUtils.clamp(dist / radius, 0f, 1f);
                }
            }
        }
    }

    public void drawGrid(ShapeRenderer shapeRenderer) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (int x = 0; x < GRID_WIDTH; x++) {
            for (int y = 0; y < GRID_HEIGHT; y++) {
                LevelGridCell cell = grid[x][y];

                float wx = x * CELL_SIZE;
                float wy = y * CELL_SIZE;

                Color baseColor;

                // Background with falloff
                if (cell.order == Entity.Order.NEUTRAL) {
                    baseColor = Color.GRAY.cpy();
                } else {
                    baseColor = getOrderColor(cell.order)
                        .cpy()
                        .lerp(Color.GRAY, cell.clusterFalloff);
                }

                // LEVEL overrides
                if (cell.type == CellType.LEVEL) {
                    baseColor = getOrderColor(cell.order);
                }

                // PATH = darkened version of whatever base was
                else if (cell.type == CellType.PATH) {
                    baseColor = baseColor.lerp(Color.DARK_GRAY, 0.6f);
                }

                shapeRenderer.setColor(baseColor);
                shapeRenderer.rect(wx, wy, CELL_SIZE, CELL_SIZE);
            }
        }

        shapeRenderer.end();
    }

    private Color getOrderColor(Entity.Order order) {
        return switch (order) {
            case NEUTRAL -> Color.GRAY;
            case TECH -> Color.CYAN;
            case NATURE -> Color.GREEN;
            case DARK -> Color.PURPLE;
            case LIGHT -> Color.YELLOW;
            case FIRE -> Color.RED;
            case WATER -> Color.BLUE;
        };
    }

    private Entity.Order randomOrder(Random rng) {
        Entity.Order[] values = Entity.Order.values();
        Entity.Order order;
        do {
            order = values[rng.nextInt(values.length)];
        } while (order == Entity.Order.NEUTRAL);
        return order;
    }

    public LevelGridCell[][] getGrid() {
        return grid;
    }
}
