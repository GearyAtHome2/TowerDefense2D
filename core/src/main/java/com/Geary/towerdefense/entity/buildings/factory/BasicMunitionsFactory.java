package com.Geary.towerdefense.entity.buildings.factory;

import com.Geary.towerdefense.UI.render.production.FactoryAppearance;
import com.Geary.towerdefense.UI.render.production.FactoryAppearances;
import com.Geary.towerdefense.entity.resources.Recipe;
import com.Geary.towerdefense.entity.resources.Resource;

public class BasicMunitionsFactory extends Factory {

    public BasicMunitionsFactory(float x, float y) {
        super(x, y);
        for (int i = 0; i < 4; i++) {
            Recipe recipe = new Recipe("Recipe " + i);
            recipe.addInput(Resource.RawResourceType.IRON, 1);
            recipe.addInput(Resource.RawResourceType.STONE, 1);
            recipe.addOutput(Resource.RefinedResourceType.BASIC_AMMO, 200);
            recipes.add(recipe);
        }
        this.name = "Basic Munitions Factory";
    }

    public FactoryAppearance getAppearance() { return FactoryAppearances.WEAPONS;
    }
}
