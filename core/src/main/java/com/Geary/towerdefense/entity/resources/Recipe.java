package com.Geary.towerdefense.entity.resources;

import com.Geary.towerdefense.entity.resources.mapEntity.ResourceType;

import java.util.HashMap;
import java.util.Map;

public class Recipe {
    public String name;
    public final Map<ResourceType, Integer> inputs = new HashMap<>();
    public final Map<ResourceType, Integer> outputs = new HashMap<>();

    public Recipe(String name) {this.name=name;}

    public Recipe addInput(Resource.RawResourceType type, int amount) {
        inputs.put(type, amount);
        return this;
    }

    public Recipe addOutput(Resource.RefinedResourceType type, int amount) {
        outputs.put(type, amount);
        return this;
    }
}
