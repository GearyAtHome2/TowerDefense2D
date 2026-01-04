package com.Geary.towerdefense.entity.mob.navigation;

import static com.badlogic.gdx.math.MathUtils.random;
import static java.lang.Math.*;

public class TileRandomMover {

    private final int cellSize;

    public TileRandomMover(int cellSize) {
        this.cellSize = cellSize;
    }

    public float computeMovement(float axisValue, float delta) {
        float minBound = 0.05f * cellSize;
        float maxBound = 0.95f * cellSize;
        float center = 0.5f * cellSize;

        axisValue = max(minBound, min(maxBound, axisValue));

        float totalWidth = maxBound - minBound;
        float offsetFromCenter = axisValue - center;

        float baseMoveProb = 0.35f;
        float distanceFactor = abs(offsetFromCenter) / (totalWidth / 2f);
        float moveTowardCenterProb = baseMoveProb + distanceFactor * (1f - baseMoveProb);

        float probLeft, probRight;
        if (offsetFromCenter > 0) {
            probLeft = moveTowardCenterProb;
            probRight = baseMoveProb * (1f - distanceFactor);
        } else if (offsetFromCenter < 0) {
            probRight = moveTowardCenterProb;
            probLeft = baseMoveProb * (1f - distanceFactor);
        } else probLeft = probRight = baseMoveProb;

        float stayProb = max(0f, 1f - (probLeft + probRight));

        float r = random();
        float moveDir;
        if (r < probLeft) moveDir = -1f;
        else if (r < probLeft + probRight) moveDir = 1f;
        else moveDir = 0f;

        float maxMove = moveDir < 0 ? axisValue - minBound : maxBound - axisValue;
        return moveDir * min(abs(moveDir), maxMove) * delta * 10f;
    }
}
