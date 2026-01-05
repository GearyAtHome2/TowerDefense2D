package com.Geary.towerdefense.entity.buildings;

import com.Geary.towerdefense.entity.resources.Resource;

public class Mine extends Building {
    public Resource resource;

    public Mine(float x, float y) {
        super(x, y);
        this.resource = new Resource(Resource.ResourceType.COAL, 0.1f);
    }

    public Mine(float x, float y, Resource resource) {
        super(x, y);
        this.resource = resource;
        System.out.println("created mine with rotation speed corresponding to: "+resource.resourceAbundance);
    }

    public void updateAnimationState(float delta) {
        float rotationSpeed = resource.resourceAbundance;
        animationState += delta * rotationSpeed;
        if (animationState > 1f) {
            animationState -= 1f;
        }
    }
}
