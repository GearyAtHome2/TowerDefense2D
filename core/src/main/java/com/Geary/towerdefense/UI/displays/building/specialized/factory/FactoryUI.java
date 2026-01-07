package com.Geary.towerdefense.UI.displays.building.specialized.factory;

import com.Geary.towerdefense.UI.displays.building.BuildingUI;
import com.Geary.towerdefense.entity.buildings.Building;
import com.Geary.towerdefense.entity.buildings.Factory;
import com.Geary.towerdefense.world.GameWorld;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class FactoryUI extends BuildingUI {

    public FactoryUI(GameWorld world, ShapeRenderer shapeRenderer, SpriteBatch batch, BitmapFont font) {
        super(world, shapeRenderer, batch, font);
    }

    @Override
    protected void addExtraButtons(Building building, float popupX, float popupY, float popupWidth, float popupHeight, float scale) {
        extraButtons.clear();

        float buttonHeight = 20 * scale;
        float buttonWidth = popupWidth - 16;
        float buttonX = popupX + 8;
        float buttonY = popupY + 40; // above delete button

        extraButtons.add(new BuildingUIButton("Open Factory Menu", 0.2f, 0.6f, 1f, () -> {
            world.showFactoryMenu((Factory) building, font);
        }) {{
            bounds.set(buttonX, buttonY, buttonWidth, buttonHeight);
        }});
    }
}
