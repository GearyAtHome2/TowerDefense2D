package com.Geary.towerdefense.world;

import com.Geary.towerdefense.entity.resources.Resource;

import java.util.EnumMap;

public class GameState {
    private EnumMap<Resource.ResourceType, Float> resources;

    public GameState(){}

    public void addResource(Resource.ResourceType type, float quantity){
        float current = resources.getOrDefault(type, 0f);
        float updated = current + quantity;
        resources.put(type, updated);
    }
}
