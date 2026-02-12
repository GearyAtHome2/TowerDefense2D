package com.Geary.towerdefense.world;

import com.Geary.towerdefense.entity.resources.Resource;

import java.util.Map;

public class LevelData {

    private final String id;
    private final String displayName;
    private final Map<Resource.RawResourceType, Integer> resourceAllocation;

    public LevelData(
        String id,
        String displayName,
        Map<Resource.RawResourceType, Integer> resourceAllocation
    ) {
        this.id = id;
        this.displayName = displayName;
        this.resourceAllocation = resourceAllocation;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Map<Resource.RawResourceType, Integer> getResourceAllocation() {
        return resourceAllocation;
    }
}
