package com.Geary.towerdefense.world;

import com.Geary.towerdefense.entity.resources.Resource;

import java.util.EnumMap;

public class GameState {
    EnumMap<Resource.RawResourceType, Double> rawResources = new EnumMap<>(Resource.RawResourceType.class);
    EnumMap<Resource.RefinedResourceType, Double> refinedResources = new EnumMap<>(Resource.RefinedResourceType.class);
    int coins;

    public GameState(){
        for (Resource.RawResourceType type: Resource.RawResourceType.values()){
            rawResources.put(type, 3535353.0);
        }
        for (Resource.RefinedResourceType type: Resource.RefinedResourceType.values()){
            refinedResources.put(type, 2.0);
        }
        coins=150;
    }

    public int getCoins(){ return coins;}

    public EnumMap<Resource.RawResourceType, Double> getRawResources() { return rawResources;}
    public EnumMap<Resource.RefinedResourceType, Double> getRefinedResources() { return refinedResources;}
}
