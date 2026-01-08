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

    @Override
    protected boolean canPlaceAt(Cell cell, int x, int y) {
        // Factories can be placed on any empty or home cell, but not on occupied cells
        return !world.occupied[x][y] &&
                (cell.type == Cell.Type.EMPTY || cell.type == Cell.Type.HOME);
    }

    @Override
    protected void handleLeftClick(Cell cell, int x, int y) {
        Factory factory = new Factory(x * world.cellSize, y * world.cellSize);
        world.factories.add(factory); // make sure your GameWorld has a factories list
        cell.building = factory;
        world.occupied[x][y] = true;
    }

    @Override
    protected void updateGhost(Cell cell, int x, int y) {
        if (world.ghostFactory == null)
            world.ghostFactory = new Factory(x * world.cellSize, y * world.cellSize);
        else {
            world.ghostFactory.xPos = x * world.cellSize;
            world.ghostFactory.yPos = y * world.cellSize;
        }
    }

    @Override
    protected void resetGhost() {
        world.ghostFactory = null;
    }

    public void handleFactoryProduction(float delta) {
        for (Factory factory : world.factories) {
            Recipe recipe = factory.activeRecipe;
            if (recipe == null) continue;

            boolean enoughResources = true;

            Map<ResourceType, Double> deltaWeightedInputs = new HashMap<>();
            for (Map.Entry<ResourceType, Integer> entry : recipe.inputs.entrySet()) {
                deltaWeightedInputs.put(entry.getKey(), (double) entry.getValue() * delta);
            }

            // Check if enough resources exist in the world
            for (Map.Entry<ResourceType, Double> entry : deltaWeightedInputs.entrySet()) {
                ResourceType type = entry.getKey();
                double neededAmount = entry.getValue();

                if (type instanceof Resource.RawResourceType) {
                    double available = world.getGameStateManager().getRawResourceCount().getOrDefault(type, 0.0);
                    if (available < neededAmount) {
                        enoughResources = false;
                        break;
                    }
                } else if (type instanceof Resource.RefinedResourceType) {
                    double available = world.getGameStateManager().getRefinedResourceCount().getOrDefault(type, 0.0);
                    if (available < neededAmount) {
                        enoughResources = false;
                        break;
                    }
                }
            }

            if (enoughResources) {
                for (Map.Entry<ResourceType, Double> entry : deltaWeightedInputs.entrySet()) {
                    world.getGameStateManager().consumeResource(entry.getKey(), entry.getValue());
                }

                for (Map.Entry<ResourceType, Integer> outputEntry : recipe.outputs.entrySet()) {
                    double amountToAdd = outputEntry.getValue() * delta;
                    world.getGameStateManager().addResource(outputEntry.getKey(), amountToAdd);
                }
            }
        }
    }

    // Optional: animate factories if they have an animation state
    public void animateFactories(float delta) {
        for (Factory factory : world.factories) {
            factory.updateAnimationState(delta);
        }
    }
}
