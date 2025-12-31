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
    private static final int PATH_LENGTH = 80;

    public PathGenerator(int gridWidth, int gridHeight, int cellSize) {
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.cellSize = cellSize;
    }

    public List<Cell> generatePathAttempts() {
        int len = 0;
        int index = 1;
        List<Cell> path = new ArrayList<>();
        while (len < PATH_LENGTH && index < 80) {
            System.out.println("attempt " + index + ", previous path length attempt: " + len + ", attempting to generate again");
            path = generatePath();
            len = path.size();
            index++;
        }
        return path;
    }

    private List<Cell> generatePath() {
        boolean init = true;//flag to ensure the first tile isn't a turn
        Cell[][] grid = new Cell[gridWidth][gridHeight];
        List<Cell> path = new ArrayList<>();

        int x = random.nextInt(gridWidth);
        int y = random.nextInt(gridHeight);

        Direction dir = Direction.RIGHT;

        int targetLength = PATH_LENGTH + random.nextInt(15);

        Cell cell = new Cell();
        for (int i = 0; i < targetLength; i++) {
            // Choose next direction
            List<Direction> options = getValidDirections(grid, x, y, dir);
            if (options.isEmpty()) break; // dead end

            Direction nextDir;
            if (i > targetLength - 10) {
                nextDir = chooseNextDirectionWithOpenness(options, dir, grid, x, y);
            } else {
                nextDir = chooseNextDirection(options, dir); // normal straight bias
            }
            if (init) {
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
                cell = new Cell(Cell.Type.PATH, x * cellSize, y * cellSize, dir);
                path.add(cell);
                grid[x][y] = cell;
                init = false;
            } else {
                if (nextDir != dir) {
                    cell = new Cell(Cell.Type.TURN, x * cellSize, y * cellSize, dir, nextDir);
                    path.add(cell);
                    grid[x][y] = cell;
                } else {
                    cell = new Cell(Cell.Type.PATH, x * cellSize, y * cellSize, dir);
                    path.add(cell);
                    grid[x][y] = cell;
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

    private Direction chooseNextDirection(List<Direction> options, Direction current) {
        // Assign weights
        int straightWeight = 70;  // 70% chance to keep going straight
        int turnWeight = 15;      // 15% chance for each turn

        List<Direction> weightedList = new ArrayList<>();
        for (Direction dir : options) {
            if (dir == current) {
                for (int i = 0; i < straightWeight; i++) weightedList.add(dir);
            } else {
                for (int i = 0; i < turnWeight; i++) weightedList.add(dir);
            }
        }

        // Pick random from weighted list
        return weightedList.get(random.nextInt(weightedList.size()));
    }

    private Direction chooseNextDirectionWithOpenness(List<Direction> options, Direction current, Cell[][] grid, int x, int y) {
        // Bias toward straight movement
        Direction bestDir = current;
        int bestScore = -1;
        for (Direction dir : options) {
            int nx = x + dx(dir);
            int ny = y + dy(dir);
            int score = openness(grid, nx, ny);
            // Slight bonus for straight movement
            if (dir == current) score += 2;

            if (score > bestScore) {
                bestScore = score;
                bestDir = dir;
            }
        }
        System.out.println("score:"+bestScore);
        return bestDir;
    }

    private int openness(Cell[][] grid, int x, int y) {
        int emptyCount = 0;
        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                int nx = x + dx;
                int ny = y + dy;
                if (nx < 0 || nx >= gridWidth || ny < 0 || ny >= gridHeight) continue;
                if (grid[nx][ny] == null) emptyCount++;
            }
        }
        return emptyCount;
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
