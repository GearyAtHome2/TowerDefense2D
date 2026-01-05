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
    }

    public void updateAnimationState(float delta) {
        if (isConnectedToNetwork) {
            float rotationSpeed = resource.resourceAbundance;
            animationState += delta * rotationSpeed * 0.3f;
            if (animationState > 1f) {
                animationState -= 1f;
            }
        }
    }
}
