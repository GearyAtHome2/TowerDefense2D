package com.Geary.towerdefense.behaviour.buildings.manager;

import com.Geary.towerdefense.Direction;
import com.Geary.towerdefense.UI.displays.UIClickManager;
import com.Geary.towerdefense.entity.buildings.Mine;
import com.Geary.towerdefense.entity.resources.Resource;
import com.Geary.towerdefense.entity.world.Cell;
import com.Geary.towerdefense.world.GameWorld;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;

import java.util.EnumMap;
import java.util.EnumSet;

public class MineManager {
    private final GameWorld world;
    private final OrthographicCamera camera;
    private boolean minePlacementButtonActive = false;
    private boolean minePlacementKbActive = false;

    public MineManager(GameWorld world, OrthographicCamera camera) {
        this.world = world;
        this.camera = camera;
    }

    public void setPlacementKeyboardActive(boolean isActive){
        minePlacementKbActive = isActive;
    }

    public boolean isPlacementActive() {
        return minePlacementButtonActive || minePlacementKbActive;
    }

    public void togglePlacementClick(Vector3 uiClick, float buttonX, float buttonY, float buttonW, float buttonH) {
        if (uiClick.x >= buttonX && uiClick.x <= buttonX + buttonW &&
            uiClick.y >= buttonY && uiClick.y <= buttonY + buttonH) {
            minePlacementButtonActive = !minePlacementButtonActive;
        }
    }

    public boolean handlePlacement() {
        if (!isPlacementActive()) {
            world.ghostMine = null;
            return false;
        }

        float screenX = Gdx.input.getX();
        float screenY = Gdx.input.getY();
        if (!UIClickManager.isClickInGameArea(screenY)) {
            world.ghostTower = null;
            return false;
        }
        Vector3 worldPos = new Vector3(screenX, screenY, 0);
        camera.unproject(worldPos);

        int x = (int) (worldPos.x / world.cellSize);
        int y = (int) (worldPos.y / world.cellSize);

        // Check bounds
        if (x < 0 || x >= world.gridWidth || y < 0 || y >= world.gridHeight) {
            world.ghostMine = null;
            return false;
        }

        Cell cell = world.grid[x][y];
        boolean canPlace = cell.resource != null && cell.building == null;

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && canPlace) {
            Mine mine = new Mine(x * GameWorld.cellSize, y * GameWorld.cellSize, cell.resource);
            world.mines.add(mine);
            world.grid[x][y].building = mine;
            world.occupied[x][y] = true;//todo: maybe not this? Would like to be able to override transports in future
            world.ghostMine = null;
            return true;
        } else if (canPlace) {
            if (world.ghostMine == null) {
                world.ghostMine = new Mine(x * GameWorld.cellSize, y * GameWorld.cellSize);
            } else {
                world.ghostMine.xPos = x * GameWorld.cellSize;
                world.ghostMine.yPos = y * GameWorld.cellSize;
            }
        } else {
            world.ghostMine = null;
        }
        return false;
    }

    public EnumMap<Resource.RawResourceType, Float> calculateResourcesGeneratedForMines(float delta){
        EnumMap<Resource.RawResourceType, Float> generatedResource = new EnumMap<>(Resource.RawResourceType.class);

        for (Mine mine : world.mines){
            if (mine.isConnectedToNetwork) {
                Resource.RawResourceType type = mine.resource.type;
                float quantity = mine.resource.resourceAbundance * delta;
                generatedResource.put(type, generatedResource.getOrDefault(type, 0f) + quantity);
            }
        }
        return generatedResource;
    }

    //keeping this in case we decide to add a UI for it
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

    public void animateMines(float delta){
        for (Mine mine : world.mines){
            mine.updateAnimationState(delta);
        }
    }

    public void deleteMine(Mine mine){
        if (mine == null) return;

        int x = (int) mine.xPos / world.cellSize;
        int y = (int) mine.yPos / world.cellSize;

        world.mines.remove(mine);

        if (x >= 0 && x < world.gridWidth && y >= 0 && y < world.gridHeight) {
            if (world.grid[x][y].building == mine) {
                world.grid[x][y].building = null;
            }
            world.occupied[x][y] = false;
        }
    }
}
