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
    private float arcProgress = 0f;
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
        arcProgress = Math.max(arcProgress, 0f);
        return new float[]{x, y};
    }

    private float clamp(float v, float min, float max) {
        return Math.max(min, Math.min(max, v));
    }

}
