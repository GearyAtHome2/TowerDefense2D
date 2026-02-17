package com.Geary.towerdefense.levelSelect.generation;

import com.Geary.towerdefense.entity.Entity;
import com.Geary.towerdefense.levelSelect.LevelGridCell;
import com.badlogic.gdx.graphics.Color;

import java.util.Random;

public class LevelClusterGenerator {

    private final LevelGridGenerator generator;
    private final Random rng = new Random();

    public LevelClusterGenerator(LevelGridGenerator generator) {
        this.generator = generator;
    }

    public void generateLevelClusters() {
        int radius = 8;
        for (LevelGridCell level : generator.getLevelCells()) {
            int cx = level.getX() + rng.nextInt(5) - 2;
            int cy = level.getY() + rng.nextInt(5) - 2;
            createCluster(cx, cy, radius, level.levelData.getPrimaryOrder());
        }
    }

    public void createCluster(int cx, int cy, int radius, Entity.Order order) {
        int outer = radius + 4;
        LevelGridCell[][] grid = generator.getGrid();
        for (int dx = -outer; dx <= outer; dx++) {
            for (int dy = -outer; dy <= outer; dy++) {
                int gx = cx + dx, gy = cy + dy;
                if (gx < 0 || gx >= LevelGridGenerator.GRID_WIDTH || gy < 0 || gy >= LevelGridGenerator.GRID_HEIGHT) continue;
                float dist = (float)Math.sqrt(dx*dx + dy*dy);
                if (dist <= outer) grid[gx][gy].addInfluence(order, 1f - dist/outer);
            }
        }
    }

    public Color computeCellColor(LevelGridCell cell) {
        float r=0, g=0, b=0, total=0;
        for (Entity.Order o : Entity.Order.values()) {
            if (o == Entity.Order.NEUTRAL) continue;
            float inf = cell.getInfluence(o);
            if (inf <= 0) continue;
            Color c = generator.getOrderColor(o);
            r += c.r * inf; g += c.g * inf; b += c.b * inf; total += inf;
        }
        float weight = Math.max(0, 1f - total);
        Color neutral = generator.getOrderColor(Entity.Order.NEUTRAL);
        r += neutral.r * weight; g += neutral.g * weight; b += neutral.b * weight;
        return new Color(r,g,b,1f);
    }
}
