package com.Geary.towerdefense.entity.world;

import com.Geary.towerdefense.Direction;
import com.Geary.towerdefense.pathGeneration.PathRules;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.Geary.towerdefense.pathGeneration.DirectionUtil.*;
import static com.Geary.towerdefense.pathGeneration.ZoneUtil.isInHomeZone;

public class PathGenerator {

    private static final int PATH_LENGTH = 80;
    private static final int ZONE_SIZE = 7;

    private final int gridW;
    private final int gridH;
    private final int cellSize;
    private final Random random = new Random();
    private final PathRules rules;

    private boolean correctEnd;

    public PathGenerator(int gridW, int gridH, int cellSize) {
        this.gridW = gridW;
        this.gridH = gridH;
        this.cellSize = cellSize;
        this.rules = new PathRules(gridW, gridH, ZONE_SIZE, random);
    }

    public List<Cell> generatePathAttempts() {
        for (int i = 0; i < 2500; i++) {
            List<Cell> path = generatePathFromHome();
            if (path.size() >= PATH_LENGTH && correctEnd) {
                return flipPath(path);
            }
        }
        return List.of();
    }

    private List<Cell> generatePathFromHome() {
        correctEnd = false;
        boolean init = true;
        boolean leftHomeZone = false;

        Cell[][] grid = new Cell[gridW][gridH];
        List<Cell> path = new ArrayList<>();

        Cursor c = new Cursor(ZONE_SIZE / 2, ZONE_SIZE / 2, Direction.RIGHT);
        Direction exitDirection = null;

        int endX = gridW - 1;
        int endY = gridH - 1;

        int maxInitialSteps =
            PATH_LENGTH - Math.abs(endX - c.x) - Math.abs(endY - c.y);

        for (int i = 0; i < maxInitialSteps; i++) {
            List<Direction> options =
                rules.getValidDirections(grid, c.x, c.y, c.dir, i, leftHomeZone, false);

            if (options.isEmpty()) break;

            Direction nextDir;

            if (!leftHomeZone) {
                nextDir = chooseHomeExitDirection(options, exitDirection);
                if (nextDir == null) break;
                exitDirection = nextDir;
            } else {
                nextDir = chooseNormalDirection(
                    options, c.dir, grid, c.x, c.y, i, maxInitialSteps
                );
            }

            Cell cell = init
                ? new Cell(Cell.Type.PATH, c.x * cellSize, c.y * cellSize, c.dir)
                : (nextDir != c.dir)
                ? new Cell(Cell.Type.TURN, c.x * cellSize, c.y * cellSize, c.dir, nextDir)
                : new Cell(Cell.Type.PATH, c.x * cellSize, c.y * cellSize, c.dir);

            init = false;
            path.add(cell);
            grid[c.x][c.y] = cell;

            if (!leftHomeZone &&
                !isInHomeZone(c.x + dx(nextDir), c.y + dy(nextDir), ZONE_SIZE)) {
                leftHomeZone = true;
            }

            c.move(nextDir);
        }

        routeToEndZone(grid, path, c, endX, endY);

        if (c.x == endX && c.y == endY) correctEnd = true;

        return path;
    }

    private List<Cell> flipPath(List<Cell> original) {
        List<Cell> flipped = new ArrayList<>();
        for (int i = original.size() - 1; i >= 0; i--) {
            Cell c = original.get(i);
            flipped.add(
                c.type == Cell.Type.TURN
                    ? new Cell(Cell.Type.TURN, c.x, c.y,
                    opposite(c.nextDirection), opposite(c.direction))
                    : new Cell(c.type, c.x, c.y, opposite(c.direction))
            );
        }
        return flipped;
    }

    private Direction chooseHomeExitDirection(
        List<Direction> options,
        Direction exitDirection
    ) {
        if (exitDirection != null) {
            return exitDirection;
        }

        options.removeIf(d -> d != Direction.RIGHT && d != Direction.UP);
        if (options.isEmpty()) return null;

        return options.get(random.nextInt(options.size()));
    }

    private Direction chooseNormalDirection(
        List<Direction> options,
        Direction current,
        Cell[][] grid,
        int x,
        int y,
        int step,
        int maxInitialSteps
    ) {
        return (step > maxInitialSteps - 10)
            ? rules.opennessDirection(options, current, grid, x, y)
            : rules.weightedDirection(options, current);
    }

    private void routeToEndZone(
        Cell[][] grid,
        List<Cell> path,
        Cursor c,
        int endX,
        int endY
    ) {
        while (c.x != endX || c.y != endY) {
            List<Direction> options = rules.getValidDirections(
                grid, c.x, c.y, c.dir, PATH_LENGTH, true, true
            );

            if (options.isEmpty()) return;

            Direction nextDir =
                rules.directionTowards(c.x, c.y, endX, endY, options);

            Cell cell = (nextDir != c.dir)
                ? new Cell(Cell.Type.TURN, c.x * cellSize, c.y * cellSize, c.dir, nextDir)
                : new Cell(Cell.Type.PATH, c.x * cellSize, c.y * cellSize, c.dir);

            path.add(cell);
            grid[c.x][c.y] = cell;

            c.move(nextDir);
        }
    }


    private static final class Cursor {
        int x, y;
        Direction dir;

        Cursor(int x, int y, Direction dir) {
            this.x = x;
            this.y = y;
            this.dir = dir;
        }

        void move(Direction next) {
            x += dx(next);
            y += dy(next);
            dir = next;
        }
    }
}
