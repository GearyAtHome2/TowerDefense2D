package com.Geary.towerdefense.entity.world;

import com.Geary.towerdefense.Direction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class PathGenerator {

    private final int gridWidth;
    private final int gridHeight;
    private final int cellSize;
    private final Random random = new Random();

    public PathGenerator(int gridWidth, int gridHeight, int cellSize) {
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.cellSize = cellSize;
    }


//    public List<Cell> generatePathAttempts() {
//        List<Cell> cells = new ArrayList<>();
//        for (int i = 0; i < 3; i++) {
//            try {
//                cells = generatePath();
//            } catch (Exception e) {
//            }
//        }
//        return cells;
//    }

    public List<Cell> generatePath() {
        boolean init = true;//flag to ensure the first tile isn't a turn
        Cell[][] grid = new Cell[gridWidth][gridHeight];
        List<Cell> path = new ArrayList<>();

        int x = random.nextInt(gridWidth);
        ;
        int y = random.nextInt(gridHeight);
        ;
        Direction dir = Direction.RIGHT;

        int targetLength = 20 + random.nextInt(9); // 20–30 tiles

        for (int i = 0; i < targetLength; i++) {
            // Choose next direction
            List<Direction> options = getValidDirections(grid, x, y, dir);
            if (options.isEmpty()) break; // dead end

            Direction nextDir = init ? dir : options.get(random.nextInt(options.size()));// don't allow a first tile turn
            if (init) {
                System.out.println("init params prefilter:" + x + "," + y);
                if (x < 2) {
                    x = 2;
                }
                if (x > gridWidth - 2) {
                    x = gridWidth - 2;
                }
                if (y < 2) {
                    y = 2;
                }
                if (y > gridHeight - 2) {
                    y = gridHeight - 2;
                }
                Cell straight = new Cell(Cell.Type.PATH, x * cellSize, y * cellSize, dir);
                path.add(straight);
                grid[x][y] = straight;
                System.out.println(i + ": Added a starter straight tile type pointing " + dir);
                init = false;
            } else {
                if (nextDir != dir) {
                    Cell turn = new Cell(Cell.Type.TURN, x * cellSize, y * cellSize, dir, nextDir);
                    path.add(turn);
                    grid[x][y] = turn;
                    System.out.println(i + ": Added a turn tile type from " + dir + " to " + nextDir);
                } else {
                    Cell straight = new Cell(Cell.Type.PATH, x * cellSize, y * cellSize, dir);
                    path.add(straight);
                    grid[x][y] = straight;
                    System.out.println(i + ": Added a straight tile type pointing " + dir);
                }
            }
            x += dx(nextDir);
            y += dy(nextDir);
            dir = nextDir;
        }
        return path;
    }

    // Convert direction to grid offset
    private int dx(Direction dir) {
        return switch (dir) {
            case LEFT -> -1;
            case RIGHT -> 1;
            default -> 0;
        };
    }

    private int dy(Direction dir) {
        return switch (dir) {
            case UP -> 1;
            case DOWN -> -1;
            default -> 0;
        };
    }

    // Return a list of valid next directions
    private List<Direction> getValidDirections(Cell[][] grid, int x, int y, Direction current) {
        List<Direction> candidates = new ArrayList<>();
        // Straight
        if (canPlace(x + dx(current), y + dy(current), grid)) candidates.add(current);
        Direction left = turnLeft(current);
        if (canPlace(x + dx(left), y + dy(left), grid)) candidates.add(left);
        Direction right = turnRight(current);
        if (canPlace(x + dx(right), y + dy(right), grid)) candidates.add(right);
        Collections.shuffle(candidates, random);
        return candidates;
    }

    private boolean canPlace(int x, int y, Cell[][] grid) {
        if (x < 0 || x >= gridWidth || y < 0 || y >= gridHeight) return false;
        if (grid[x][y] != null) return false;

        int[][] dirs = {
            {1, 0},   // right
            {-1, 0},   // left
            {0, 1},   // up
            {0, -1}    // down
        };
        for (int[] d : dirs) {
            int nx = x + d[0];
            int ny = y + d[1];
            if (nx >= 0 && nx < gridWidth && ny >= 0 && ny < gridHeight) {
                if (grid[nx][ny] != null) return false;
            }
        }
        return true;
    }


    private Direction turnLeft(Direction dir) {
        return switch (dir) {
            case UP -> Direction.LEFT;
            case DOWN -> Direction.RIGHT;
            case LEFT -> Direction.DOWN;
            case RIGHT -> Direction.UP;
            default -> dir;
        };
    }

    private Direction turnRight(Direction dir) {
        return switch (dir) {
            case UP -> Direction.RIGHT;
            case DOWN -> Direction.LEFT;
            case LEFT -> Direction.UP;
            case RIGHT -> Direction.DOWN;
            default -> dir;
        };
    }
}
