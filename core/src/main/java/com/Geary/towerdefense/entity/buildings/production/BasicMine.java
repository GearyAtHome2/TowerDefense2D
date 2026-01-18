package com.Geary.towerdefense.entity.buildings.production;

import com.badlogic.gdx.graphics.Color;

import java.util.List;

public class BasicMine extends Production {

    public BasicMine(float x, float y) {
        super(x, y);
        this.name = "Mine";
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
