package com.Geary.towerdefense.levelSelect;

import com.Geary.towerdefense.entity.resources.Resource;

import java.util.HashMap;
import java.util.Map;

public class LevelGenerator {



    //no-arg for now - soon we will want to store stuff, maybe have multiple pages of level eventually?
    public LevelGenerator(
    ) {}

    public LevelData generateLevel(LevelGridCell cell, int index){
        //todo: add more and more complex resources here for higher index.
        //handle index being a float in future for branched levels
        //LevelData should eventually be completely specific to a particular enemy? Maybe Boss boolean also.
        Map<Resource.RawResourceType, Integer> levelResources = new HashMap<>();
        levelResources.put(Resource.RawResourceType.IRON, 3);
        levelResources.put(Resource.RawResourceType.COAL, 1);
        levelResources.put(Resource.RawResourceType.COPPER, 2);

        return new LevelData("level"+index, "Level "+index, levelResources, cell.getDominantOrder(), cell.getSecondDominantOrder());
    }
}
