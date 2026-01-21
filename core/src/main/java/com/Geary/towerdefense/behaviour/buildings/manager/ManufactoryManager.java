package com.Geary.towerdefense.behaviour.buildings.manager;

import com.Geary.towerdefense.entity.buildings.factory.BasicArmourFactory;
import com.Geary.towerdefense.entity.buildings.factory.BasicMunitionsFactory;
import com.Geary.towerdefense.entity.buildings.factory.Manufacturing;
import com.Geary.towerdefense.entity.resources.Recipe;
import com.Geary.towerdefense.entity.resources.Resource;
import com.Geary.towerdefense.entity.resources.mapEntity.ResourceType;
import com.Geary.towerdefense.entity.world.Cell;
import com.Geary.towerdefense.world.GameWorld;
import com.badlogic.gdx.graphics.OrthographicCamera;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ManufactoryManager extends BuildingManager<Manufacturing> {

    private Manufacturing activelyPlacing = new BasicMunitionsFactory(0, 0);

    public final List<Manufacturing> allManufacturingTypes;
    public List<Manufacturing> unlockedManufacturingTypes;

    public ManufactoryManager(GameWorld world, OrthographicCamera camera) {
        super(world, camera);
        allManufacturingTypes = List.of(
            new BasicMunitionsFactory(0, 0),
            new BasicArmourFactory(0, 0)
        );
        unlockedManufacturingTypes = allManufacturingTypes;
    }

    @Override
    protected boolean canPlaceAt(Cell cell, int x, int y) {
        return !world.occupied[x][y] &&
            (cell.type == Cell.Type.EMPTY || cell.type == Cell.Type.HOME);
    }

    @Override
    protected void handleLeftClick(Cell cell, int x, int y) {
        if (canPlaceAt(cell, x, y)) {
            Manufacturing manufacturing = activelyPlacing.clone();
            manufacturing.setPosition(x * world.cellSize, y * world.cellSize);

            world.factories.add(manufacturing);
            cell.building = manufacturing;
            world.occupied[x][y] = true;
        }
    }

    @Override
    protected void updateGhost(Cell cell, int x, int y) {
        if (world.ghostManufacturing == null) {
            world.ghostManufacturing = activelyPlacing.clone();
            world.ghostManufacturing.setPosition(x * world.cellSize, y * world.cellSize);
        } else {
            world.ghostManufacturing.setPosition(x * world.cellSize, y * world.cellSize);
        }
    }

    /** Reset ghost factory */
    @Override
    protected void resetGhost() {
        world.ghostManufacturing = null;
    }

    public void setPlacementFactory(Manufacturing manufacturing) {
        activelyPlacing = manufacturing;
        BuildingManager.setActivePlacement(this);
    }

    /** Factory production logic */
    public void handleFactoryProduction(float delta) {
        for (Manufacturing manufacturing : world.factories) {
            Recipe recipe = manufacturing.activeRecipe;
            if (recipe == null || !manufacturing.isConnectedToNetwork) continue;

            boolean enoughResources = true;
            Map<ResourceType, Double> deltaWeightedInputs = new HashMap<>();
            for (Map.Entry<ResourceType, Integer> entry : recipe.inputs.entrySet()) {
                deltaWeightedInputs.put(entry.getKey(), (double) (entry.getValue() * delta));
            }

            for (Map.Entry<ResourceType, Double> entry : deltaWeightedInputs.entrySet()) {
                ResourceType type = entry.getKey();
                double needed = entry.getValue();
                double available = type instanceof Resource.RawResourceType
                    ? world.getGameStateManager().getRawResourceCount().getOrDefault(type, 0.0)
                    : world.getGameStateManager().getRefinedResourceCount().getOrDefault(type, 0.0);
                if (available < needed) {
                    enoughResources = false;
                    break;
                }
            }

            if (enoughResources) {
                for (Map.Entry<ResourceType, Double> entry : deltaWeightedInputs.entrySet()) {
                    world.getGameStateManager().consumeResource(entry.getKey(), entry.getValue());
                }
                for (Map.Entry<ResourceType, Integer> out : recipe.outputs.entrySet()) {
                    world.getGameStateManager().addResource(out.getKey(), out.getValue() * delta);
                }
            }
        }
    }

    /** Animate factories */
    public void animateFactories(float delta) {
        for (Manufacturing f : world.factories) {
            f.updateAnimationState(delta);
        }
    }
}
