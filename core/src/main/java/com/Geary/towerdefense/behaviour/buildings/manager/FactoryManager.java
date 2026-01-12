package com.Geary.towerdefense.behaviour.buildings.manager;

import com.Geary.towerdefense.entity.buildings.Factory;
import com.Geary.towerdefense.entity.resources.Recipe;
import com.Geary.towerdefense.entity.resources.Resource;
import com.Geary.towerdefense.entity.resources.mapEntity.ResourceType;
import com.Geary.towerdefense.entity.world.Cell;
import com.Geary.towerdefense.world.GameWorld;
import com.badlogic.gdx.graphics.OrthographicCamera;

import java.util.HashMap;
import java.util.Map;

public class FactoryManager extends BuildingManager<Factory> {

    public FactoryManager(GameWorld world, OrthographicCamera camera) {
        super(world, camera);
    }

    /** Can place on empty or home cells */
    @Override
    protected boolean canPlaceAt(Cell cell, int x, int y) {
        return !world.occupied[x][y] &&
            (cell.type == Cell.Type.EMPTY || cell.type == Cell.Type.HOME);
    }

    /** Left click: place factory at cell */
    @Override
    protected void handleLeftClick(Cell cell, int x, int y) {
        if (canPlaceAt(cell, x, y)) {
            Factory f = new Factory(
                x * world.cellSize + world.cellSize / 2f,
                y * world.cellSize + world.cellSize / 2f
            );
            world.factories.add(f);
            cell.building = f;
            world.occupied[x][y] = true;
        }
    }

    /** Update ghost factory position to follow mouse */
    @Override
    protected void updateGhost(Cell cell, int x, int y) {
        if (world.ghostFactory == null) {
            world.ghostFactory = new Factory(
                x * world.cellSize + world.cellSize / 2f,
                y * world.cellSize + world.cellSize / 2f
            );
        } else {
            world.ghostFactory.xPos = x * world.cellSize + world.cellSize / 2f;
            world.ghostFactory.yPos = y * world.cellSize + world.cellSize / 2f;
        }
    }

    /** Reset ghost factory */
    @Override
    protected void resetGhost() {
        world.ghostFactory = null;
    }

    /** Factory production logic */
    public void handleFactoryProduction(float delta) {
        for (Factory factory : world.factories) {
            Recipe recipe = factory.activeRecipe;
            if (recipe == null) continue;

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
        for (Factory f : world.factories) {
            f.updateAnimationState(delta);
        }
    }
}
