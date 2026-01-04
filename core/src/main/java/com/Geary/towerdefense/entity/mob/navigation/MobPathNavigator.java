package com.Geary.towerdefense.entity.mob.navigation;

import com.Geary.towerdefense.entity.world.Cell;

import java.util.List;

public class MobPathNavigator {

    private final List<Cell> path;
    private final int cellSize;
    private final boolean reversed;

    private int pathIndex;
    private int lastPathIndex = -1;
    private float tileProgress = 0f;
    private boolean reachedEnd = false;

    public MobPathNavigator(List<Cell> path, int cellSize, boolean reversed) {
        this.path = path;
        this.cellSize = cellSize;
        this.reversed = reversed;
        this.pathIndex = reversed ? path.size() - 1 : 0;
    }

    public Cell getCurrentCell() {
        if (pathIndex < 0 || pathIndex >= path.size()) return null;
        return path.get(pathIndex);
    }

    public boolean hasEnteredNewTile() {
        if (pathIndex != lastPathIndex) {
            lastPathIndex = pathIndex;
            tileProgress = 0f;
            return true;
        }
        return false;
    }

    public void updateTileProgress(float progress) {
        tileProgress = progress;
    }

    public void advance() {
        tileProgress = 0f;
        if (reversed) pathIndex--;
        else pathIndex++;

        if (pathIndex < 0 || pathIndex >= path.size()) {
            reachedEnd = true;
        }
    }

    public boolean reachedEnd() {
        return reachedEnd || pathIndex < 0 || pathIndex >= path.size();
    }

    public int getCellSize() {
        return cellSize;
    }

    public int getPathIndex() {
        return pathIndex;
    }

    public float getTileProgress() {
        return tileProgress;
    }

    public void setTileProgress(float progress) {
        this.tileProgress = progress;
    }
}
