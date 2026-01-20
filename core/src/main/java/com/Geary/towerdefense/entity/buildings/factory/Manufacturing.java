package com.Geary.towerdefense.entity.buildings.factory;

import com.Geary.towerdefense.UI.render.production.FactoryAppearance;
import com.Geary.towerdefense.entity.buildings.Building;
import com.Geary.towerdefense.entity.resources.Recipe;
import com.badlogic.gdx.graphics.Color;

import java.util.ArrayList;
import java.util.List;

public abstract class Manufacturing extends Building implements Cloneable {
    public List<Recipe> recipes = new ArrayList<>();
    public Recipe activeRecipe = null;


    public Manufacturing(float x, float y) {
        super(x, y);
        this.name = "parent factory object";
    }

    public void updateAnimationState(float delta) {
        if (isConnectedToNetwork) {
            animationState += delta * 0.3f;
        }
    }

    @Override
    public Manufacturing clone() {
        try {
            return (Manufacturing) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(); // can't happen
        }
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

    public abstract FactoryAppearance getAppearance();
}
