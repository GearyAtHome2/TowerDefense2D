package com.Geary.towerdefense.entity.resources;

public class Resource {
    public float xPos, yPos;
    public float resourceAbundance;
    public RawResourceType type;

    public Resource(RawResourceType type, float resourceAbundance){
        this.type = type;
        this.resourceAbundance = resourceAbundance;
    }

    public enum RawResourceType { STONE, COPPER, TIN, IRON, COAL}
    public enum RefinedResourceType {BASIC_AMMO, BASIC_WEAPONS}
}
