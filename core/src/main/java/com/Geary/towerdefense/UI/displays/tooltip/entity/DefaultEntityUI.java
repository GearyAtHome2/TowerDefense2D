package com.Geary.towerdefense.UI.displays.tooltip.entity;

import com.Geary.towerdefense.entity.Entity;
import com.Geary.towerdefense.entity.buildings.Building;
import com.Geary.towerdefense.world.GameWorld;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class DefaultEntityUI extends EntityUI {
    protected OrthographicCamera camera;

    public DefaultEntityUI(GameWorld world, ShapeRenderer shapeRenderer, SpriteBatch batch, BitmapFont font, OrthographicCamera camera) {
        super(world, shapeRenderer, batch, font);
        this.camera = camera;
    }

    @Override
    protected float getPopupX(Entity entity) {
        return entity.xPos + entity.collisionRadius*2;
    }

    @Override
    protected float getPopupY(Entity entity) {
        return entity.yPos + entity.collisionRadius*2;
    }

    @Override
    protected boolean shouldDrawDeleteButton(Entity entity) {
        if (entity instanceof Building building){
            return building.isDeletable;
        }
        return false;
    }

}
