package com.Geary.towerdefense.behaviour.buildings.manager;

import com.Geary.towerdefense.Direction;
import com.Geary.towerdefense.entity.buildings.*;
import com.Geary.towerdefense.entity.world.Cell;
import com.Geary.towerdefense.world.GameWorld;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;

import java.util.*;

public class TransportManager extends BuildingManager<Transport> {
    private boolean isDragging = false;
    private int dragStartX = -1, dragStartY = -1;
    private final Set<Cell> draggedCells = new HashSet<>();

    public TransportManager(GameWorld world, OrthographicCamera camera) {
        super(world, camera);
    }

    @Override
    public void togglePlacementClick(Vector3 uiClick, float buttonX, float buttonY, float buttonW, float buttonH) {
        placementButtonActive = !placementButtonActive;
    }

    @Override
    protected boolean canPlaceAt(Cell cell, int x, int y) {
        boolean bridge = cell.type == Cell.Type.PATH || cell.type == Cell.Type.TURN;
        return !world.occupied[x][y] && cell.bridgable &&
            (cell.type == Cell.Type.EMPTY || cell.type == Cell.Type.HOME || bridge);
    }

    @Override
    protected void handleLeftClick(Cell cell, int x, int y) {
        boolean bridge = cell.type == Cell.Type.PATH || cell.type == Cell.Type.TURN;

        Transport transport = bridge ?
            new Bridge(x * world.cellSize, y * world.cellSize, getAdjacentOccupiedTiles(x, y)) :
            new Transport(x * world.cellSize, y * world.cellSize, getAdjacentOccupiedTiles(x, y));

        world.transports.add(transport);
        cell.building = transport;
        world.occupied[x][y] = true;
        draggedCells.add(cell);
    }

    @Override
    protected void updateGhost(Cell cell, int x, int y) {
        EnumSet<Direction> adjacent = getAdjacentOccupiedTiles(x, y);

        if (world.ghostTransport == null)
            world.ghostTransport = new Transport(x * world.cellSize, y * world.cellSize, adjacent);
        else {
            world.ghostTransport.directions = adjacent;
            world.ghostTransport.xPos = x * world.cellSize + world.cellSize / 2f;
            world.ghostTransport.yPos = y * world.cellSize + world.cellSize / 2f;
        }
    }

    @Override
    protected void resetGhost() {
        world.ghostTransport = null;
        isDragging = false;
        draggedCells.clear();
    }

    @Override
    public boolean handlePlacement() {
        Vector3 worldPos = getWorldMousePosition();

        if (!isPlacementActive() || worldPos == null) {
            resetGhost();
            return false;
        }
        int x = (int) (worldPos.x / world.cellSize);
        int y = (int) (worldPos.y / world.cellSize);

        if (!inBounds(x, y)) {
            resetGhost();
            return false;
        }

        Cell cell = world.grid[x][y];
        boolean canPlace = canPlaceAt(cell, x, y);

        // update ghost every frame
        if (canPlace) updateGhost(cell, x, y);
        else resetGhost();

        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)) {
            if (!isDragging) {
                isDragging = true;
                draggedCells.clear();
            }

            if (canPlace && !draggedCells.contains(cell)) {
                handleLeftClick(cell, x, y);
            }
            return true; // handled placement this frame
        } else {
            isDragging = false;
            draggedCells.clear();
        }

        return false;
    }

    public void updateAllTransportLinks() {
        List<Building> allBuildings = new ArrayList<>();
        allBuildings.addAll(world.transports);
        allBuildings.addAll(world.towers);
        allBuildings.addAll(world.mines);
        allBuildings.addAll(world.factories);

        for (Building b : allBuildings)
            b.isConnectedToNetwork = false;

        boolean changed;
        do {
            changed = false;
            for (Building b : allBuildings)
                changed |= updateIfConnected(b);
        } while (changed);

    }

    private boolean updateIfConnected(Building b) {
        boolean wasConnected = b.isConnectedToNetwork;
        updateBuildingWithNetworkCheck(b);
        return !wasConnected && b.isConnectedToNetwork;
    }

    public void updateBuildingWithNetworkCheck(Building building) {
        int x = (int) (building.xPos / world.cellSize);
        int y = (int) (building.yPos / world.cellSize);

        EnumSet<Direction> adjacent = EnumSet.noneOf(Direction.class);

        Cell left = (x > 0) ? world.grid[x - 1][y] : null;
        Cell right = (x < world.gridWidth - 1) ? world.grid[x + 1][y] : null;
        Cell down = (y > 0) ? world.grid[x][y - 1] : null;
        Cell up = (y < world.gridHeight - 1) ? world.grid[x][y + 1] : null;

        if (left != null && left.building != null) {
            adjacent.add(Direction.LEFT);
            if (!building.isConnectedToNetwork && left.building.isConnectedToNetwork)
                building.isConnectedToNetwork = true;
        }
        if (up != null && up.building != null) {
            adjacent.add(Direction.UP);
            if (!building.isConnectedToNetwork && up.building.isConnectedToNetwork)
                building.isConnectedToNetwork = true;
        }
        if (right != null && right.building != null) {
            adjacent.add(Direction.RIGHT);
            if (!building.isConnectedToNetwork && right.building.isConnectedToNetwork)
                building.isConnectedToNetwork = true;
        }
        if (down != null && down.building != null) {
            adjacent.add(Direction.DOWN);
            if (!building.isConnectedToNetwork && down.building.isConnectedToNetwork)
                building.isConnectedToNetwork = true;
        }

        if (building instanceof Transport transport) {
            transport.directions = adjacent;
        }
    }

    public void updateTransports(float delta) {
        for (Transport transport : world.transports) transport.updateAnimationState(delta);
    }

    public void deleteTransport(Transport transport) {
        deleteBuilding(transport, world.transports);
    }
}
