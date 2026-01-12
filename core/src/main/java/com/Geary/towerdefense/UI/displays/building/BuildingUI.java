package com.Geary.towerdefense.UI.displays.building;

import com.Geary.towerdefense.entity.buildings.Building;
import com.Geary.towerdefense.world.GameWorld;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class BuildingUI extends EntityUI {

    public BuildingUI(GameWorld world, ShapeRenderer shapeRenderer, SpriteBatch batch, BitmapFont font) {
        super(world, shapeRenderer, batch, font);
    }

    @Override
    protected float getPopupX(com.Geary.towerdefense.entity.Entity entity) {
        return entity.xPos + world.cellSize + 5;
    }

    @Override
    protected float getPopupY(com.Geary.towerdefense.entity.Entity entity) {
        return entity.yPos + world.cellSize;
    }

    @Override
    protected boolean shouldDrawDeleteButton(com.Geary.towerdefense.entity.Entity entity) {
        return ((Building) entity).isDeletable;
    }
}
