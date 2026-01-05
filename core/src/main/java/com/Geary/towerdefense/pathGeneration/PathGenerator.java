package com.Geary.towerdefense.pathGeneration;

import com.Geary.towerdefense.Direction;
import com.Geary.towerdefense.entity.world.Cell;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.Geary.towerdefense.pathGeneration.DirectionUtil.*;
import static com.Geary.towerdefense.pathGeneration.ZoneUtil.isInHomeZone;

public class PathGenerator {

    private static final int PATH_LENGTH = 85;
    private static final int ZONE_SIZE = 7;

    private final int gridW;
    private final int gridH;
    private final int cellSize;
    private final Random random;
    private final PathRules rules;

    private boolean correctEnd;

    public PathGenerator(int gridW, int gridH, int cellSize) {
        this.random = new Random();
        this.gridW = gridW;
        this.gridH = gridH;
        this.cellSize = cellSize;
        this.rules = new PathRules(gridW, gridH, ZONE_SIZE, random);
    }

    public List<Cell> generatePathAttempts() {
        long startTimeNs = System.nanoTime();
        int attempts = 0;

        for (int i = 0; i < 2500; i++, attempts++) {
            List<Cell> path = generatePathFromHome();

            if (path.size() >= PATH_LENGTH && correctEnd) {
                logTiming(startTimeNs, System.nanoTime(), attempts + 1, true);
                System.out.println("Success! Attempt " + i +
                    ": length: " + path.size() + ", end: " + correctEnd);
                return flipPath(path);
            }

            System.out.println("Attempt " + i + ": length: " + path.size() + ", end: " + correctEnd);
        }

        logTiming(startTimeNs, System.nanoTime(), attempts, false);
        return List.of();
    }

    private List<Cell> generatePathFromHome() {
        correctEnd = false;
        boolean init = true;
        boolean leftHomeZone = false;

        Cell[][] grid = new Cell[gridW][gridH];
        List<Cell> path = new ArrayList<>(PATH_LENGTH);

        Cursor c = new Cursor(ZONE_SIZE / 2, ZONE_SIZE / 2, Direction.RIGHT);
        int endX = gridW - 1, endY = gridH - 1;

        // Generate main path until end zone or maximum PATH_LENGTH
        for (int step = 0; step < 1000; step++) { // high upper bound, break early if needed
            List<Direction> options = rules.getValidDirections(grid, c.x, c.y, c.dir, step, leftHomeZone, false);
            if (options.isEmpty()) break;

            Direction nextDir = !leftHomeZone ? Direction.RIGHT
                : (step > PATH_LENGTH - 10
                ? rules.opennessDirection(options, c.dir, grid, c.x, c.y)
                : rules.weightedDirection(options, c.dir));

            if (nextDir == null) break;

            Cell cell = (init || nextDir == c.dir)
                ? new Cell(Cell.Type.PATH, c.x * cellSize, c.y * cellSize, c.dir)
                : new Cell(Cell.Type.TURN, c.x * cellSize, c.y * cellSize, c.dir, nextDir);

            //randomly decide if the path cell can have a bridge built on it - make it hard to simply bridge over all cells.

            init = false;
            path.add(cell);
            grid[c.x][c.y] = cell;

            c.move(nextDir);

            if (!leftHomeZone && !isInHomeZone(c.x, c.y, ZONE_SIZE)) leftHomeZone = true;

            if (c.x == endX && c.y == endY) {
                correctEnd = true;
                break;
            }
        }

        routeToEndZone(grid, path, c, endX, endY);
        if (c.x == endX && c.y == endY) correctEnd = true;

        return path;
    }

    private void routeToEndZone(Cell[][] grid, List<Cell> path, Cursor c, int endX, int endY) {
        while (c.x != endX || c.y != endY) {
            List<Direction> options = rules.getValidDirections(grid, c.x, c.y, c.dir, PATH_LENGTH, true, true);
            if (options.isEmpty()) return;

            Direction nextDir = rules.directionTowards(c.x, c.y, endX, endY, options);

            Cell cell = (nextDir != c.dir)
                ? new Cell(Cell.Type.TURN, c.x * cellSize, c.y * cellSize, c.dir, nextDir)
                : new Cell(Cell.Type.PATH, c.x * cellSize, c.y * cellSize, c.dir);
            path.add(cell);
            grid[c.x][c.y] = cell;
            c.move(nextDir);
        }
    }

    private List<Cell> flipPath(List<Cell> original) {
        List<Cell> flipped = new ArrayList<>(original.size());
        for (int i = original.size() - 1; i >= 0; i--) {
            Cell c = original.get(i);
            Cell flippedCell = c.type == Cell.Type.TURN
                ? new Cell(Cell.Type.TURN, c.x, c.y, opposite(c.nextDirection), opposite(c.direction))
                : new Cell(c.type, c.x, c.y, opposite(c.direction));
            flippedCell.bridgable = random.nextDouble() > 0.5;
            flipped.add(flippedCell);
        }
        return flipped;
    }

    private void logTiming(long startNs, long endNs, int attempts, boolean success) {
        double totalMs = (endNs - startNs) / 1_000_000.0;
        double avgMs = totalMs / Math.max(attempts, 1);

        System.out.println("---- Path generation stats ----");
        System.out.println("Success: " + success);
        System.out.println("Attempts: " + attempts);
        System.out.printf("Total time: %.3f ms%n", totalMs);
        System.out.printf("Avg per attempt: %.5f ms%n", avgMs);
        System.out.println("--------------------------------");
    }

    private static final class Cursor {
        int x, y;
        Direction dir;
        Cursor(int x, int y, Direction dir) { this.x = x; this.y = y; this.dir = dir; }
        void move(Direction d) { x += dx(d); y += dy(d); dir = d; }
    }
}
