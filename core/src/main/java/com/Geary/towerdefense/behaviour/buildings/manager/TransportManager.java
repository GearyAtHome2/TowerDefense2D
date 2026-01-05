package com.Geary.towerdefense.behaviour.buildings.manager;

import com.Geary.towerdefense.Direction;
import com.Geary.towerdefense.entity.buildings.*;
import com.Geary.towerdefense.entity.world.Cell;
import com.Geary.towerdefense.world.GameWorld;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;

import java.util.EnumSet;

public class TransportManager {
    private final GameWorld world;
    private final OrthographicCamera camera;
    private boolean transportPlacementButtonActive = false;
    private boolean transportPlacementKbActive = false;

    public TransportManager(GameWorld world, OrthographicCamera camera) {
        this.world = world;
        this.camera = camera;
    }

    public void setPlacementKeyboardActive(boolean isActive){
        transportPlacementKbActive = isActive;
    }

    public boolean isPlacementActive() {
        return transportPlacementButtonActive || transportPlacementKbActive;
    }

    public void togglePlacementClick(Vector3 uiClick, float buttonX, float buttonY, float buttonW, float buttonH) {
        if (uiClick.x >= buttonX && uiClick.x <= buttonX + buttonW &&
            uiClick.y >= buttonY && uiClick.y <= buttonY + buttonH) {
            transportPlacementButtonActive = !transportPlacementButtonActive;
        }
    }

    public void togglePlacementKb(boolean ctrlHeld) {
        transportPlacementButtonActive = ctrlHeld;
    }

    public boolean handlePlacement() {
        if (!isPlacementActive()) {
            world.ghostTransport = null;
            return false;
        }

        Vector3 worldPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(worldPos);
        int x = (int) (worldPos.x / GameWorld.cellSize);
        int y = (int) (worldPos.y / GameWorld.cellSize);

        // Check bounds
        if (x < 0 || x >= world.gridWidth || y < 0 || y >= world.gridHeight) {
            world.ghostTransport = null;
            return false;
        }

        Cell cell = world.grid[x][y];
        Cell.Type type = cell.type;

        boolean bridge = type == Cell.Type.PATH || type == Cell.Type.TURN;

        boolean canPlace = !world.occupied[x][y] && cell.bridgable &&
            (type == Cell.Type.EMPTY || type == Cell.Type.HOME || bridge);

        EnumSet<Direction> adjacentTransports = checkAdjacentTiles(x, y);

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && canPlace) {
            Transport transport = bridge ? new Bridge(x * GameWorld.cellSize, y * GameWorld.cellSize, adjacentTransports) :
                new Transport(x * GameWorld.cellSize, y * GameWorld.cellSize, adjacentTransports);
            world.transports.add(transport);
            cell.building = transport;
            world.occupied[x][y] = true;//todo: maybe not this? Would like to be able to override transports in future
            world.ghostTransport = null; // clear ghost after placement
            return true;
        } else if (canPlace) {
            if (world.ghostTransport == null) {
                world.ghostTransport = new Transport(x * GameWorld.cellSize, y * GameWorld.cellSize, adjacentTransports);
            } else {
                world.ghostTransport.directions = adjacentTransports;
                world.ghostTransport.xPos = x * GameWorld.cellSize;
                world.ghostTransport.yPos = y * GameWorld.cellSize;
            }
        } else {
            world.ghostTransport = null;
        }
        return false;
    }

    public void updateAllTransportLinks() {
        boolean changed;
        do {
            changed = false;
            for (Transport t : world.transports) {
                changed |= updateIfConnected(t);
            }
            for (Tower t : world.towers) {
                changed |= updateIfConnected(t);
            }
            for (Mine m : world.mines) {
                changed |= updateIfConnected(m);
            }

        } while (changed);
    }

    private boolean updateIfConnected(Building b) {
        boolean wasConnected = b.isConnectedToNetwork;
        updateBuildingWithNetworkCheck(b);
        return !wasConnected && b.isConnectedToNetwork;
    }

    public EnumSet<Direction> checkAdjacentTiles(int x, int y) {
        EnumSet<Direction> adjacent = EnumSet.noneOf(Direction.class);
        if (x > 0 && world.occupied[x - 1][y]) {
            adjacent.add(Direction.LEFT);
        }
        if (x < world.gridWidth - 1 && world.occupied[x + 1][y]) {
            adjacent.add(Direction.RIGHT);
        }
        if (y > 0 && world.occupied[x][y - 1]) {
            adjacent.add(Direction.DOWN);
        }
        if (y < world.gridHeight - 1 && world.occupied[x][y + 1]) {
            adjacent.add(Direction.UP);
        }
        return adjacent;
    }

    public void updateBuildingWithNetworkCheck(Building building) {
        int x = (int) (building.xPos / GameWorld.cellSize);
        int y = (int) (building.yPos / GameWorld.cellSize);

        EnumSet<Direction> adjacent = EnumSet.noneOf(Direction.class);

        Cell left = (x > 0) ? world.grid[x - 1][y] : null;
        Cell right = (x < world.gridWidth - 1) ? world.grid[x + 1][y] : null;
        Cell down = (y > 0) ? world.grid[x][y - 1] : null;
        Cell up = (y < world.gridHeight - 1) ? world.grid[x][y + 1] : null;

        if (left != null && left.building != null) {
            adjacent.add(Direction.LEFT);
            if (!building.isConnectedToNetwork && left.building.isConnectedToNetwork) {
                building.isConnectedToNetwork = true;
            }
        }
        if (up != null && up.building != null) {
            adjacent.add(Direction.UP);
            if (!building.isConnectedToNetwork && up.building.isConnectedToNetwork) {
                building.isConnectedToNetwork = true;
            }
        }
        if (right != null && right.building != null) {
            adjacent.add(Direction.RIGHT);
            if (!building.isConnectedToNetwork && right.building.isConnectedToNetwork) {
                building.isConnectedToNetwork = true;
            }
        }
        if (down != null && down.building != null) {
            adjacent.add(Direction.DOWN);
            if (!building.isConnectedToNetwork && down.building.isConnectedToNetwork) {
                building.isConnectedToNetwork = true;
            }
        }

        if (building.getClass().equals(Transport.class) ||
            building.getClass().equals(Bridge.class)) {
            Transport transport = (Transport) building;
            transport.directions = adjacent;
        }
    }

    public void updateTransports(float delta) {
        for (Transport transport : world.transports) {
            transport.updateAnimationState(delta);
            //don't do anything yet - might want an animation at some stage.
        }
    }
}
