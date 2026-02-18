package com.Geary.towerdefense.levelSelect.generation;

import com.Geary.towerdefense.entity.Entity;
import com.Geary.towerdefense.levelSelect.LevelGridCell;
import com.badlogic.gdx.graphics.Color;

import java.util.Random;

public class LevelClusterGenerator {

    private final LevelGridGenerator generator;
    private final MergedClusterGenerator mergedClusterGenerator;
    private final Random rng = new Random();

    // Size control
    public static final float GLOBAL_SIZE_MULTIPLIER = 0.6f;
    public static final int GLOBAL_SIZE_PADDING = 3;

    // Falloff control
    public static final float GLOBAL_FALLOFF_EXPONENT = 0.6f;
    public static final float GLOBAL_EDGE_HARDNESS = 1.5f;

    // Pattern intensity
    public static final float GLOBAL_TWIST_STRENGTH = 1.0f;
    public static final float GLOBAL_NOISE_STRENGTH = 1.0f;

    // Influence strength
    public static final float GLOBAL_INFLUENCE_MULTIPLIER = 1.0f;

    // Cluster variability
    private static final float CLUSTER_RADIUS_MIN_FACTOR = 0.8f;  // 20% smaller
    private static final float CLUSTER_RADIUS_MAX_FACTOR = 1.05f; // 5% larger
    private static final float CLUSTER_OVOID_FACTOR = 0.6f;       // ±40% X/Y scale
    private static final int CLUSTER_OFFSET_MAX = 3;              // ±3 tiles offset

    public LevelClusterGenerator(LevelGridGenerator generator) {
        this.generator = generator;
        this.mergedClusterGenerator = new MergedClusterGenerator(generator);
    }

    public void generateLevelClusters() {
        int baseRadius = 10;

        for (LevelGridCell level : generator.getLevelCells()) {
            if (level.isCentreBranch) continue;

            // Base center with small random offset
            int cx = level.getX() + rng.nextInt(CLUSTER_OFFSET_MAX * 2 + 1) - CLUSTER_OFFSET_MAX;
            int cy = level.getY() + rng.nextInt(CLUSTER_OFFSET_MAX * 2 + 1) - CLUSTER_OFFSET_MAX;

            // Random radius variation
            float radiusFactor = CLUSTER_RADIUS_MIN_FACTOR
                + rng.nextFloat() * (CLUSTER_RADIUS_MAX_FACTOR - CLUSTER_RADIUS_MIN_FACTOR);
            int radius = Math.max(2, (int) (baseRadius * radiusFactor));

            // Ovoid factor
            float scaleX = 1.0f + (rng.nextFloat() - 0.5f) * (1f - CLUSTER_OVOID_FACTOR) * 2;
            float scaleY = 1.0f + (rng.nextFloat() - 0.5f) * (1f - CLUSTER_OVOID_FACTOR) * 2;

            Entity.Order primary = level.levelData.getPrimaryOrder();
            Entity.Order secondary = level.levelData.getSecondaryOrder();

            if (secondary != null && secondary != Entity.Order.NEUTRAL) {
                mergedClusterGenerator.generate(cx, cy, radius, primary, secondary);
            } else {
                createRadialCluster(cx, cy, radius, primary, scaleX, scaleY);
            }
        }

        // Generate small, irrelevant clusters
        generateSmallIsolatedClusters(20); // 20 small clusters, adjust as needed
    }

    /**
     * Generates small random clusters that are mostly isolated
     */
    private void generateSmallIsolatedClusters(int count) {
        LevelGridCell[][] grid = generator.getGrid();

        for (int i = 0; i < count; i++) {
            // Random center
            int cx = rng.nextInt(LevelGridGenerator.GRID_WIDTH);
            int cy = rng.nextInt(LevelGridGenerator.GRID_HEIGHT);

            // Skip if this spot is too close to existing influence
            if (isNearInfluence(grid, cx, cy, 2)) continue;

            // Random tiny radius (0 = smallest, up to normal cluster radius)
            int radius = rng.nextInt(10);

            // Small ovoid shape
            float scaleX = 0.7f + rng.nextFloat() * 0.6f; // 0.7–1.3
            float scaleY = 0.7f + rng.nextFloat() * 0.6f;

            // Pick a random order
            Entity.Order order = generator.randomOrderNonNeutral();

            createRadialCluster(cx, cy, radius, order, scaleX, scaleY);
        }
    }

    /**
     * Checks if any influence exists nearby within a given distance
     */
    private boolean isNearInfluence(LevelGridCell[][] grid, int cx, int cy, int distance) {
        for (int dx = -distance; dx <= distance; dx++) {
            for (int dy = -distance; dy <= distance; dy++) {
                int gx = cx + dx;
                int gy = cy + dy;
                if (!valid(gx, gy)) continue;
                for (Entity.Order o : Entity.Order.values()) {
                    if (o == Entity.Order.NEUTRAL) continue;
                    if (grid[gx][gy].getInfluence(o) > 0f) return true;
                }
            }
        }
        return false;
    }

