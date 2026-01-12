package com.Geary.towerdefense.UI.displays.mob;

import com.Geary.towerdefense.UI.displays.building.EntityUI;
import com.Geary.towerdefense.entity.Entity;
import com.Geary.towerdefense.entity.mob.Mob;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.Geary.towerdefense.world.GameWorld;

public class MobUI extends EntityUI {

    public MobUI(GameWorld world, ShapeRenderer shapeRenderer, SpriteBatch batch, BitmapFont font) {
        super(world, shapeRenderer, batch, font);
    }

    @Override
    protected boolean shouldDrawDeleteButton(Entity entity) {
        return false;
    }

    @Override
    protected void addExtraButtons(com.Geary.towerdefense.entity.Entity entity, float popupX, float popupY, float popupWidth, float popupHeight, float scale) {
        if (entity instanceof Mob mob) {
            // example: add a "Target" button for hostile mobs
            addStackedButton("Inspect", popupX, popupWidth, scale, 0.2f, 0.6f, 1f,
                () -> System.out.println("Inspecting " + mob.name));
        }
    }
}
