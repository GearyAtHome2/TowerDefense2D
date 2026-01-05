package com.Geary.towerdefense.world;

import com.Geary.towerdefense.entity.resources.Resource;

public class BankedResource {
    public int resourceCount;
    public Resource.ResourceType resourceType;

    public BankedResource(int count, Resource.ResourceType type){
        resourceCount = count;
        resourceType = type;
    }
}
