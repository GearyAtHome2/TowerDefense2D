package com.Geary.towerdefense.levelSelect;

import com.Geary.towerdefense.UI.render.icons.IconStore;
import com.Geary.towerdefense.entity.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LevelGridGenerator {

    public static final int GRID_WIDTH = 128;
    public static final int GRID_HEIGHT = 128;
    public static final float CELL_SIZE = 20f;

    private static final int ROW_COUNT = 4;
    private static final int ROW_HEIGHT = 32;

    private static final int PATH_MIN_DISTANCE = 10;
    private static final int PATH_MAX_DISTANCE = 16;
    private static final int PATH_EDGE_MARGIN = 14;

    // 🔶 Order Square Settings
    private static final int MIN_ORDER_SQUARE_SIZE = 2;
    private static final int MAX_ORDER_SQUARE_SIZE = 4;
    private static final int ORDER_SQUARES_PER_ROW = 3;

    private final LevelGenerator levelGenerator;
    private final List<LevelGridCell> levels = new ArrayList<>();
    private final List<OrderSquare> orderSquares = new ArrayList<>();

    private LevelGridCell[][] grid;
    private int levelIndex = 1;

    public LevelGridGenerator() {
        this.levelGenerator = new LevelGenerator();
    }

    public List<LevelGridCell> generateMap() {
        grid = new LevelGridCell[GRID_WIDTH][GRID_HEIGHT];

        for (int x = 0; x < GRID_WIDTH; x++)
            for (int y = 0; y < GRID_HEIGHT; y++)
                grid[x][y] = new LevelGridCell(x, y);

        RowGenerator rowGenerator = new RowGenerator(grid, GRID_WIDTH, GRID_HEIGHT, ROW_HEIGHT);

        for (int i = 0; i < ROW_COUNT; i++)
            rowGenerator.populateRow(i);

//        generateOrderSquares();
        generateSnakeLevelsPath();

        for (int x = 0; x < GRID_WIDTH; x++) {
            for (int y = 0; y < GRID_HEIGHT; y++) {
                LevelGridCell cell = grid[x][y];
                if (!cell.isLevel() && !cell.isPath() && cell.getDominantOrder() == Entity.Order.NATURE) {
                    TextureRegion icon = IconStore.randomMapTileForOrder(Entity.Order.NATURE, false);
                    cell.setCachedIcon(icon);
                }
                if (!cell.isLevel() && !cell.isPath() && cell.getDominantOrder() == Entity.Order.WATER) {
//                    TextureRegion icon = IconStore.randomMapTileForOrder(Entity.Order.WATER);
                    boolean edge = rowGenerator.isEdgeCell(x,y);
                    TextureRegion icon = IconStore.randomMapTileForOrder(Entity.Order.WATER, edge);
                    cell.setCachedIcon(icon);
                }
            }
        }

        return levels;
    }

//    private void generateOrderSquares() {
//        Random rng = new Random();
//
//        // For each order type (skip NEUTRAL if desired)
//        for (Entity.Order order : Entity.Order.values()) {
//            if (order == Entity.Order.NEUTRAL) continue;
//
//            // Collect all cells dominated by this order and not level/path
//            List<LevelGridCell> candidates = new ArrayList<>();
//            for (int x = 0; x < GRID_WIDTH; x++) {
//                for (int y = 0; y < GRID_HEIGHT; y++) {
//                    LevelGridCell cell = grid[x][y];
//                    if (!cell.isLevel() && !cell.isPath() && cell.getDominantOrder() == order) {
//                        candidates.add(cell);
//                    }
//                }
//            }
//
//            if (candidates.isEmpty()) continue;
//
//            // Place ORDER_SQUARES_PER_ROW squares per order cluster
//            for (int i = 0; i < ORDER_SQUARES_PER_ROW; i++) {
//                int size = MIN_ORDER_SQUARE_SIZE +
//                    rng.nextInt(MAX_ORDER_SQUARE_SIZE - MIN_ORDER_SQUARE_SIZE + 1);
//
//                // Random starting cell inside the cluster
//                LevelGridCell startCell = candidates.get(rng.nextInt(candidates.size()));
//                int startX = startCell.getX();
//                int startY = startCell.getY();
//
//                // Clamp so the square doesn't go outside grid
//                startX = Math.min(startX, GRID_WIDTH - size - 1);
//                startY = Math.min(startY, GRID_HEIGHT - size - 1);
//
//                // Check all cells in square are dominated by the same order
//                boolean valid = true;
//                outer: for (int x = startX; x < startX + size; x++) {
//                    for (int y = startY; y < startY + size; y++) {
//                        if (grid[x][y].getDominantOrder() != order) {
//                            valid = false;
//                            break outer;
//                        }
//                    }
//                }
//                if (!valid) continue;
//
//                // Apply square influence
//                for (int x = startX; x < startX + size; x++)
//                    for (int y = startY; y < startY + size; y++)
//                        grid[x][y].addInfluence(order, 1f);
//
//                orderSquares.add(new OrderSquare(startX, startY, size, order));
//            }
//        }
//    }
//
//    private boolean isValidOrderSquare(int startX, int startY, int size) {
//        return startX > 0 && startY > 0 && startX + size < GRID_WIDTH - 1 && startY + size < GRID_HEIGHT - 1;
//    }

    private void generateSnakeLevelsPath() {
        Random rng = new Random();

        int currentX = 10;
        int currentY = ROW_HEIGHT / 4;
        int dirX = 1;
        int rowIndex = 0;
        int verticalBuffer = 5;

        while (rowIndex < ROW_COUNT) {
            int rowMinY = rowIndex * ROW_HEIGHT;
            int rowMaxY = rowMinY + ROW_HEIGHT - 1;

            boolean branchPlaced = false;

            currentY = Math.max(rowMinY + verticalBuffer,
                Math.min(rowMaxY - verticalBuffer, currentY));

            grid[currentX][currentY] = setLevel(grid[currentX][currentY]);

            while (true) {
                int distance = PATH_MIN_DISTANCE + rng.nextInt(PATH_MAX_DISTANCE - PATH_MIN_DISTANCE + 1);

                for (int step = 0; step < distance; step++) {
                    int vertical = rng.nextInt(3) - 1;
                    int nextX = Math.max(0, Math.min(GRID_WIDTH - 1, currentX + dirX));
                    int nextY = Math.max(rowMinY + verticalBuffer,
                        Math.min(rowMaxY - verticalBuffer, currentY + vertical));

                    currentX = nextX;
                    currentY = nextY;

                    grid[currentX][currentY].setPath();
                }

                grid[currentX][currentY] = setLevel(grid[currentX][currentY]);

                boolean atEdge = (dirX == 1 && currentX >= GRID_WIDTH - PATH_EDGE_MARGIN - 5)
                    || (dirX == -1 && currentX <= PATH_EDGE_MARGIN + 5);

                if (atEdge) {
                    rowIndex++;
                    if (rowIndex >= ROW_COUNT)
                        break;

                    int targetY = rowIndex * ROW_HEIGHT + ROW_HEIGHT / 2;

                    while (currentY != targetY) {
                        currentY += (currentY < targetY) ? 1 : -1;
                        grid[currentX][currentY].setPath();
                    }

                    grid[currentX][currentY] = setLevel(grid[currentX][currentY]);
                    dirX = -dirX;
                    break;
                }
            }
        }
    }

    // ============================================================
    // DRAWING (FULLY INTEGRATED WITH NATURE ICONS)
    // ============================================================
    public void drawGrid(ShapeRenderer shapeRenderer, SpriteBatch batch) {
        // Filled cells
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int x = 0; x < GRID_WIDTH; x++) {
            for (int y = 0; y < GRID_HEIGHT; y++) {
                LevelGridCell cell = grid[x][y];
                float wx = x * CELL_SIZE;
                float wy = y * CELL_SIZE;

                Color color;

                if (cell.isLevel()) {
                    color = Color.WHITE;
                } else if (cell.isPath()) {
                    color = Color.DARK_GRAY;
                } else {
                    float total = 0f, r = 0f, g = 0f, b = 0f;

                    for (Entity.Order order : Entity.Order.values()) {
                        float val = cell.getInfluence(order);
                        if (val > 0f) {
                            Color c = getOrderColor(order);
                            r += c.r * val;
                            g += c.g * val;
                            b += c.b * val;
                            total += val;
                        }
                    }

                    if (total > 0f) {
                        r /= total;
                        g /= total;
                        b /= total;

                        float intensity = Math.min(total, 1f);
                        Color base = Color.LIGHT_GRAY;

                        color = new Color(
                            MathUtils.lerp(base.r, r, intensity),
                            MathUtils.lerp(base.g, g, intensity),
                            MathUtils.lerp(base.b, b, intensity),
                            1f
                        );
                    } else {
                        color = Color.LIGHT_GRAY;
                    }
                }

                shapeRenderer.setColor(color);
                shapeRenderer.rect(wx, wy, CELL_SIZE, CELL_SIZE);
            }
        }
        shapeRenderer.end();

        batch.begin();
        for (int x = 0; x < GRID_WIDTH; x++) {
            for (int y = 0; y < GRID_HEIGHT; y++) {
                LevelGridCell cell = grid[x][y];
                TextureRegion icon = cell.getCachedIcon();
                if (icon != null) {
                    float wx = x * CELL_SIZE;
                    float wy = y * CELL_SIZE;
                    batch.draw(icon, wx, wy, CELL_SIZE, CELL_SIZE);
                }
            }
        }
        batch.end();

        // Row lines
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.DARK_GRAY);
        for (int i = 1; i < ROW_COUNT; i++) {
            float y = i * ROW_HEIGHT * CELL_SIZE;
            shapeRenderer.line(0, y, GRID_WIDTH * CELL_SIZE, y);
        }
        shapeRenderer.end();

        // Order square outlines
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.YELLOW);
        for (OrderSquare square : orderSquares) {
            float wx = square.startX * CELL_SIZE;
            float wy = square.startY * CELL_SIZE;
            float size = square.size * CELL_SIZE;
            shapeRenderer.rect(wx, wy, size, size);
        }
        shapeRenderer.end();
    }

    // ============================================================
    // HELPERS
    // ============================================================
    public LevelGridCell[][] getGrid() { return grid; }

    public LevelGridCell setLevel(LevelGridCell cell) {
        LevelData level = levelGenerator.generateLevel(cell, levelIndex);
        cell.setLevel(level);
        levels.add(cell);
        levelIndex++;
        return cell;
    }

    private Color getOrderColor(Entity.Order order) {
        return switch (order) {
            case NEUTRAL -> Color.LIGHT_GRAY;
            case TECH -> Color.CYAN;
            case NATURE -> Color.GREEN;
            case DARK -> Color.PURPLE;
            case LIGHT -> Color.YELLOW;
            case FIRE -> Color.RED;
            case WATER -> Color.BLUE;
        };
    }

    private static class OrderSquare {
        int startX, startY, size;
        Entity.Order order; // store the order

        OrderSquare(int startX, int startY, int size, Entity.Order order) {
            this.startX = startX;
            this.startY = startY;
            this.size = size;
            this.order = order;
        }
    }
}
