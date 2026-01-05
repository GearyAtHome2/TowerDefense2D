package com.Geary.towerdefense.entity.resources;

public class Resource {
    public float xPos, yPos;
    public float resourceAbundance = 0;
    public ResourceType type;

    public Resource(ResourceType type, float resourceAbundance){
        this.type = type;
        this.resourceAbundance = resourceAbundance;
    }

    public enum ResourceType { STONE, COPPER, TIN, IRON, COAL}
}
