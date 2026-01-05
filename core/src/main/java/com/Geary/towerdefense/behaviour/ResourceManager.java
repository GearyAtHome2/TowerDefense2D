package com.Geary.towerdefense.behaviour;

import com.Geary.towerdefense.entity.resources.Resource;
import com.Geary.towerdefense.entity.world.Cell;
import com.Geary.towerdefense.world.GameWorld;
import com.badlogic.gdx.math.MathUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ResourceManager {

    private final GameWorld world;

    public ResourceManager(GameWorld world) {
        this.world = world;
    }

    public void populate(Map<Resource.ResourceType, Integer> resourceAllocation) {
        List<Cell> freeCells = world.getFreeCells();
        if (freeCells.isEmpty()) return;

        for (Resource.ResourceType resourceType : resourceAllocation.keySet()){
            int quantity = resourceAllocation.get(resourceType);
            Collections.shuffle(freeCells);
            for (int i = 0; i < quantity; i++) {
                placeResource(freeCells.get(i), resourceType);
            }
        };

    }

    private int determineResourceCount(int freeCount) {
        int min = Math.max(1, freeCount / 100);
        int max = Math.max(min, freeCount / 70);
        return MathUtils.random(min, max);
    }

    private void placeResource(Cell cell, Resource.ResourceType resourceType) {
        cell.resource = new Resource(resourceType, 0.5f);
    }
}
