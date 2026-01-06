package com.Geary.towerdefense.behaviour.buildings.manager;

import com.Geary.towerdefense.Direction;
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
    protected boolean placementButtonActive = false;
    protected boolean placementKbActive = false;

    public BuildingManager(GameWorld world, OrthographicCamera camera) {
        this.world = world;
        this.camera = camera;
    }

    public void setPlacementKeyboardActive(boolean isActive) {
        placementKbActive = isActive;
    }

    public boolean isPlacementActive() {
        return placementButtonActive || placementKbActive;
    }

    public void togglePlacementClick(Vector3 uiClick, float buttonX, float buttonY, float buttonW, float buttonH) {
        if (uiClick.x >= buttonX && uiClick.x <= buttonX + buttonW &&
            uiClick.y >= buttonY && uiClick.y <= buttonY + buttonH) {
            placementButtonActive = !placementButtonActive;
        }
    }

    public boolean handlePlacement() {
        if (!isPlacementActive()) {
            resetGhost();
            return false;
        }

        Vector3 worldPos = getWorldMousePosition();
        int x = (int) (worldPos.x / world.cellSize);
        int y = (int) (worldPos.y / world.cellSize);

        if (!inBounds(x, y)) {
            resetGhost();
            return false;
        }

        Cell cell = world.grid[x][y];
        boolean canPlace = canPlaceAt(cell, x, y);

        // Always update ghost
        if (canPlace) updateGhost(cell, x, y);
        else resetGhost();

        // Only place on actual left-click
        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && canPlace) {
            handleLeftClick(cell, x, y);
            return true;
        }

        return false;
    }

    protected Vector3 getWorldMousePosition() {
        Vector3 pos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(pos);
        return pos;
    }

    protected boolean inBounds(int x, int y) {
        return x >= 0 && x < world.gridWidth && y >= 0 && y < world.gridHeight;
    }

    protected abstract boolean canPlaceAt(Cell cell, int x, int y);
    protected abstract void handleLeftClick(Cell cell, int x, int y);
    protected abstract void updateGhost(Cell cell, int x, int y);
    protected abstract void resetGhost();

    public void deleteBuilding(T building, List<T> buildingList) {
        if (building == null) return;

        int x = (int) building.xPos / world.cellSize;
        int y = (int) building.yPos / world.cellSize;

        buildingList.remove(building);

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
