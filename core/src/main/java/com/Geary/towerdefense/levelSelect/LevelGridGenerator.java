package com.Geary.towerdefense.levelSelect;

import com.Geary.towerdefense.entity.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

import java.util.Random;

public class LevelGridGenerator {

    public static final int GRID_WIDTH = 128;
    public static final int GRID_HEIGHT = 128;
    public static final float CELL_SIZE = 10f;

    public enum CellType { PATH, LEVEL, BACKGROUND }

    public static class LevelGridCell {
        public final int xIndex;
        public final int yIndex;
        public CellType type;
        public Entity.Order order;

        public LevelGridCell(int xIndex, int yIndex, CellType type, Entity.Order order) {
            this.xIndex = xIndex;
            this.yIndex = yIndex;
            this.type = type;
            this.order = order;
        }
    }

    private LevelGridCell[][] grid;

    public void generateGrid() {
        grid = new LevelGridCell[GRID_WIDTH][GRID_HEIGHT];
        Random rng = new Random();

        for (int x = 0; x < GRID_WIDTH; x++)
            for (int y = 0; y < GRID_HEIGHT; y++)
                grid[x][y] = new LevelGridCell(x, y, CellType.BACKGROUND, Entity.Order.NEUTRAL);

        for (Entity.Order order : Entity.Order.values()) {
            if (order == Entity.Order.NEUTRAL) continue;
            int clusterCount = 10 + rng.nextInt(6);
            for (int i = 0; i < clusterCount; i++) {
                int cx = rng.nextInt(GRID_WIDTH);
                int cy = rng.nextInt(GRID_HEIGHT);
                int radius = 5 + rng.nextInt(5);
                fillCluster(cx, cy, radius, order, rng);
            }
        }
    }

    private void fillCluster(int cx, int cy, int radius, Entity.Order order, Random rng) {
        for (int dx = -radius; dx <= radius; dx++) {
            for (int dy = -radius; dy <= radius; dy++) {
                int gx = cx + dx, gy = cy + dy;
                if (gx < 0 || gx >= GRID_WIDTH || gy < 0 || gy >= GRID_HEIGHT) continue;
                float dist = (float)Math.sqrt(dx*dx + dy*dy);
                if (dist < radius*0.5f || rng.nextFloat() < 1f - MathUtils.clamp(dist/radius,0f,1f)) {
                    grid[gx][gy].type = (dx == 0 && dy == 0) ? CellType.LEVEL : CellType.BACKGROUND;
                    grid[gx][gy].order = order;
                }
            }
        }
    }

    public void drawGrid(ShapeRenderer shapeRenderer) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (int x = 0; x < GRID_WIDTH; x++)
            for (int y = 0; y < GRID_HEIGHT; y++) {
                LevelGridCell cell = grid[x][y];
                float wx = x * CELL_SIZE, wy = y * CELL_SIZE;
                Color baseColor = switch (cell.type) {
                    case LEVEL -> Color.CYAN;
                    case PATH -> getOrderColor(cell.order).cpy().lerp(Color.WHITE,0.3f);
                    case BACKGROUND -> getOrderColor(cell.order).cpy().lerp(Color.BLACK,0.3f);
                };
                if (cell.order != Entity.Order.NEUTRAL && cell.type == CellType.BACKGROUND) {
                    float dist = findDistanceToClusterCenter(x,y,cell.order);
                    baseColor.lerp(getOrderColor(Entity.Order.NEUTRAL), MathUtils.clamp(dist/8f,0f,1f));
                }
                shapeRenderer.setColor(baseColor);
                shapeRenderer.rect(wx, wy, CELL_SIZE, CELL_SIZE);
            }
        shapeRenderer.end();
    }

    private float findDistanceToClusterCenter(int x, int y, Entity.Order order) {
        int searchRadius = 8;
        for(int r=1;r<=searchRadius;r++)
            for(int dx=-r;dx<=r;dx++)
                for(int dy=-r;dy<=r;dy++) {
                    int gx = x+dx, gy = y+dy;
                    if(gx<0||gx>=GRID_WIDTH||gy<0||gy>=GRID_HEIGHT) continue;
                    LevelGridCell n = grid[gx][gy];
                    if(n.type==CellType.LEVEL && n.order==order) return (float)Math.sqrt(dx*dx+dy*dy);
                }
        return searchRadius;
    }

    private Color getOrderColor(Entity.Order order) {
        return switch(order) {
            case NEUTRAL -> Color.GRAY;
            case TECH -> Color.CYAN;
            case NATURE -> Color.GREEN;
            case DARK -> Color.PURPLE;
            case LIGHT -> Color.YELLOW;
            case FIRE -> Color.RED;
            case WATER -> Color.BLUE;
        };
    }

    public LevelGridCell[][] getGrid() { return grid; }
}
