package com.Geary.towerdefense.entity.buildings.factory;

import com.Geary.towerdefense.entity.resources.Recipe;
import com.Geary.towerdefense.entity.resources.Resource;
import com.badlogic.gdx.graphics.Color;

import java.util.ArrayList;
import java.util.List;

public class BasicArmourFactory extends Manufacturing {

    public BasicArmourFactory(float x, float y) {
        super(x, y);
        for (int i = 0; i < 4; i++) {
            Recipe recipe = new Recipe("Recipe " + i);
            recipe.addInput(Resource.RawResourceType.IRON, 1);
            recipe.addInput(Resource.RawResourceType.STONE, 1);
            recipe.addOutput(Resource.RefinedResourceType.BASIC_WEAPONS, 1);
            recipes.add(recipe);
        }
        this.name = "Basic Armour Factory";
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
