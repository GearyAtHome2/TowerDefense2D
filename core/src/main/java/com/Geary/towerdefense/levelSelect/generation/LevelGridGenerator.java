package com.Geary.towerdefense.levelSelect.generation;

import com.Geary.towerdefense.UI.render.icons.IconStore;
import com.Geary.towerdefense.entity.Entity;
import com.Geary.towerdefense.levelSelect.LevelData;
import com.Geary.towerdefense.levelSelect.LevelGridCell;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
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
    private final List<LevelGridCell> levelCells = new ArrayList<>();
    private LevelGridCell[][] grid;
    private int levelIndex = 1;
    private final Random rng = new Random();

    private LevelPathGenerator pathGenerator;
    private LevelClusterGenerator clusterGenerator;

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
        clusterGenerator.generateBackgroundStreaks(10);
        return levelCells;
    }

    /**
     * Main entry used by cluster generator.
     * Automatically decides whether this becomes a merged level.
     */
    public LevelGridCell setLevel(LevelGridCell anchorCell, int width, int height, boolean branch) {

        // Only allow merges outside of branches
        if (!branch && shouldCreateMergedCluster(anchorCell)) {

            Entity.Order primary = randomOrder();
            Entity.Order secondary = randomOrderExcluding(primary);

            return setMergeLevel(anchorCell, primary, secondary, width, height);
        }

        LevelData level = levelGenerator.generateLevel(anchorCell, levelIndex, randomOrder());

        anchorCell.setLevel(level);
        levelCells.add(anchorCell);
        levelIndex++;

        anchorCell.setCachedIcon(createLevelIcon(anchorCell, width, height));
        return anchorCell;
    }

    public LevelGridCell setMergeLevel(LevelGridCell anchorCell, Entity.Order primary, Entity.Order secondary,
                                       int width, int height) {

        LevelData level = levelGenerator.generateMergeLevel(anchorCell, levelIndex, primary, secondary);

        anchorCell.setLevel(level);
        anchorCell.setRegion(3, 3);
        levelCells.add(anchorCell);
        levelIndex++;

        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                int levx = anchorCell.getX() + dx;
                int levy = anchorCell.getY() + dy;
                if (levx == anchorCell.getX() && levy == anchorCell.getY()) continue;
                grid[levx][levy].setRegion(anchorCell, width, height);
            }
        }

        anchorCell.setCachedIcon(createLevelIcon(anchorCell, width, height));
        return anchorCell;
    }

    /**
     * Row-scaled merge probability.
     * Row 0 = 0%
     * Row 3 = 40%
     */
    private boolean shouldCreateMergedCluster(LevelGridCell cell) {

        int row = cell.getY() / ROW_HEIGHT;

        if (row <= 0) return false;

        row = Math.min(row, ROW_COUNT - 1);

        float maxChance = 0.4f;
        float chance = (row / (float) (ROW_COUNT - 1)) * maxChance;

        return rng.nextFloat() < chance;
    }

    private Entity.Order randomOrderExcluding(Entity.Order exclude) {
        Entity.Order order;
        do {
            order = randomOrder();
        } while (order == exclude);
        return order;
    }

    private TextureRegion createLevelIcon(LevelGridCell anchorCell, int width, int height) {
        TextureRegion tex = IconStore.level3x3ForOrder(anchorCell.getPrimaryOrder());
        return tex;
    }

    public LevelGridCell[][] getGrid() {
        return grid;
    }

    public void drawGrid(ShapeRenderer shapeRenderer, SpriteBatch batch) {

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int x = 0; x < GRID_WIDTH; x++) {
            for (int y = 0; y < GRID_HEIGHT; y++) {
                LevelGridCell cell = grid[x][y];
                Color color;

                if (cell.isPath() && !cell.isLevel()) {
                    color = Color.DARK_GRAY;
                } else {
                    color = clusterGenerator.computeCellColor(cell);
                }

                shapeRenderer.setColor(color);
                shapeRenderer.rect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.DARK_GRAY);
        for (int i = 1; i < ROW_COUNT; i++) {
            shapeRenderer.line(0, i * ROW_HEIGHT * CELL_SIZE,
                GRID_WIDTH * CELL_SIZE, i * ROW_HEIGHT * CELL_SIZE);
        }
        shapeRenderer.end();

        batch.begin();
        for (LevelGridCell cell : levelCells) {
            TextureRegion icon = cell.getCachedIcon();
            if (icon != null) {

                int anchorX = cell.getX() - 1;
                int anchorY = cell.getY() - 1;

                anchorX = clamp(anchorX, 0, GRID_WIDTH - 3);
                anchorY = clamp(anchorY, 0, GRID_HEIGHT - 3);

                float pixelX = anchorX * CELL_SIZE;
                float pixelY = anchorY * CELL_SIZE;

                batch.draw(icon, pixelX, pixelY, CELL_SIZE * 3, CELL_SIZE * 3);
            }
        }
        batch.end();
    }

    public Entity.Order randomOrder() {
        Entity.Order[] values = Entity.Order.values();
        Entity.Order order;
        do order = values[rng.nextInt(values.length)];
        while (order == Entity.Order.NEUTRAL);
        return order;
    }

    public Entity.Order randomOrderNonNeutral() {
        Entity.Order[] values = Entity.Order.values();
        Entity.Order order;
        do order = values[rng.nextInt(values.length)];
        while (order == Entity.Order.NEUTRAL);
        return order;
    }

    public Color getOrderColor(Entity.Order order) {
        return switch (order) {
            case NEUTRAL -> new Color(0.65f, 0.65f, 0.65f, 1f);
            case TECH -> new Color(0.75f, 0.9f, 1f, 1f);
            case NATURE -> new Color(0.1f, 0.35f, 0.1f, 1f);
            case DARK -> new Color(0.25f, 0.2f, 0.3f, 1f);
            case LIGHT -> new Color(0.95f, 0.95f, 0.9f, 1f);
            case FIRE -> new Color(0.85f, 0.2f, 0.1f, 1f);
            case WATER -> new Color(0.6f, 0.8f, 1f, 1f);
        };
    }

    public static int clamp(int val, int min, int max) {
        return Math.max(min, Math.min(max, val));
    }

    public List<LevelGridCell> getLevelCells() {
        return levelCells;
    }
}
