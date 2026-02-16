package com.Geary.towerdefense.levelSelect;

import com.Geary.towerdefense.entity.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LevelGridGenerator {

    public static final int GRID_WIDTH = 128;
    public static final int GRID_HEIGHT = 128;
    public static final float CELL_SIZE = 10f;

    private static final int ROW_COUNT = 4;
    private static final int ROW_HEIGHT = 32;

    private static final int PATH_MIN_DISTANCE = 10;
    private static final int PATH_MAX_DISTANCE = 16;
    private static final int PATH_EDGE_MARGIN = 14; // tiles from edge before turn
    private final LevelGenerator levelGenerator;
    private final List<LevelGridCell> levels = new ArrayList<>();

    public LevelGridGenerator() {
        this.levelGenerator = new LevelGenerator();
    }

    int levelIndex = 1;
    private LevelGridCell[][] grid;

    public List<LevelGridCell> generateMap() {
        grid = new LevelGridCell[GRID_WIDTH][GRID_HEIGHT];
        for (int x = 0; x < GRID_WIDTH; x++)
            for (int y = 0; y < GRID_HEIGHT; y++)
                grid[x][y] = new LevelGridCell(x, y);

        RowGenerator rowGenerator = new RowGenerator(grid, GRID_WIDTH, GRID_HEIGHT, ROW_HEIGHT);
        for (int i = 0; i < ROW_COUNT; i++)
            rowGenerator.populateRow(i);

        generateSnakeLevelsPath();
        return levels;
    }

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

            currentY = Math.max(rowMinY + verticalBuffer, Math.min(rowMaxY - verticalBuffer, currentY));
            grid[currentX][currentY] = setLevel(grid[currentX][currentY]);

            while (true) {
                int distance = PATH_MIN_DISTANCE + rng.nextInt(PATH_MAX_DISTANCE - PATH_MIN_DISTANCE + 1);

                for (int step = 0; step < distance; step++) {
                    int vertical = rng.nextInt(3) - 1;
                    int nextX = Math.max(0, Math.min(GRID_WIDTH - 1, currentX + dirX));
                    int nextY = Math.max(rowMinY + verticalBuffer, Math.min(rowMaxY - verticalBuffer, currentY + vertical));

                    int attempts = 0;
                    while (attempts < 3) {
                        boolean adjusted = false;
                        if (nextY > rowMinY && grid[nextX][nextY - 1].isLevel()) { nextY++; adjusted = true; }
                        if (nextY < rowMaxY && grid[nextX][nextY + 1].isLevel()) { nextY--; adjusted = true; }
                        if (!adjusted) break;
                        attempts++;
                    }

                    currentX = nextX;
                    currentY = nextY;
                    grid[currentX][currentY].setPath();
                }

                grid[currentX][currentY] = setLevel(grid[currentX][currentY]);

                boolean atEdge = (dirX == 1 && currentX >= GRID_WIDTH - PATH_EDGE_MARGIN - 5)
                    || (dirX == -1 && currentX <= PATH_EDGE_MARGIN + 5);
                boolean safeToBranch = (dirX == 1 && currentX < GRID_WIDTH / 2) || (dirX == -1 && currentX > GRID_WIDTH / 2);

                if (!branchPlaced && safeToBranch && rng.nextFloat() < 0.3f) {
                    branchPlaced = true;
                    int[] branchEnd = createBranches(rowIndex, currentX, currentY, dirX, rng);
                    currentX = branchEnd[0];
                    currentY = branchEnd[1];
                }

                if (atEdge) {
                    rowIndex++;
                    if (rowIndex >= ROW_COUNT) break;

                    int targetY = rowIndex * ROW_HEIGHT + ROW_HEIGHT / 2;
                    int loopCounter = 0;
                    while (currentY != targetY && loopCounter < 100) {
                        currentY += (currentY < targetY) ? 1 : -1;

                        int attempts = 0;
                        while (attempts < 3) {
                            boolean adjusted = false;
                            if (currentY > 0 && grid[currentX][currentY - 1].isLevel()) { currentY++; adjusted = true; }
                            if (currentY < GRID_HEIGHT - 1 && grid[currentX][currentY + 1].isLevel()) { currentY--; adjusted = true; }
                            if (!adjusted) break;
                            attempts++;
                        }

                        grid[currentX][currentY].setPath();
                        loopCounter++;
                    }

                    grid[currentX][currentY] = setLevel(grid[currentX][currentY]);
                    dirX = -dirX;
                    break;
                }
            }
        }
    }

    private int[] createBranches(int rowIndex, int startX, int startY, int dirX, Random rng) {
        int rowMinY = rowIndex * ROW_HEIGHT;
        int rowMaxY = rowMinY + ROW_HEIGHT - 1;

        int branchCount = 2 + rng.nextInt(2);
        int levelCount = 1 + rng.nextInt(2);
        int branchLength = PATH_MIN_DISTANCE + rng.nextInt(PATH_MAX_DISTANCE - PATH_MIN_DISTANCE + 1);

        int[] biases = new int[branchCount];
        if (branchCount == 2) { biases[0] = 1; biases[1] = -1; }
        else { biases[0] = 1; biases[1] = 0; biases[2] = -1; }

        int[] branchEndX = new int[branchCount];
        int[] branchEndY = new int[branchCount];

        for (int b = 0; b < branchCount; b++) {
            int x = startX;
            int y = startY;
            int verticalBias = biases[b];

            if (dirX < 0) verticalBias = -verticalBias;
            int targetY = startY + verticalBias * 5;

            for (int step = 0; step < branchLength; step++) {
                x = Math.max(0, Math.min(GRID_WIDTH - 1, x + dirX));
                int vertical = targetY - y;
                if (vertical != 0) vertical = (vertical > 0) ? 1 : -1;
                y += vertical;
                y = Math.max(rowMinY, Math.min(rowMaxY, y));

                int attempts = 0;
                while (attempts < 3) {
                    boolean adjusted = false;
                    if (y > rowMinY && grid[x][y - 1].isLevel()) { y++; adjusted = true; }
                    if (y < rowMaxY && grid[x][y + 1].isLevel()) { y--; adjusted = true; }
                    if (!adjusted) break;
                    attempts++;
                }

                grid[x][y].setPath();
            }

            grid[x][y] = setLevel(grid[x][y]);
            branchEndX[b] = x;
            branchEndY[b] = y;
        }

        if (levelCount > 1) {
            for (int b = 0; b < branchCount; b++) {
                int x = branchEndX[b];
                int y = branchEndY[b];
                for (int l = 1; l < levelCount; l++) {
                    int segmentLength = PATH_MIN_DISTANCE + rng.nextInt(PATH_MAX_DISTANCE - PATH_MIN_DISTANCE + 1);
                    for (int step = 0; step < segmentLength; step++) {
                        x = Math.max(0, Math.min(GRID_WIDTH - 1, x + dirX));
                        int vertical = rng.nextInt(3) - 1;
                        y += vertical;
                        y = Math.max(rowMinY, Math.min(rowMaxY, y));

                        int attempts = 0;
                        while (attempts < 3) {
                            boolean adjusted = false;
                            if (y > rowMinY && grid[x][y - 1].isLevel()) { y++; adjusted = true; }
                            if (y < rowMaxY && grid[x][y + 1].isLevel()) { y--; adjusted = true; }
                            if (!adjusted) break;
                            attempts++;
                        }

                        grid[x][y].setPath();
                    }
                    grid[x][y] = setLevel(grid[x][y]);
                }
                branchEndX[b] = x;
                branchEndY[b] = y;
            }
        }

        int reconnectDistance = PATH_MIN_DISTANCE + rng.nextInt(PATH_MAX_DISTANCE - PATH_MIN_DISTANCE + 1);
        int reconnectX = Math.max(0, Math.min(GRID_WIDTH - 1, branchEndX[0] + dirX * reconnectDistance));
        int reconnectY = rowMinY + ROW_HEIGHT / 2 + rng.nextInt(7) - 3;

        grid[reconnectX][reconnectY] = setLevel(grid[reconnectX][reconnectY]);

        for (int b = 0; b < branchCount; b++) {
            int x = branchEndX[b];
            int y = branchEndY[b];
            int loopCounter = 0;
            while ((x != reconnectX || y != reconnectY) && loopCounter < 200) {
                if (x != reconnectX) x += (x < reconnectX) ? 1 : -1;
                if (y != reconnectY) y += (y < reconnectY) ? 1 : -1;

                int attempts = 0;
                while (attempts < 3) {
                    boolean adjusted = false;
                    if (y > rowMinY && grid[x][y - 1].isLevel()) { y++; adjusted = true; }
                    if (y < rowMaxY && grid[x][y + 1].isLevel()) { y--; adjusted = true; }
                    if (!adjusted) break;
                    attempts++;
                }

                grid[x][y].setPath();
                loopCounter++;
            }
        }

        return new int[]{reconnectX, reconnectY};
    }

    public void drawGrid(ShapeRenderer shapeRenderer) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int x = 0; x < GRID_WIDTH; x++) {
            for (int y = 0; y < GRID_HEIGHT; y++) {
                LevelGridCell cell = grid[x][y];
                float wx = x * CELL_SIZE;
                float wy = y * CELL_SIZE;

                Color color;
                if (cell.isLevel()) color = Color.WHITE;
                else if (cell.isPath()) color = Color.DARK_GRAY;
                else {
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
                    if (total > 0f) { r /= total; g /= total; b /= total; color = new Color(r, g, b, 1f); }
                    else color = Color.LIGHT_GRAY;
                }

                shapeRenderer.setColor(color);
                shapeRenderer.rect(wx, wy, CELL_SIZE, CELL_SIZE);
            }
        }
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.DARK_GRAY);
        for (int i = 1; i < ROW_COUNT; i++) {
            float y = i * ROW_HEIGHT * CELL_SIZE;
            shapeRenderer.line(0, y, GRID_WIDTH * CELL_SIZE, y);
        }
        shapeRenderer.end();
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

    public LevelGridCell[][] getGrid() { return grid; }

    public LevelGridCell setLevel(LevelGridCell cell) {
        LevelData level = levelGenerator.generateLevel(cell, levelIndex);
        cell.setLevel(level);
        levels.add(cell);
        return cell;
    }
}
