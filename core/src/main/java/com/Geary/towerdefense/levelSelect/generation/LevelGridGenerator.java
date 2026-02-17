package com.Geary.towerdefense.levelSelect.generation;

import com.Geary.towerdefense.entity.Entity;
import com.Geary.towerdefense.levelSelect.LevelData;
import com.Geary.towerdefense.levelSelect.LevelGridCell;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LevelGridGenerator {

    public static final int GRID_WIDTH = 128;
    public static final int GRID_HEIGHT = 128;
    public static final float CELL_SIZE = 20f;
    protected static final int ROW_COUNT = 4;
    protected static final int ROW_HEIGHT = 32;

    private final LevelGenerator levelGenerator;
    private final List<LevelGridCell> levels = new ArrayList<>();
    private LevelGridCell[][] grid;
    private int levelIndex = 1;
    private final Random rng = new Random();

    private LevelPathGenerator pathGenerator;
    private LevelClusterGenerator clusterGenerator;

    public static final int DEFAULT_LEVEL_REGION = 2;

    public LevelGridGenerator() {
        this.levelGenerator = new LevelGenerator();
        this.pathGenerator = new LevelPathGenerator(this);
        this.clusterGenerator = new LevelClusterGenerator(this);
    }

    public List<LevelGridCell> generateMap() {
        grid = new LevelGridCell[GRID_WIDTH][GRID_HEIGHT];
        for (int x = 0; x < GRID_WIDTH; x++)
            for (int y = 0; y < GRID_HEIGHT; y++)
                grid[x][y] = new LevelGridCell(x, y);

        pathGenerator.generateSnakeLevelsPath();
        clusterGenerator.generateLevelClusters();
        return levels;
    }

    public LevelGridCell setLevel(LevelGridCell anchorCell) {
        return setLevel(anchorCell, DEFAULT_LEVEL_REGION, DEFAULT_LEVEL_REGION);
    }

    public LevelGridCell setLevel(LevelGridCell anchorCell, int width, int height) {
        LevelData level = levelGenerator.generateLevel(anchorCell, levelIndex, randomOrder());

        // assign the anchor cell
        anchorCell.setLevel(level);
        levels.add(anchorCell);
        levelIndex++;
        return anchorCell;
    }

    public LevelGridCell setMergeLevel(LevelGridCell anchorCell, Entity.Order primary, Entity.Order secondary) {
        return setMergeLevel(anchorCell, primary, secondary, DEFAULT_LEVEL_REGION, DEFAULT_LEVEL_REGION);
    }

    public LevelGridCell setMergeLevel(LevelGridCell anchorCell, Entity.Order primary, Entity.Order secondary,
                                       int width, int height) {
        LevelData level = levelGenerator.generateMergeLevel(anchorCell, levelIndex, primary, secondary);

        anchorCell.setLevel(level);
        levels.add(anchorCell);
        levelIndex++;

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                int levx = anchorCell.getX() + dx;
                int levy = anchorCell.getY() + dy;
                if (levx == anchorCell.getX() && levy == anchorCell.getY()) continue;
                grid[levx][levy].setRegion(anchorCell, 3, 3);
            }
        }
        return anchorCell;
    }

    public LevelGridCell[][] getGrid() {
        return grid;
    }

    public void drawGrid(ShapeRenderer shapeRenderer) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int x = 0; x < GRID_WIDTH; x++) {
            for (int y = 0; y < GRID_HEIGHT; y++) {
                LevelGridCell cell = grid[x][y];
                Color color;
                if (cell.isLevel()) color = Color.WHITE;
                else if (cell.isPath()) color = Color.DARK_GRAY;
                else color = clusterGenerator.computeCellColor(cell);
                shapeRenderer.setColor(color);
                shapeRenderer.rect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.DARK_GRAY);
        for (int i = 1; i < ROW_COUNT; i++)
            shapeRenderer.line(0, i * ROW_HEIGHT * CELL_SIZE, GRID_WIDTH * CELL_SIZE, i * ROW_HEIGHT * CELL_SIZE);
        shapeRenderer.end();
    }

    public Entity.Order randomOrder() {
        Entity.Order[] values = Entity.Order.values();
        Entity.Order order;
        do order = values[rng.nextInt(values.length)];
        while (order == Entity.Order.NEUTRAL);
        return order;
    }

    public Color getOrderColor(Entity.Order order) {
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

    public static int clamp(int val, int min, int max) {
        return Math.max(min, Math.min(max, val));
    }

    public List<LevelGridCell> getLevelCells() {
        return levels;
    }
}
