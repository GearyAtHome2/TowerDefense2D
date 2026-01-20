package com.Geary.towerdefense.entity.mob.navigation;

import com.Geary.towerdefense.Direction;
import com.Geary.towerdefense.entity.world.Cell;

import static java.lang.Math.*;

public class ArcTurnHandler {

    private float arcRadius;

    // Angles define geometry only
    private float arcStartAngle;
    private float arcEndAngle;

    private float arcCenterX;
    private float arcCenterY;

    // Parametric progress (distance along arc)
    private float arcProgress = 0f;
    private float arcLength = 0f;

    private boolean inArcTurn = false;

    public boolean isInArcTurn() {
        return inArcTurn;
    }

    public void setupArc(
        Cell cell,
        Direction from,
        Direction to,
        float mobCenterX,
        float mobCenterY,
        float cellSize
    ) {
        inArcTurn = true;

        float cx = cell.x;
        float cy = cell.y;
        float size = cellSize;

        // --- Arc center ---
        if (from == Direction.RIGHT && to == Direction.UP) { arcCenterX = cx; arcCenterY = cy + size; }
        else if (from == Direction.RIGHT && to == Direction.DOWN) { arcCenterX = cx; arcCenterY = cy; }
        else if (from == Direction.LEFT && to == Direction.UP) { arcCenterX = cx + size; arcCenterY = cy + size; }
        else if (from == Direction.LEFT && to == Direction.DOWN) { arcCenterX = cx + size; arcCenterY = cy; }
        else if (from == Direction.UP && to == Direction.RIGHT) { arcCenterX = cx + size; arcCenterY = cy; }
        else if (from == Direction.UP && to == Direction.LEFT) { arcCenterX = cx; arcCenterY = cy; }
        else if (from == Direction.DOWN && to == Direction.RIGHT) { arcCenterX = cx + size; arcCenterY = cy + size; }
        else if (from == Direction.DOWN && to == Direction.LEFT) { arcCenterX = cx; arcCenterY = cy + size; }
        else { arcCenterX = cx + size / 2f; arcCenterY = cy + size / 2f; }

        // --- Radius ---
        arcRadius = (float) hypot(mobCenterX - arcCenterX, mobCenterY - arcCenterY);
        arcRadius = max(arcRadius, 0.001f);

        // --- Start angle ---
        arcStartAngle = (float) atan2(
            mobCenterY - arcCenterY,
            mobCenterX - arcCenterX
        );

        // --- Turn direction (CW vs CCW) ---
        float dxEntry = switch (from) {
            case RIGHT -> 1;
            case LEFT -> -1;
            default -> 0;
        };
        float dyEntry = switch (from) {
            case UP -> 1;
            case DOWN -> -1;
            default -> 0;
        };

        float dxExit = switch (to) {
            case RIGHT -> 1;
            case LEFT -> -1;
            default -> 0;
        };
        float dyExit = switch (to) {
            case UP -> 1;
            case DOWN -> -1;
            default -> 0;
        };

        float cross = dxEntry * dyExit - dyEntry * dxExit;
        float deltaAngle = (float) (cross > 0 ? PI / 2f : -PI / 2f);

        arcEndAngle = arcStartAngle + deltaAngle;

        // --- Parametric setup ---
        arcLength = arcRadius * abs(deltaAngle);
        arcProgress = 0f;
    }

    public float[] updateArc(float deltaDist) {
        if (!inArcTurn) return null;

        arcProgress += deltaDist;
        arcProgress = clamp(arcProgress, 0f, arcLength);

        float t = arcLength > 0f ? arcProgress / arcLength : 1f;
        float angle = arcStartAngle + t * (arcEndAngle - arcStartAngle);

        float x = arcCenterX + (float) cos(angle) * arcRadius;
        float y = arcCenterY + (float) sin(angle) * arcRadius;

        if (arcProgress >= arcLength - 0.001f) {
            arcProgress = arcLength;
            inArcTurn = false;
        }

        return new float[]{x, y};
    }

    /**
     * Rebuild geometry ONLY.
     * Progress is preserved parametrically.
     */
    public void rebuildArcPreserveProgress(
        Cell cell,
        Direction from,
        Direction to,
        float mobCenterX,
        float mobCenterY,
        float cellSize
    ) {
        if (!inArcTurn) return;

        float cx = cell.x;
        float cy = cell.y;
        float size = cellSize;

        // --- Recompute arc center ---
        if (from == Direction.RIGHT && to == Direction.UP) { arcCenterX = cx; arcCenterY = cy + size; }
        else if (from == Direction.RIGHT && to == Direction.DOWN) { arcCenterX = cx; arcCenterY = cy; }
        else if (from == Direction.LEFT && to == Direction.UP) { arcCenterX = cx + size; arcCenterY = cy + size; }
        else if (from == Direction.LEFT && to == Direction.DOWN) { arcCenterX = cx + size; arcCenterY = cy; }
        else if (from == Direction.UP && to == Direction.RIGHT) { arcCenterX = cx + size; arcCenterY = cy; }
        else if (from == Direction.UP && to == Direction.LEFT) { arcCenterX = cx; arcCenterY = cy; }
        else if (from == Direction.DOWN && to == Direction.RIGHT) { arcCenterX = cx + size; arcCenterY = cy + size; }
        else if (from == Direction.DOWN && to == Direction.LEFT) { arcCenterX = cx; arcCenterY = cy + size; }

        // --- Radius changes due to knockback ---
        arcRadius = (float) hypot(mobCenterX - arcCenterX, mobCenterY - arcCenterY);
        arcRadius = max(arcRadius, 0.001f);

        // --- Recompute arc length, preserve normalized progress ---
        float t = arcLength > 0f ? arcProgress / arcLength : 0f;
        arcLength = arcRadius * abs(arcEndAngle - arcStartAngle);
        arcProgress = clamp(t * arcLength, 0f, arcLength);

        inArcTurn = true;
    }

    private float clamp(float v, float min, float max) {
        return max(min, min(max, v));
    }
}
