package com.Geary.towerdefense.entity.world;

import com.Geary.towerdefense.Direction;

import java.util.*;

public class PathGenerator {

    private final int gridWidth;
    private final int gridHeight;
    private final int cellSize;
    private final Random random = new Random();
    private static final int PATH_LENGTH = 80;
    private static final int ZONE_SIZE = 7;
    private static boolean correctEnd;

    public PathGenerator(int gridWidth, int gridHeight, int cellSize) {
        this.gridWidth = gridWidth;
        this.gridHeight = gridHeight;
        this.cellSize = cellSize;
    }

    public List<Cell> generatePathAttempts() {
        int len = 0;
        int index = 0;
        List<Cell> path = new ArrayList<>();
        while (index < 2500 && (len < PATH_LENGTH || !correctEnd)) {
            System.out.println("attempt: "+index+", previous path lenght: "+len+", previous correct end: "+correctEnd);
            path = generatePathFromHome();
            len = path.size();
            index++;
        }
        System.out.println("SUCCESS! attempt: "+index+", path lenght: "+len+", correct end: "+correctEnd);
        // Flip directions so path visually flows from ENEMY_ZONE → HOME_ZONE
        return flipPath(path);
    }

    private List<Cell> generatePathFromHome() {
        correctEnd = false;
        boolean init = true;
        boolean leftHomeZone = false; // <-- track leaving HOME_ZONE
        Cell[][] grid = new Cell[gridWidth][gridHeight];
        List<Cell> path = new ArrayList<>();

        int x = ZONE_SIZE / 2;
        int y = ZONE_SIZE / 2;
        Direction dir = Direction.RIGHT;

        int forcedEndX = gridWidth - 1;
        int forcedEndY = gridHeight - 1;

        int maxInitialSteps = PATH_LENGTH - manhattanDistance(x, y, forcedEndX, forcedEndY);

        for (int i = 0; i < maxInitialSteps; i++) {
            List<Direction> options = getValidDirections(grid, x, y, dir, i, false, leftHomeZone);

            // First 4 tiles: only RIGHT or UP
            if (i < 4) {
                options.removeIf(d -> d != Direction.RIGHT && d != Direction.UP);
            }

            if (options.isEmpty()) break;

            Direction nextDir;
            if (i > maxInitialSteps - 10) {
                nextDir = chooseNextDirectionWithOpenness(options, dir, grid, x, y);
            } else {
                nextDir = chooseNextDirection(options, dir);
            }

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

            // Check if we have left HOME_ZONE
            if (!leftHomeZone && !isInHomeZone(x, y)) leftHomeZone = true;

            x += dx(nextDir);
            y += dy(nextDir);
            dir = nextDir;
        }

        // --- Forcefully route to END_ZONE (top-right) ---
        while (x != forcedEndX || y != forcedEndY) {
            List<Direction> options = new ArrayList<>();
            for (Direction d : new Direction[]{dir, turnLeft(dir), turnRight(dir)}) {
                int nx = x + dx(d);
                int ny = y + dy(d);

                if (nx < 0 || nx >= gridWidth || ny < 0 || ny >= gridHeight) continue;
                if (grid[nx][ny] != null) continue;

                // Never re-enter HOME_ZONE once we've left
                if (leftHomeZone && isInHomeZone(nx, ny)) continue;

                options.add(d);
            }

            if (options.isEmpty()) break;

            Direction nextDir = directionTowards(x, y, forcedEndX, forcedEndY, options);

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

        if (x == forcedEndX && y == forcedEndY) correctEnd = true;

        return path;
    }

    private int manhattanDistance(int x1, int y1, int x2, int y2) {
        return Math.abs(x2 - x1) + Math.abs(y2 - y1);
    }

    /** Flip all directions in the path so it visually flows backward */
    private List<Cell> flipPath(List<Cell> original) {
        List<Cell> flipped = new ArrayList<>();
        for (int i = original.size() - 1; i >= 0; i--) {
            Cell cell = original.get(i);
            Cell newCell;
            if (cell.type == Cell.Type.TURN) {
                newCell = new Cell(Cell.Type.TURN,
                    cell.x,
                    cell.y,
                    opposite(cell.nextDirection),
                    opposite(cell.direction));
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

    private List<Direction> getValidDirections(Cell[][] grid, int x, int y, Direction current, int pathIndex, boolean allowEndZone, boolean leftHomeZone) {
        List<Direction> candidates = new ArrayList<>();
        Direction[] dirs = {current, turnLeft(current), turnRight(current)};
        for (Direction d : dirs) {
            int nx = x + dx(d);
            int ny = y + dy(d);
            if (nx < 0 || nx >= gridWidth || ny < 0 || ny >= gridHeight) continue;
            if (!allowEndZone && isInEndZone(nx, ny)) continue;
            if (!leftHomeZone && pathIndex >= 4 && isInHomeZone(nx, ny)) continue;
            if (leftHomeZone && isInHomeZone(nx, ny)) continue; // <-- forbid re-entry
            if (grid[nx][ny] != null) continue;
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

    private Direction directionTowards(int x, int y, int targetX, int targetY, List<Direction> options) {
        List<Direction> valid = new ArrayList<>();
        int oldDist = Math.abs(x - targetX) + Math.abs(y - targetY);

        for (Direction d : options) {
            int nx = x + dx(d);
            int ny = y + dy(d);
            int newDist = Math.abs(nx - targetX) + Math.abs(ny - targetY);
            if (newDist < oldDist) valid.add(d);
        }

        if (!valid.isEmpty()) return valid.get(random.nextInt(valid.size()));
        return options.get(random.nextInt(options.size()));
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
