package com.Geary.towerdefense.entity.resources;

import com.Geary.towerdefense.entity.resources.mapEntity.ResourceType;

public class Resource {
    public float xPos, yPos;
    public float resourceAbundance;
    public RawResourceType type;

    public Resource(RawResourceType type, float resourceAbundance) {
        this.type = type;
        this.resourceAbundance = resourceAbundance;
    }

    public enum RawResourceType implements ResourceType {
        STONE, COPPER, TIN, IRON, COAL;

        @Override
        public String getName() {
            return this.name(); // or a prettier name if you want
        }
    }

    public enum RefinedResourceType implements ResourceType {
        BASIC_AMMO, BASIC_WEAPONS;

        @Override
        public String getName() {
            return this.name();
        }
    }
}
