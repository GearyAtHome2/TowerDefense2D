package com.Geary.towerdefense.world;

import com.Geary.towerdefense.entity.resources.Resource;

import java.util.EnumMap;

public class GameState {
    EnumMap<Resource.ResourceType, Float> resources = new EnumMap<>(Resource.ResourceType.class);

    public GameState(){}
}
