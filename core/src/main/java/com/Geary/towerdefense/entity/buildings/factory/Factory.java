package com.Geary.towerdefense.entity.buildings.factory;

import com.Geary.towerdefense.entity.resources.Recipe;
import com.badlogic.gdx.graphics.Color;

import java.util.ArrayList;
import java.util.List;

public abstract class Factory extends Manufacturing {
    public List<Recipe> recipes = new ArrayList<>();
    public Recipe activeRecipe = null;


    public Factory(float x, float y) {
        super(x, y);
    }

    @Override
    public List<String> getInfoLines() {
        List infoLines = new ArrayList<>();
        infoLines.add(this.name);
        if (activeRecipe != null){
            infoLines.add(activeRecipe.name);
        }
        return infoLines;
    }

    public Color getInfoTextColor() {
        return Color.CYAN; // default
    }
}
