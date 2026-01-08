package com.Geary.towerdefense.behaviour.buildings.manager;

import com.Geary.towerdefense.entity.buildings.Mine;
import com.Geary.towerdefense.entity.resources.Resource;
import com.Geary.towerdefense.entity.world.Cell;
import com.Geary.towerdefense.world.GameWorld;
import com.badlogic.gdx.graphics.OrthographicCamera;

import java.util.EnumMap;
import java.util.Map;

public class MineManager extends BuildingManager<Mine> {

    public MineManager(GameWorld world, OrthographicCamera camera) {
        super(world, camera);
    }

    @Override
    protected boolean canPlaceAt(Cell cell, int x, int y) {
        return cell.resource != null && cell.building == null;
    }

    @Override
    protected void handleLeftClick(Cell cell, int x, int y) {
        Mine mine = new Mine(x * world.cellSize, y * world.cellSize, cell.resource);
        world.mines.add(mine);
        cell.building = mine;
        world.occupied[x][y] = true;
    }

    @Override
    protected void updateGhost(Cell cell, int x, int y) {
        if (world.ghostMine == null) world.ghostMine = new Mine(x * world.cellSize, y * world.cellSize);
        else {
            world.ghostMine.xPos = x * world.cellSize;
            world.ghostMine.yPos = y * world.cellSize;
        }
    }

    @Override
    protected void resetGhost() {
        world.ghostMine = null;
    }

    public void calculateResourcesGenerated(float delta) {
        Map<Resource.RawResourceType, Float> generated = new EnumMap<>(Resource.RawResourceType.class);
        for (Mine mine : world.mines) {
            if (mine.isConnectedToNetwork) {
                float quantity = mine.resource.resourceAbundance * delta;
                generated.put(mine.resource.type, generated.getOrDefault(mine.resource.type, 0f) + quantity);
            }
        }
        world.getGameStateManager().addRawResources(generated);
    }

    public void animateMines(float delta) {
        for (Mine mine : world.mines) mine.updateAnimationState(delta);
    }
}
