package com.Geary.towerdefense.world;

import com.Geary.towerdefense.entity.resources.Resource;

import java.util.EnumMap;

public class GameState {
    EnumMap<Resource.RawResourceType, Double> rawResources = new EnumMap<>(Resource.RawResourceType.class);
    EnumMap<Resource.RefinedResourceType, Double> refinedResources = new EnumMap<>(Resource.RefinedResourceType.class);

    public GameState(){
        //todo: remove this, test only
        for (Resource.RawResourceType type: Resource.RawResourceType.values()){
            rawResources.put(type, 3535353.0);
        }
    }
}
