package com.Geary.towerdefense.entity.resources;

import java.util.EnumMap;

public class Recipe {
    public final EnumMap<Resource.RawResourceType, Integer> inputs = new EnumMap<>(Resource.RawResourceType.class);
    public final EnumMap<Resource.RefinedResourceType, Integer> outputs = new EnumMap<>(Resource.RefinedResourceType.class);

    public Recipe() {}

    public Recipe addInput(Resource.RawResourceType type, int amount) {
        inputs.put(type, amount);
        return this;
    }

    public Recipe addOutput(Resource.RefinedResourceType type, int amount) {
        outputs.put(type, amount);
        return this;
    }
}
