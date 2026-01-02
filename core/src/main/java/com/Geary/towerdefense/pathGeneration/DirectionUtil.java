package com.Geary.towerdefense.pathGeneration;

import com.Geary.towerdefense.Direction;

public final class DirectionUtil {

    private DirectionUtil() {}

    public static int dx(Direction dir) {
        return switch (dir) {
            case LEFT -> -1;
            case RIGHT -> 1;
            default -> 0;
        };
    }

    public static int dy(Direction dir) {
        return switch (dir) {
            case UP -> 1;
            case DOWN -> -1;
            default -> 0;
        };
    }

    public static Direction turnLeft(Direction dir) {
        return switch (dir) {
            case UP -> Direction.LEFT;
            case DOWN -> Direction.RIGHT;
            case LEFT -> Direction.DOWN;
            case RIGHT -> Direction.UP;
            default -> dir;
        };
    }

    public static Direction turnRight(Direction dir) {
        return switch (dir) {
            case UP -> Direction.RIGHT;
            case DOWN -> Direction.LEFT;
            case LEFT -> Direction.UP;
            case RIGHT -> Direction.DOWN;
            default -> dir;
        };
    }

    public static Direction opposite(Direction dir) {
        return switch (dir) {
            case UP -> Direction.DOWN;
            case DOWN -> Direction.UP;
            case LEFT -> Direction.RIGHT;
            case RIGHT -> Direction.LEFT;
            default -> dir;
        };
    }
}
