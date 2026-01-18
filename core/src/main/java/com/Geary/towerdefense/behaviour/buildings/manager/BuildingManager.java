package com.Geary.towerdefense.behaviour.buildings.manager;

import com.Geary.towerdefense.Direction;
import com.Geary.towerdefense.UI.gameUI.GameUI;
import com.Geary.towerdefense.entity.buildings.Building;
import com.Geary.towerdefense.entity.world.Cell;
import com.Geary.towerdefense.world.GameWorld;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;

import java.util.EnumSet;
import java.util.List;

public abstract class BuildingManager<T extends Building> {

    protected final GameWorld world;
    protected final OrthographicCamera camera;
    private static BuildingManager activePlacementManager;
    private static boolean keyboardOverrideActive;
    protected static boolean placementButtonActive;
    protected boolean keyboardPlacementRequested;

    protected BuildingManager(GameWorld world, OrthographicCamera camera) {
        this.world = world;
        this.camera = camera;
    }

    public void requestKeyboardPlacement() {
        keyboardPlacementRequested = true;
    }

    public boolean isPlacementActive() {
        return isThisActiveManager();
    }

    public static void clearActivePlacement() {
        if (activePlacementManager != null) {
            activePlacementManager.onPlacementDeactivated();
            activePlacementManager = null;
        }
    }

    public static void setActivePlacement(BuildingManager manager) {
        if (activePlacementManager == manager) return;
        clearActivePlacement();
        activePlacementManager = manager;
        manager.onPlacementActivated();
    }

    public static boolean isAnyPlacementActive() {
        return activePlacementManager != null;
    }

    public boolean isThisActiveManager() {
        return activePlacementManager == this;
    }

    // Hooks for subclasses
    protected void onPlacementActivated() {
    }

    protected void onPlacementDeactivated() {
    }


    public void togglePlacementClick(
        Vector3 uiClick,
        float x, float y, float width, float height
    ) {
        if (uiClick.x >= x && uiClick.x <= x + width &&
            uiClick.y >= y && uiClick.y <= y + height) {
            placementButtonActive = !placementButtonActive;
        }
    }

    public boolean handlePlacement() {
        Vector3 worldPos = getWorldMousePosition();
        if (!isThisActiveManager() || worldPos == null) {
            resetGhost();
            return false;
        }


        int gridX = toGrid(worldPos.x);
        int gridY = toGrid(worldPos.y);

        if (!inBounds(gridX, gridY)) {
            resetGhost();
            return false;
        }

        Cell cell = world.grid[gridX][gridY];
        boolean canPlace = canPlaceAt(cell, gridX, gridY);

        if (canPlace) {
            updateGhost(cell, gridX, gridY);
        } else {
            resetGhost();
        }

        boolean shouldPlace =
            Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)
                || keyboardPlacementRequested;

        if (shouldPlace && canPlace) {
            handleLeftClick(cell, gridX, gridY);
            return true;
        }
        keyboardPlacementRequested = false;
        return false;
    }

    protected Vector3 getWorldMousePosition() {
        float screenX = Gdx.input.getX();
        float screenY = Gdx.graphics.getHeight() - Gdx.input.getY();

        // Reject placement if mouse is over the UI bar
        if (screenY < GameUI.UI_BAR_HEIGHT) {
            return null;
        }

        Vector3 worldPos = new Vector3(screenX, Gdx.input.getY(), 0);
        camera.unproject(worldPos);
        return worldPos;
    }

    protected int toGrid(float worldCoord) {
        return (int) (worldCoord / world.cellSize);
    }

    protected boolean inBounds(int x, int y) {
        return x >= 0 && x < world.gridWidth
            && y >= 0 && y < world.gridHeight;
    }

    protected abstract boolean canPlaceAt(Cell cell, int x, int y);

    protected abstract void handleLeftClick(Cell cell, int x, int y);

    protected abstract void updateGhost(Cell cell, int x, int y);

    protected abstract void resetGhost();

    public void deleteBuilding(T building, List<T> buildings) {
        if (building == null) return;

        buildings.remove(building);

        int x = toGrid(building.xPos);
        int y = toGrid(building.yPos);

        if (inBounds(x, y) && world.grid[x][y].building == building) {
            world.grid[x][y].building = null;
            world.occupied[x][y] = false;
        }
    }

    protected EnumSet<Direction> getAdjacentOccupiedTiles(int x, int y) {
        EnumSet<Direction> adjacent = EnumSet.noneOf(Direction.class);

        if (x > 0 && world.occupied[x - 1][y]) adjacent.add(Direction.LEFT);
        if (x < world.gridWidth - 1 && world.occupied[x + 1][y]) adjacent.add(Direction.RIGHT);
        if (y > 0 && world.occupied[x][y - 1]) adjacent.add(Direction.DOWN);
        if (y < world.gridHeight - 1 && world.occupied[x][y + 1]) adjacent.add(Direction.UP);

        return adjacent;
    }
}