    private void createRadialCluster(int cx, int cy, int radius, Entity.Order order, float scaleX, float scaleY) {
        int outerX = (int) (radius * GLOBAL_SIZE_MULTIPLIER * scaleX) + GLOBAL_SIZE_PADDING;
        int outerY = (int) (radius * GLOBAL_SIZE_MULTIPLIER * scaleY) + GLOBAL_SIZE_PADDING;

        LevelGridCell[][] grid = generator.getGrid();

        for (int dx = -outerX; dx <= outerX; dx++) {
            for (int dy = -outerY; dy <= outerY; dy++) {

                int gx = cx + dx;
                int gy = cy + dy;

                if (!valid(gx, gy)) continue;

                float dist = (float) Math.sqrt((dx / (float) outerX) * (dx / (float) outerX)
                    + (dy / (float) outerY) * (dy / (float) outerY));

                if (dist > 1.0f) continue;

                float normalized = Math.max(0f, 1f - dist);
                float falloff = (float) Math.pow(normalized, GLOBAL_FALLOFF_EXPONENT);
                falloff *= GLOBAL_EDGE_HARDNESS;

                addInfluence(grid, gx, gy, order, falloff);
            }
        }
    }

    public void generateBackgroundStreaks(int count) {
        LevelGridCell[][] grid = generator.getGrid();

        for (int i = 0; i < count; i++) {
            // Random starting point
            int startX = rng.nextInt(LevelGridGenerator.GRID_WIDTH);
            int startY = rng.nextInt(LevelGridGenerator.GRID_HEIGHT);

            // Random direction
            float angle = (float)(rng.nextFloat() * Math.PI * 2);
            float cos = (float)Math.cos(angle);
            float sin = (float)Math.sin(angle);

            // Random streak properties
            int length = 30 + rng.nextInt(40);  // 30–70 tiles
            int width  = 1 + rng.nextInt(2);    // 1–2 tiles wide
            float maxInfluence = 0.2f + rng.nextFloat() * 0.1f; // 0.2–0.3 at center

            // Pick a random non-neutral order
            Entity.Order order = generator.randomOrderNonNeutral();

            for (int step = 0; step < length; step++) {
                int x = startX + Math.round(cos * step);
                int y = startY + Math.round(sin * step);

                // Add slight curvature
                x += rng.nextInt(3) - 1;
                y += rng.nextInt(3) - 1;

                for (int dx = -width; dx <= width; dx++) {
                    for (int dy = -width; dy <= width; dy++) {
                        int gx = x + dx;
                        int gy = y + dy;

                        if (!valid(gx, gy)) continue;

                        // Skip if cell already has influence
                        boolean occupied = false;
                        for (Entity.Order o : Entity.Order.values()) {
                            if (o == Entity.Order.NEUTRAL) continue;
                            if (grid[gx][gy].getInfluence(o) > 0f) {
                                occupied = true;
                                break;
                            }
                        }
                        if (occupied) continue;

                        // Calculate influence falloff: stronger at the center of the streak
                        float distToCenter = Math.abs(step - length / 2f) / (length / 2f); // 0=center, 1=end
                        float influence = maxInfluence * (1f - distToCenter);              // taper to ends

                        grid[gx][gy].addInfluence(order, influence);
                    }
                }
            }
        }
    }


    public Color computeCellColor(LevelGridCell cell) {
        float r = 0, g = 0, b = 0, total = 0;

        for (Entity.Order o : Entity.Order.values()) {
            if (o == Entity.Order.NEUTRAL) continue;

            float inf = cell.getInfluence(o);
            if (inf <= 0) continue;

            Color c = generator.getOrderColor(o);
            r += c.r * inf;
            g += c.g * inf;
            b += c.b * inf;
            total += inf;
        }

        float weight = Math.max(0, 1f - total);
        Color neutral = generator.getOrderColor(Entity.Order.NEUTRAL);

        r += neutral.r * weight;
        g += neutral.g * weight;
        b += neutral.b * weight;

        return new Color(r, g, b, 1f);
    }

    private boolean valid(int gx, int gy) {
        return gx >= 0 && gx < LevelGridGenerator.GRID_WIDTH &&
            gy >= 0 && gy < LevelGridGenerator.GRID_HEIGHT;
    }

    private void addInfluence(LevelGridCell[][] grid, int gx, int gy,
                              Entity.Order order, float influence) {
        influence *= GLOBAL_INFLUENCE_MULTIPLIER;
        if (influence > 0) {
            grid[gx][gy].addInfluence(order, influence);
        }
    }
}
