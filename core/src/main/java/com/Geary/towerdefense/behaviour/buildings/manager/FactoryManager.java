package com.Geary.towerdefense.behaviour.buildings.manager;

import com.Geary.towerdefense.entity.buildings.Factory;
import com.Geary.towerdefense.entity.world.Cell;
import com.Geary.towerdefense.world.GameWorld;
import com.badlogic.gdx.graphics.OrthographicCamera;

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

    // Optional: animate factories if they have an animation state
    public void animateFactories(float delta) {
        for (Factory factory : world.factories) {
            factory.updateAnimationState(delta);
        }
    }
}
