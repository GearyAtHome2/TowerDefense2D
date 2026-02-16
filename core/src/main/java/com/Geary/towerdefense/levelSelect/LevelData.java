package com.Geary.towerdefense.levelSelect;

import com.Geary.towerdefense.entity.Entity;
import com.Geary.towerdefense.entity.resources.Resource;

import java.util.Map;

public class LevelData {

    private final String id;
    private final String displayName;
    private final Map<Resource.RawResourceType, Integer> resourceAllocation;
    private final Entity.Order primaryOrder;
    private final Entity.Order secondaryOrder;

    public LevelData(
        String id,
        String displayName,
        Map<Resource.RawResourceType, Integer> resourceAllocation,
        Entity.Order primaryOrder,
        Entity.Order secondaryOrder
    ) {
        this.id = id;
        this.displayName = displayName;
        this.resourceAllocation = resourceAllocation;
        this.primaryOrder = primaryOrder;
        this.secondaryOrder = secondaryOrder;
    }

    public LevelData(
        String id,
        String displayName,
        Map<Resource.RawResourceType, Integer> resourceAllocation,
        Entity.Order primaryOrder
    ) {
        this.id = id;
        this.displayName = displayName;
        this.resourceAllocation = resourceAllocation;
        this.primaryOrder = primaryOrder;
        this.secondaryOrder = Entity.Order.NEUTRAL;
    }

    public Entity.Order getPrimaryOrder(){
        return primaryOrder;
    }

    public Entity.Order getSecondaryOrder(){
        return secondaryOrder;
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
