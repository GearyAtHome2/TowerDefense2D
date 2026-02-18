package com.Geary.towerdefense.levelSelect.generation;

import com.Geary.towerdefense.entity.Entity;
import com.Geary.towerdefense.levelSelect.LevelGridCell;

import java.util.Random;

import static com.Geary.towerdefense.levelSelect.generation.LevelClusterGenerator.*;

public class MergedClusterGenerator {

    private final LevelGridGenerator generator;
    private final Random rng = new Random();


    public MergedClusterGenerator(LevelGridGenerator generator) {
        this.generator = generator;
    }

    public void generate(int cx, int cy, int radius,
                         Entity.Order primary,
                         Entity.Order secondary) {

        switch (rng.nextInt(7)) {
            case 0 -> createSpiralCluster(cx, cy, radius, primary, secondary);
            case 1 -> createBinaryOrbitCluster(cx, cy, radius, primary, secondary);
            case 2 -> createYinYangCluster(cx, cy, radius, primary, secondary);
            case 3 -> createTurbulentCluster(cx, cy, radius, primary, secondary);
            case 4 -> createHelixCluster(cx, cy, radius, primary, secondary);
//            case 5 -> createFracturedCluster(cx, cy, radius, primary, secondary);
            case 5 -> createGravitationalLensCluster(cx, cy, radius, primary, secondary);
            case 6 -> createGravitationalLensCluster(cx, cy, radius, primary, secondary);
        }
    }

    /* ===================== 1. SPIRAL ========================= */
    private void createSpiralCluster(int cx, int cy, int radius,
                                     Entity.Order primary,
                                     Entity.Order secondary) {

        LevelGridCell[][] grid = generator.getGrid();
        int outer = (int)(radius * GLOBAL_SIZE_MULTIPLIER) + GLOBAL_SIZE_PADDING;
        float baseRotation = rng.nextFloat() * (float) (Math.PI * 2);

        for (int dx = -outer; dx <= outer; dx++) {
            for (int dy = -outer; dy <= outer; dy++) {

                int gx = cx + dx;
                int gy = cy + dy;
                if (!valid(gx, gy)) continue;

                float dist = dist(dx, dy);
                if (dist > outer) continue;

                float angle = (float)Math.atan2(dy, dx) + baseRotation;
                float twist = dist * 0.4f * GLOBAL_TWIST_STRENGTH;

                float arm1 = (float) Math.sin(angle + twist);
                float arm2 = (float) Math.sin(angle + twist + Math.PI);

                float falloff = Math.max(0f, 1f - dist / outer);
                falloff = (float) Math.pow(falloff, GLOBAL_FALLOFF_EXPONENT) * GLOBAL_EDGE_HARDNESS;

                add(grid, gx, gy, primary, Math.max(0, arm1) * falloff);
                add(grid, gx, gy, secondary, Math.max(0, arm2) * falloff);
            }
        }
    }

    /* ================= 2. BINARY ORBIT ====================== */
    private void createBinaryOrbitCluster(int cx, int cy, int radius,
                                          Entity.Order primary,
                                          Entity.Order secondary) {

        LevelGridCell[][] grid = generator.getGrid();
        int outer = (int)(radius * GLOBAL_SIZE_MULTIPLIER) + GLOBAL_SIZE_PADDING;

        float offsetAngle = rng.nextFloat() * (float)(Math.PI * 2);
        float offsetDist = radius * 0.5f * GLOBAL_SIZE_MULTIPLIER;

        float ox1 = (float)Math.cos(offsetAngle) * offsetDist;
        float oy1 = (float)Math.sin(offsetAngle) * offsetDist;
        float ox2 = -ox1;
        float oy2 = -oy1;

        for (int dx = -outer; dx <= outer; dx++) {
            for (int dy = -outer; dy <= outer; dy++) {

                int gx = cx + dx;
                int gy = cy + dy;
                if (!valid(gx, gy)) continue;

                float d1 = dist(dx - ox1, dy - oy1);
                float d2 = dist(dx - ox2, dy - oy2);

                if (d1 < outer)
                    add(grid, gx, gy, primary, computeFalloff(d1, outer));

                if (d2 < outer)
                    add(grid, gx, gy, secondary, computeFalloff(d2, outer));
            }
        }
    }

    /* ==================== 3. YIN YANG ======================== */
    private void createYinYangCluster(int cx, int cy, int radius,
                                      Entity.Order primary,
                                      Entity.Order secondary) {

        LevelGridCell[][] grid = generator.getGrid();
        int outer = (int)(radius * GLOBAL_SIZE_MULTIPLIER) + GLOBAL_SIZE_PADDING;
        float rotation = rng.nextFloat() * (float)(Math.PI * 2);

        for (int dx = -outer; dx <= outer; dx++) {
            for (int dy = -outer; dy <= outer; dy++) {

                int gx = cx + dx;
                int gy = cy + dy;
                if (!valid(gx, gy)) continue;

                float dist = dist(dx, dy);
                if (dist > outer) continue;

                float angle = (float)Math.atan2(dy, dx) + rotation;
                float curve = (float)Math.sin(angle * 1.5f * GLOBAL_TWIST_STRENGTH);

                float falloff = computeFalloff(dist, outer);

                if (curve > 0)
                    add(grid, gx, gy, primary, falloff);
                else
                    add(grid, gx, gy, secondary, falloff);
            }
        }
    }

