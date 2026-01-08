package com.Geary.towerdefense.entity.buildings;

import com.Geary.towerdefense.entity.resources.Recipe;
import com.Geary.towerdefense.entity.resources.Resource;
import com.badlogic.gdx.graphics.Color;

import java.util.ArrayList;
import java.util.List;

public class Factory extends Building {
    public List<Recipe> recipes = new ArrayList<>();
    public Recipe activeRecipe = null;

    public Factory(float x, float y) {
        super(x, y);
        for (int i = 0; i < 20; i++) {
            Recipe recipe = new Recipe("Recipe "+i);
            recipe.addInput(Resource.RawResourceType.IRON, 99999);
            recipe.addInput(Resource.RawResourceType.STONE, 1);
            recipe.addOutput(Resource.RefinedResourceType.BASIC_AMMO, 99999);
            recipe.addOutput(Resource.RefinedResourceType.BASIC_WEAPONS, 1);
            recipes.add(recipe);
        }
    }

    public void updateAnimationState(float delta) {
        if (isConnectedToNetwork) {
            animationState += delta * 0.3f;
        }
    }

    @Override
    public List<String> getInfoLines() {
        return List.of(
            "Recipe object: " + recipes
        );
    }

    public void activateRecipe(Recipe recipe) {
        this.activeRecipe = recipe;
    }

    public Color getInfoTextColor() {
        return Color.CYAN; // default
    }
}
