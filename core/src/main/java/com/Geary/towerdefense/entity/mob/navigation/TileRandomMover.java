package com.Geary.towerdefense.entity.mob.navigation;

import static com.badlogic.gdx.math.MathUtils.random;
import static java.lang.Math.*;

public class TileRandomMover {

    private final float cellSize;
    private final float center;
    private final float hardMin;
    private final float hardMax;

    public TileRandomMover(int cellSize) {
        this.cellSize = cellSize;
        this.center   = cellSize * 0.5f;
        this.hardMin  = cellSize * 0.1f;
        this.hardMax  = cellSize * 0.9f;
    }

    public float computeMovement(float axisValue, float delta, float baseMoveProb) {

        // Clamp to cell
        axisValue = max(0f, min(cellSize, axisValue));

        // Chance to move at all
        if (random() > baseMoveProb)
            return 0f;

        float offset = axisValue - center;
        float absOffset = abs(offset);

        float centerBiasProb;

        // Hard walls
        if (axisValue <= hardMin || axisValue >= hardMax) {
            centerBiasProb = 1f;
        } else {
            // Linear ramp: 0 at centre, 1 at hardMin/hardMax
            centerBiasProb = absOffset / (center - hardMin);
        }

        float dir;

        if (random() < centerBiasProb) {
            // Move toward centre
            dir = offset > 0 ? -1f : 1f;
        } else {
            // Pure random
            dir = random() < 0.5f ? -1f : 1f;
        }

        float maxStep = 0.25f * cellSize;
        return dir * maxStep * delta;
    }
}