    /* ================== 4. TURBULENT STORM =================== */
    private void createTurbulentCluster(int cx, int cy, int radius,
                                        Entity.Order primary,
                                        Entity.Order secondary) {

        LevelGridCell[][] grid = generator.getGrid();
        int outer = (int)(radius * GLOBAL_SIZE_MULTIPLIER) + GLOBAL_SIZE_PADDING;

        float noiseSeed = rng.nextFloat() * 1000f;

        for (int dx = -outer; dx <= outer; dx++) {
            for (int dy = -outer; dy <= outer; dy++) {

                int gx = cx + dx;
                int gy = cy + dy;
                if (!valid(gx, gy)) continue;

                float dist = dist(dx, dy);
                if (dist > outer) continue;

                float noise = (float)Math.sin(dx * 0.4f * GLOBAL_NOISE_STRENGTH + noiseSeed)
                    + (float)Math.cos(dy * 0.4f * GLOBAL_NOISE_STRENGTH + noiseSeed);

                float falloff = computeFalloff(dist, outer);

                if (noise > 0)
                    add(grid, gx, gy, primary, falloff);
                else
                    add(grid, gx, gy, secondary, falloff);
            }
        }
    }

    /* =================== 5. DOUBLE HELIX ===================== */
    private void createHelixCluster(int cx, int cy, int radius,
                                    Entity.Order primary,
                                    Entity.Order secondary) {

        LevelGridCell[][] grid = generator.getGrid();
        int outer = (int)(radius * GLOBAL_SIZE_MULTIPLIER) + GLOBAL_SIZE_PADDING;

        float rotation = rng.nextFloat() * (float)(Math.PI * 2);

        for (int dx = -outer; dx <= outer; dx++) {
            for (int dy = -outer; dy <= outer; dy++) {

                int gx = cx + dx;
                int gy = cy + dy;
                if (!valid(gx, gy)) continue;

                float dist = dist(dx, dy);
                if (dist > outer) continue;

                float angle = (float)Math.atan2(dy, dx) + rotation;
                float wave = (float)Math.sin(angle * 3 * GLOBAL_TWIST_STRENGTH + dist * 0.5f);

                float falloff = computeFalloff(dist, outer);

                if (wave > 0)
                    add(grid, gx, gy, primary, falloff);
                else
                    add(grid, gx, gy, secondary, falloff);
            }
        }
    }

    /* ================== 6. FRACTURED SHARDS ================== */
    private void createFracturedCluster(int cx, int cy, int radius,
                                        Entity.Order primary,
                                        Entity.Order secondary) {

        LevelGridCell[][] grid = generator.getGrid();
        int outer = (int)(radius * GLOBAL_SIZE_MULTIPLIER) + GLOBAL_SIZE_PADDING;

        float angleSplit = rng.nextFloat() * (float)(Math.PI * 2);

        for (int dx = -outer; dx <= outer; dx++) {
            for (int dy = -outer; dy <= outer; dy++) {

                int gx = cx + dx;
                int gy = cy + dy;
                if (!valid(gx, gy)) continue;

                float dist = dist(dx, dy);
                if (dist > outer) continue;

                float angle = (float)Math.atan2(dy, dx);
                float shard = (float)Math.sin((angle - angleSplit) * 6 * GLOBAL_TWIST_STRENGTH);

                float falloff = computeFalloff(dist, outer);

                if (shard > 0)
                    add(grid, gx, gy, primary, falloff);
                else
                    add(grid, gx, gy, secondary, falloff);
            }
        }
    }

    /* ================= 7. GRAVITATIONAL LENS ================= */
    private void createGravitationalLensCluster(int cx, int cy, int radius,
                                                Entity.Order primary,
                                                Entity.Order secondary) {

        LevelGridCell[][] grid = generator.getGrid();
        int outer = (int)(radius * GLOBAL_SIZE_MULTIPLIER) + GLOBAL_SIZE_PADDING;

        for (int dx = -outer; dx <= outer; dx++) {
            for (int dy = -outer; dy <= outer; dy++) {

                int gx = cx + dx;
                int gy = cy + dy;
                if (!valid(gx, gy)) continue;

                float dist = dist(dx, dy);
                if (dist > outer) continue;

                float ring = Math.abs(dist - radius * 0.5f);

                float falloff = computeFalloff(dist, outer);

                if (ring < radius * 0.2f)
                    add(grid, gx, gy, secondary, falloff);
                else
                    add(grid, gx, gy, primary, falloff);
            }
        }
    }

    /* ====================== UTILITIES ======================== */
    private boolean valid(int gx, int gy) {
        return gx >= 0 && gx < LevelGridGenerator.GRID_WIDTH &&
            gy >= 0 && gy < LevelGridGenerator.GRID_HEIGHT;
    }

    private float dist(float x, float y) {
        return (float)Math.sqrt(x * x + y * y);
    }

    private float computeFalloff(float dist, float outer) {
        float f = Math.max(0f, 1f - dist / outer);
        f = (float) Math.pow(f, GLOBAL_FALLOFF_EXPONENT) * GLOBAL_EDGE_HARDNESS;
        return f;
    }

    private void add(LevelGridCell[][] grid, int gx, int gy,
                     Entity.Order order, float influence) {
        influence *= GLOBAL_INFLUENCE_MULTIPLIER;
        if (influence > 0)
            grid[gx][gy].addInfluence(order, influence);
    }
}
