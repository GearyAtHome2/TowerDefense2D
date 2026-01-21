package com.Geary.towerdefense.entity.mob.navigation;

import com.Geary.towerdefense.Direction;
import com.Geary.towerdefense.entity.world.Cell;

import java.util.List;

public class MobPathNavigator {

    private final List<Cell> path;
    private final int cellSize;
    private final boolean reversed;

    private int pathIndex = -1;
    private int lastPathIndex = -1;
    private boolean reachedEnd = false;

    private Cell current;
    private Cell next;
    private Cell previous;

    public MobPathNavigator(List<Cell> path, int cellSize, boolean reversed) {
        this.path = path;
        this.cellSize = cellSize;
        this.reversed = reversed;

        if (!path.isEmpty()) {
            pathIndex = reversed ? path.size() - 1 : 0;
            refreshNeighbors();
        }
    }

    /* ------------------------------------------------------------------ */
    /*  Core update: call once per tick with mob center position            */
    /* ------------------------------------------------------------------ */

    public void updateFromPosition(float x, float y) {
        if (reachedEnd) return;

        // Fast path: still inside current cell
        if (current != null && current.contains(x, y)) return;

        // Check neighbors only (shortlist)
        if (next != null && next.contains(x, y)) {
            pathIndex += reversed ? -1 : 1;
        } else if (previous != null && previous.contains(x, y)) {
            pathIndex += reversed ? 1 : -1;
        } else {
            // Fallback: scan local area (rare, high velocity / knockback)
            resolveByScan(x, y);
        }

        refreshNeighbors();
    }

    /* ------------------------------------------------------------------ */

    private void refreshNeighbors() {
        if (pathIndex < 0 || pathIndex >= path.size()) {
            reachedEnd = true;
            current = next = previous = null;
            return;
        }

        current = path.get(pathIndex);

        int nextIndex = pathIndex + (reversed ? -1 : 1);
        int prevIndex = pathIndex + (reversed ? 1 : -1);

        next = (nextIndex >= 0 && nextIndex < path.size()) ? path.get(nextIndex) : null;
        previous = (prevIndex >= 0 && prevIndex < path.size()) ? path.get(prevIndex) : null;
    }

    private void resolveByScan(float x, float y) {
        // Extremely rare fallback — correctness over speed
        for (int i = 0; i < path.size(); i++) {
            if (path.get(i).contains(x, y)) {
                pathIndex = i;
                return;
            }
        }
    }

    /* ------------------------------------------------------------------ */
    /*  Query API                                                          */
    /* ------------------------------------------------------------------ */

    public Cell getCurrentCell() {
        return current;
    }

    public Cell getNextCell() {
        return next;
    }

    public Cell getPreviousCell() {
        return previous;
    }

    public boolean hasEnteredNewTile() {
        if (pathIndex != lastPathIndex) {
            lastPathIndex = pathIndex;
            return true;
        }
        return false;
    }

    public boolean reachedEnd() {
        return reachedEnd;
    }

    public int getCellSize() {
        return cellSize;
    }

    public int getPathIndex() {
        return pathIndex;
    }


    public Direction getCurrentDirection() {
        if (current == null) return null;

        if (current.type == Cell.Type.TURN) {
            return reversed ? current.reverseDirection : current.nextDirection;
        }

        return reversed ? current.reverseDirection : current.direction;
    }

    public Direction getLeaveDirectionForCell(Cell cell) {
        if (cell == null) return null;

        if (cell.type == Cell.Type.TURN) {
            return reversed ? cell.reverseDirection : cell.nextDirection;
        }

        return reversed ? cell.reverseDirection : cell.direction;
    }
}
