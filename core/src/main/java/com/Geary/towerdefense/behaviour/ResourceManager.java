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

    public void populate(Map<Resource.RawResourceType, Integer> resourceAllocation) {
        List<Cell> freeCells = world.getFreeCells();
        if (freeCells.isEmpty()) return;

        for (Resource.RawResourceType rawResourceType : resourceAllocation.keySet()){
            int quantity = resourceAllocation.get(rawResourceType);
            Collections.shuffle(freeCells);
            for (int i = 0; i < quantity; i++) {
                placeResource(freeCells.get(i), rawResourceType);
            }
        };

    }

    private int determineResourceCount(int freeCount) {
        int min = Math.max(1, freeCount / 100);
        int max = Math.max(min, freeCount / 70);
        return MathUtils.random(min, max);
    }

    private void placeResource(Cell cell, Resource.RawResourceType rawResourceType) {
        float abundance;
        switch (cell.type) {
            case HOME -> {
                // Low, random between 0.3 and 0.6
                abundance = MathUtils.random(0.3f, 0.6f);
            }
            case EMPTY -> {
                // Weighted by distance from bottom-left (0,0)
                // Simple approach: normalized distance = (x + y) / (maxX + maxY)
                float maxX = world.gridWidth  * GameWorld.cellSize;
                float maxY = world.gridHeight * GameWorld.cellSize;

                float normalizedDistance = (cell.x + cell.y) / (maxX + maxY);
                // Add a small random variation
                abundance = 0.3f + normalizedDistance * 0.7f * MathUtils.random(0.8f, 1.2f);
                abundance = Math.min(1f, abundance); // clamp to 1.0
            }
            default -> {
                // default value for other types
                abundance = 0.5f;
            }
        }
        System.out.println("placed mine of abundance: "+abundance);
        cell.resource = new Resource(rawResourceType, abundance);
    }

}
