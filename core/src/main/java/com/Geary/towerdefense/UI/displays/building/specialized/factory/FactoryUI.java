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
    protected float getPopupScale(float zoom) {
        return super.getPopupScale(zoom);
    }

    @Override
    protected void addExtraButtons(
        Building building,
        float popupX,
        float popupY,
        float popupWidth,
        float popupHeight,
        float scale
    ) {
        addStackedButton(
            "Open Factory Menu",
            popupX,
            popupWidth,
            scale,
            0.2f, 0.6f, 1f,
            () -> world.showFactoryMenu((Factory) building, font)
        );
    }
}
