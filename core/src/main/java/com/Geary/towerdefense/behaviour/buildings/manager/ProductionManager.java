package com.Geary.towerdefense.behaviour.buildings.manager;

import com.Geary.towerdefense.entity.buildings.production.BasicMine;
import com.Geary.towerdefense.entity.buildings.production.Production;
import com.Geary.towerdefense.entity.resources.Resource;
import com.Geary.towerdefense.entity.world.Cell;
import com.Geary.towerdefense.world.GameWorld;
import com.badlogic.gdx.graphics.OrthographicCamera;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class ProductionManager extends BuildingManager<Production> {

    private Production activelyPlacing = new BasicMine(0, 0);

    public final List<Production> allProductionTypes;
    public List<Production> unlockedProductionTypes;


    public ProductionManager(GameWorld world, OrthographicCamera camera) {
        super(world, camera);
        allProductionTypes = List.of(
            new BasicMine(0, 0)
        );
        unlockedProductionTypes = allProductionTypes;
    }

    @Override
    protected boolean canPlaceAt(Cell cell, int x, int y) {
        return cell.resource != null && cell.building == null;
    }

    @Override
    protected void handleLeftClick(Cell cell, int x, int y) {
        if (canPlaceAt(cell, x, y)) {
            Production production = activelyPlacing.clone();
            production.resource = cell.resource;
            production.setPosition(x * world.cellSize, y * world.cellSize);

            world.productions.add(production);
            cell.building = production;
            world.occupied[x][y] = true;
        }
    }

    @Override
    protected void updateGhost(Cell cell, int x, int y) {
        if (world.ghostProduction == null) {
            world.ghostProduction = activelyPlacing.clone();
            world.ghostProduction.setPosition(x * world.cellSize, y * world.cellSize);
        } else {
            world.ghostProduction.setPosition(x * world.cellSize, y * world.cellSize);
        }
    }

    @Override
    protected void resetGhost() {
        world.ghostProduction = null;
    }

    public void calculateResourcesGenerated(float delta) {
        Map<Resource.RawResourceType, Float> generated = new EnumMap<>(Resource.RawResourceType.class);
        for (Production production : world.productions) {
            if (production.isConnectedToNetwork) {
                float quantity = production.resource.resourceAbundance * delta;
                generated.put(production.resource.type, generated.getOrDefault(production.resource.type, 0f) + quantity);
            }
        }
        world.getGameStateManager().addRawResources(generated);
    }

    public void setPlacementProduction(Production production) {
        activelyPlacing = production;
        BuildingManager.setActivePlacement(this);
    }

    public void animateMines(float delta) {
        for (Production production : world.productions) production.updateAnimationState(delta);
    }
}
