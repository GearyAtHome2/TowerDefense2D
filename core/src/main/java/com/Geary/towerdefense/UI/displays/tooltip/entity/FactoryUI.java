package com.Geary.towerdefense.UI.displays.tooltip.entity;

import com.Geary.towerdefense.entity.Entity;
import com.Geary.towerdefense.entity.buildings.factory.Manufacturing;
import com.Geary.towerdefense.world.GameWorld;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class FactoryUI extends DefaultEntityUI {

    public FactoryUI(GameWorld world, ShapeRenderer shapeRenderer, SpriteBatch batch, BitmapFont font, OrthographicCamera camera) {
        super(world, shapeRenderer, batch, font, camera);
    }

    @Override
    protected void addExtraButtons(Entity entity, float popupX, float popupY, float popupWidth, float popupHeight, float scale) {
        if (entity instanceof Manufacturing manufacturing) {
            addStackedButton("Open Factory Menu", popupX, popupWidth, scale, 0.2f, 0.6f, 1f,
                () -> {
                world.showFactoryMenu(manufacturing, font, camera);
            });
        }
    }
}
