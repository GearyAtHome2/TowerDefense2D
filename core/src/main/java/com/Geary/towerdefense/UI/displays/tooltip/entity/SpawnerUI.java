package com.Geary.towerdefense.UI.displays.tooltip.entity;

import com.Geary.towerdefense.entity.Entity;
import com.Geary.towerdefense.entity.spawner.FriendlySpawner;
import com.Geary.towerdefense.world.GameWorld;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class SpawnerUI extends DefaultEntityUI {

    public SpawnerUI(GameWorld world, ShapeRenderer shapeRenderer, SpriteBatch batch, BitmapFont font, OrthographicCamera camera) {
        super(world, shapeRenderer, batch, font, camera);
    }

    @Override
    protected boolean shouldDrawDeleteButton(Entity entity) {
        return false;
    }

    @Override
    protected void addExtraButtons(com.Geary.towerdefense.entity.Entity entity, float popupX, float popupY, float popupWidth, float popupHeight, float scale) {
        if (entity instanceof FriendlySpawner spawner) {
            addStackedButton("Open Spawner Menu", popupX, popupWidth, scale, 0.2f, 0.6f, 1f,
                () -> {
                    world.showSpawnerMenu(spawner, font, camera);
                });
        }
    }
}
