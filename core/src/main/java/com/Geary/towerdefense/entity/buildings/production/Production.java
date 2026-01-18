package com.Geary.towerdefense.entity.buildings.production;

import com.Geary.towerdefense.entity.buildings.Building;
import com.Geary.towerdefense.entity.resources.Resource;
import com.badlogic.gdx.graphics.Color;

import java.util.List;

public abstract class Production extends Building implements Cloneable  {
    public Resource resource;

    public Production(float x, float y) {
        super(x, y);
        this.resource = new Resource(Resource.RawResourceType.COAL, 0.1f);
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

    @Override
    public Production clone() {
        try {
            return (Production) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(); // can't happen
        }
    }

    @Override
    public List<String> getInfoLines() {
        return List.of(
            this.name,
            "Abundance: " + resource.resourceAbundance
        );
    }

    public Color getInfoTextColor() {
        return Color.CYAN; // default
    }
}
