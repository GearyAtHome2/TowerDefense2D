package com.Geary.towerdefense.UI.displays.building;

import com.Geary.towerdefense.entity.Entity;
import com.Geary.towerdefense.world.GameWorld;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class DefaultEntityUI extends EntityUI {
    public DefaultEntityUI(GameWorld world, ShapeRenderer shapeRenderer, SpriteBatch batch, BitmapFont font) {
        super(world, shapeRenderer, batch, font);
    }

    @Override
    protected float getPopupX(Entity entity) {
        return entity.xPos + 5;
    }

    @Override
    protected float getPopupY(Entity entity) {
        return entity.yPos + 5;
    }

    @Override
    protected boolean shouldDrawDeleteButton(Entity entity) {
        return false;
    }
}
