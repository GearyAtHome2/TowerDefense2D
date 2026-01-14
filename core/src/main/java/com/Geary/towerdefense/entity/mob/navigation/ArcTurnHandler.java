package com.Geary.towerdefense.entity.mob.navigation;

import com.Geary.towerdefense.Direction;
import com.Geary.towerdefense.entity.world.Cell;

import static java.lang.Math.*;

public class ArcTurnHandler {

    private float arcRadius;
    private float arcAngle;
    private float arcStartAngle;
    private float arcEndAngle;
    private float arcCenterX;
    private float arcCenterY;
    public float arcProgress = 0f;
    private boolean inArcTurn = false;

    public boolean isInArcTurn() {
        return inArcTurn;
    }

    public void setupArc(Cell cell, Direction from, Direction to, float mobCenterX, float mobCenterY, float cellSize) {
        inArcTurn = true;

        float cx = cell.x;
        float cy = cell.y;
        float size = cellSize;

        if (from == Direction.RIGHT && to == Direction.UP) { arcCenterX = cx; arcCenterY = cy + size; }
        else if (from == Direction.RIGHT && to == Direction.DOWN) { arcCenterX = cx; arcCenterY = cy; }
        else if (from == Direction.LEFT && to == Direction.UP) { arcCenterX = cx + size; arcCenterY = cy + size; }
        else if (from == Direction.LEFT && to == Direction.DOWN) { arcCenterX = cx + size; arcCenterY = cy; }
        else if (from == Direction.UP && to == Direction.RIGHT) { arcCenterX = cx + size; arcCenterY = cy; }
        else if (from == Direction.UP && to == Direction.LEFT) { arcCenterX = cx; arcCenterY = cy; }
        else if (from == Direction.DOWN && to == Direction.RIGHT) { arcCenterX = cx + size; arcCenterY = cy + size; }
        else if (from == Direction.DOWN && to == Direction.LEFT) { arcCenterX = cx; arcCenterY = cy + size; }
        else { arcCenterX = cx + size / 2f; arcCenterY = cy + size / 2f; }

        arcRadius = (float) hypot(mobCenterX - arcCenterX, mobCenterY - arcCenterY);
        arcRadius = max(arcRadius, 0.001f);

        arcStartAngle = (float) atan2(mobCenterY - arcCenterY, mobCenterX - arcCenterX);

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
        arcEndAngle = (float) (arcStartAngle + (cross > 0 ? Math.PI / 2f : -Math.PI / 2f));

        arcAngle = arcStartAngle;
        arcProgress = 0f;
    }

    public float[] updateArc(float deltaDist) {
        if (!inArcTurn) return null;

        float arcLength = arcRadius * Math.abs(arcEndAngle - arcStartAngle);

        arcProgress += deltaDist;
        arcProgress = clamp(arcProgress, 0f, arcLength);

        float t = arcProgress / arcLength;
        arcAngle = arcStartAngle + t * (arcEndAngle - arcStartAngle);

        if (arcProgress >= arcLength - 0.001f) {
            arcProgress = arcLength;
            arcAngle = arcEndAngle;
            inArcTurn = false;
        }

        float x = arcCenterX + (float) cos(arcAngle) * arcRadius;
        float y = arcCenterY + (float) sin(arcAngle) * arcRadius;
//        arcProgress = Math.max(arcProgress, 0f);
        return new float[]{x, y};
    }

    public void rebuildArcPreserveProgress(
        Cell cell,
        Direction from,
        Direction to,
        float mobCenterX,
        float mobCenterY,
        float cellSize
    ) {
        // Recompute arc center (same logic as setupArc)
        float cx = cell.x;
        float cy = cell.y;
        float size = cellSize;

        if (from == Direction.RIGHT && to == Direction.UP) { arcCenterX = cx; arcCenterY = cy + size; }
        else if (from == Direction.RIGHT && to == Direction.DOWN) { arcCenterX = cx; arcCenterY = cy; }
        else if (from == Direction.LEFT && to == Direction.UP) { arcCenterX = cx + size; arcCenterY = cy + size; }
        else if (from == Direction.LEFT && to == Direction.DOWN) { arcCenterX = cx + size; arcCenterY = cy; }
        else if (from == Direction.UP && to == Direction.RIGHT) { arcCenterX = cx + size; arcCenterY = cy; }
        else if (from == Direction.UP && to == Direction.LEFT) { arcCenterX = cx; arcCenterY = cy; }
        else if (from == Direction.DOWN && to == Direction.RIGHT) { arcCenterX = cx + size; arcCenterY = cy + size; }
        else if (from == Direction.DOWN && to == Direction.LEFT) { arcCenterX = cx; arcCenterY = cy + size; }

        arcRadius = (float) hypot(mobCenterX - arcCenterX, mobCenterY - arcCenterY);
        arcRadius = Math.max(arcRadius, 0.001f);

        // Compute current angle from center
        arcAngle = (float) atan2(mobCenterY - arcCenterY, mobCenterX - arcCenterX);

        // Recompute progress based on angle delta
        float totalAngle = arcEndAngle - arcStartAngle;
        float currentAngle = arcAngle - arcStartAngle;

        float deltaAngle = arcAngle - arcStartAngle;
        while (deltaAngle > Math.PI) deltaAngle -= 2*Math.PI;
        while (deltaAngle < -Math.PI) deltaAngle += 2*Math.PI;

        float t = deltaAngle / totalAngle;   // use deltaAngle instead of currentAngle directly
        t = clamp(t, 0f, 1f);
//        arcProgress = t * arcRadius * Math.abs(totalAngle);

        float arcLength = arcRadius * Math.abs(totalAngle);
        arcProgress = t * arcLength;

        inArcTurn = true;
    }

//    private float computeIdealArcRadius(Cell cell, Direction from, Direction to, float cellSize) {
//        float cx = cell.x;
//        float cy = cell.y;
//        float size = cellSize;
//
//        float idealX = 0, idealY = 0;
//
//        if (from == Direction.RIGHT && to == Direction.UP) { idealX = cx; idealY = cy + size; }
//        else if (from == Direction.RIGHT && to == Direction.DOWN) { idealX = cx; idealY = cy; }
//        else if (from == Direction.LEFT && to == Direction.UP) { idealX = cx + size; idealY = cy + size; }
//        else if (from == Direction.LEFT && to == Direction.DOWN) { idealX = cx + size; idealY = cy; }
//        else if (from == Direction.UP && to == Direction.RIGHT) { idealX = cx + size; idealY = cy; }
//        else if (from == Direction.UP && to == Direction.LEFT) { idealX = cx; idealY = cy; }
//        else if (from == Direction.DOWN && to == Direction.RIGHT) { idealX = cx + size; idealY = cy + size; }
//        else if (from == Direction.DOWN && to == Direction.LEFT) { idealX = cx; idealY = cy + size; }
//
//        float dx = getCenterX() - idealX;
//        float dy = getCenterY() - idealY;
//        float r = (float) Math.hypot(dx, dy);
//
//        // Clamp to some minimum fraction of cell size instead of 0
//        return Math.max(r, cellSize * 0.25f);
//    }


    private float clamp(float v, float min, float max) {
        return Math.max(min, Math.min(max, v));
    }

}
