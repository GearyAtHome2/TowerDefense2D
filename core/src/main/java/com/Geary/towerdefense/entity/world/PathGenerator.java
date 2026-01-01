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
    private static final int ZONE_SIZE = 7;

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
            path = generatePathFromHome();
            len = path.size();
            index++;
        }
        // Flip directions so path visually flows from ENEMY_ZONE → HOME_ZONE
        return flipPath(path);
    }

    private List<Cell> generatePathFromHome() {
        boolean init = true;
        Cell[][] grid = new Cell[gridWidth][gridHeight];
        List<Cell> path = new ArrayList<>();

        // Start at center of HOME_ZONE (bottom-left)
        int x = ZONE_SIZE / 2;
        int y = ZONE_SIZE / 2;
        Direction dir = Direction.RIGHT;

        int targetLength = PATH_LENGTH + random.nextInt(15);

        for (int i = 0; i < targetLength; i++) {
            List<Direction> options = getValidDirections(grid, x, y, dir, i);

            // First 4 tiles: only RIGHT or UP
            Direction nextDir;
            if (i < 4) {
                options.removeIf(d -> d != Direction.RIGHT && d != Direction.UP);
                if (i>0){
                    nextDir = dir;
                }
            }

            if (options.isEmpty()) break;

            if (i > targetLength - 10) {
                nextDir = chooseNextDirectionWithOpenness(options, dir, grid, x, y);
            } else {
                nextDir = chooseNextDirection(options, dir);
            }

            // Create cell
            Cell cell;
            if (init) {
                cell = new Cell(Cell.Type.PATH, x * cellSize, y * cellSize, dir);
                init = false;
            } else if (nextDir != dir) {
                cell = new Cell(Cell.Type.TURN, x * cellSize, y * cellSize, dir, nextDir);
            } else {
                cell = new Cell(Cell.Type.PATH, x * cellSize, y * cellSize, dir);
            }

            path.add(cell);
            grid[x][y] = cell;

            x += dx(nextDir);
            y += dy(nextDir);
            dir = nextDir;
        }

        // --- Now route path to top-right END_ZONE ---
        int endX = gridWidth - 1;
        int endY = gridHeight - 1;

        while (!(x == endX && y == endY)) {
            List<Direction> options = new ArrayList<>();
            if (x < endX) options.add(Direction.RIGHT);
            if (x > endX) options.add(Direction.LEFT);
            if (y < endY) options.add(Direction.UP);
            if (y > endY) options.add(Direction.DOWN);

            // Pick a direction that does not enter END_ZONE prematurely
            Direction nextDir = options.get(random.nextInt(options.size()));

            Cell cell;
            if (nextDir != dir) {
                cell = new Cell(Cell.Type.TURN, x * cellSize, y * cellSize, dir, nextDir);
            } else {
                cell = new Cell(Cell.Type.PATH, x * cellSize, y * cellSize, dir);
            }

            path.add(cell);
            grid[x][y] = cell;

            x += dx(nextDir);
            y += dy(nextDir);
            dir = nextDir;
        }

        return path;
    }

    /** Flip all directions in the path so it visually flows backward */
    private List<Cell> flipPath(List<Cell> original) {
        List<Cell> flipped = new ArrayList<>();
        for (int i = original.size() - 1; i >= 0; i--) {
            Cell cell = original.get(i);
            Cell newCell;
            if (cell.type == Cell.Type.TURN) {
                newCell = new Cell(
                    Cell.Type.TURN,
                    cell.x,
                    cell.y,
                    opposite(cell.nextDirection),
                    opposite(cell.direction)
                );
            } else {
                newCell = new Cell(cell.type, cell.x, cell.y, opposite(cell.direction));
            }
            flipped.add(newCell);
        }
        return flipped;
    }

    private Direction opposite(Direction dir) {
        return switch (dir) {
            case UP -> Direction.DOWN;
            case DOWN -> Direction.UP;
            case LEFT -> Direction.RIGHT;
            case RIGHT -> Direction.LEFT;
            default -> dir;
        };
    }

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

    private List<Direction> getValidDirections(Cell[][] grid, int x, int y, Direction current, int pathIndex) {
        List<Direction> candidates = new ArrayList<>();
        Direction[] dirs = {current, turnLeft(current), turnRight(current)};
        for (Direction d : dirs) {
            int nx = x + dx(d);
            int ny = y + dy(d);
            if (!canPlace(nx, ny, grid)) continue;
            if (pathIndex >= 4 && isInHomeZone(nx, ny)) continue;
            if (isInEndZone(nx, ny)) continue; // prevent early entry
            candidates.add(d);
        }
        Collections.shuffle(candidates, random);
        return candidates;
    }

    private boolean isInHomeZone(int x, int y) {
        return x < ZONE_SIZE && y < ZONE_SIZE;
    }

    private boolean isInEndZone(int x, int y) {
        return x >= gridWidth - ZONE_SIZE && y >= gridHeight - ZONE_SIZE;
    }

    private boolean canPlace(int x, int y, Cell[][] grid) {
        if (x < 0 || x >= gridWidth || y < 0 || y >= gridHeight) return false;
        if (grid[x][y] != null) return false;
        int[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
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
        int straightWeight = 70;
        int turnWeight = 15;
        List<Direction> weightedList = new ArrayList<>();
        for (Direction dir : options) {
            if (dir == current) {
                for (int i = 0; i < straightWeight; i++) weightedList.add(dir);
            } else {
                for (int i = 0; i < turnWeight; i++) weightedList.add(dir);
            }
        }
        return weightedList.get(random.nextInt(weightedList.size()));
    }

    private Direction chooseNextDirectionWithOpenness(List<Direction> options, Direction current, Cell[][] grid, int x, int y) {
        Direction bestDir = current;
        int bestScore = -1;
        for (Direction dir : options) {
            int nx = x + dx(dir);
            int ny = y + dy(dir);
            int score = openness(grid, nx, ny);
            if (dir == current) score += 2;
            if (score > bestScore) {
                bestScore = score;
                bestDir = dir;
            }
        }
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
