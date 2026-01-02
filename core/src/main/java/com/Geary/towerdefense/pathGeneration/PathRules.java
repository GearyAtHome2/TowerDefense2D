package com.Geary.towerdefense.pathGeneration;

import com.Geary.towerdefense.Direction;
import com.Geary.towerdefense.entity.world.Cell;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static com.Geary.towerdefense.pathGeneration.DirectionUtil.*;
import static com.Geary.towerdefense.pathGeneration.ZoneUtil.isInEndZone;
import static com.Geary.towerdefense.pathGeneration.ZoneUtil.isInHomeZone;

public class PathRules {

    private final int gridW;
    private final int gridH;
    private final int zoneSize;
    private final Random random;

    public PathRules(int gridW, int gridH, int zoneSize, Random random) {
        this.gridW = gridW;
        this.gridH = gridH;
        this.zoneSize = zoneSize;
        this.random = random;
    }

    public List<Direction> getValidDirections(
        Cell[][] grid,
        int x,
        int y,
        Direction current,
        int pathIndex,
        boolean leftHomeZone,
        boolean allowEndZone
    ) {
        List<Direction> result = new ArrayList<>();
        Direction[] dirs = {current, turnLeft(current), turnRight(current)};

        for (Direction d : dirs) {
            int nx = x + dx(d);
            int ny = y + dy(d);

            if (nx < 0 || nx >= gridW || ny < 0 || ny >= gridH) continue;
            if (grid[nx][ny] != null) continue;

            if (!allowEndZone && isInEndZone(nx, ny, gridW, gridH, zoneSize)) continue;

            if (!leftHomeZone && pathIndex >= 4 && isInHomeZone(nx, ny, zoneSize)) continue;
            if (leftHomeZone && isInHomeZone(nx, ny, zoneSize)) continue;

            result.add(d);
        }

        Collections.shuffle(result, random);
        return result;
    }

    public Direction weightedDirection(List<Direction> options, Direction current) {
        List<Direction> weighted = new ArrayList<>();
        for (Direction d : options) {
            int weight = (d == current) ? 70 : 15;
            for (int i = 0; i < weight; i++) weighted.add(d);
        }
        return weighted.get(random.nextInt(weighted.size()));
    }

    public Direction opennessDirection(
        List<Direction> options,
        Direction current,
        Cell[][] grid,
        int x,
        int y
    ) {
        Direction best = current;
        int bestScore = -1;

        for (Direction d : options) {
            int score = openness(grid, x + dx(d), y + dy(d));
            if (d == current) score += 2;

            if (score > bestScore) {
                bestScore = score;
                best = d;
            }
        }
        return best;
    }

    private int openness(Cell[][] grid, int x, int y) {
        int count = 0;
        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                int nx = x + dx;
                int ny = y + dy;
                if (nx < 0 || nx >= gridW || ny < 0 || ny >= gridH) continue;
                if (grid[nx][ny] == null) count++;
            }
        }
        return count;
    }

    public Direction directionTowards(
        int x,
        int y,
        int targetX,
        int targetY,
        List<Direction> options
    ) {
        int oldDist = Math.abs(x - targetX) + Math.abs(y - targetY);
        List<Direction> closer = new ArrayList<>();

        for (Direction d : options) {
            int nx = x + dx(d);
            int ny = y + dy(d);
            int newDist = Math.abs(nx - targetX) + Math.abs(ny - targetY);
            if (newDist < oldDist) closer.add(d);
        }

        if (!closer.isEmpty()) {
            return closer.get(random.nextInt(closer.size()));
        }
        return options.get(random.nextInt(options.size()));
    }
}
