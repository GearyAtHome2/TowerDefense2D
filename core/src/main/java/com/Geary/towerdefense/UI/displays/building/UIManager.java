package com.Geary.towerdefense.UI.displays.building;

import com.Geary.towerdefense.UI.displays.building.specialized.factory.FactoryUI;
import com.Geary.towerdefense.entity.Entity;
import com.Geary.towerdefense.entity.buildings.Factory;
import com.Geary.towerdefense.world.GameWorld;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.HashMap;
import java.util.Map;

public class UIManager {

    private final GameWorld world;
    private final ShapeRenderer shapeRenderer;
    private final SpriteBatch batch;
    private final BitmapFont font;

    private final BuildingUI defaultUI;
    private final Map<Class<? extends Entity>, EntityUI> specializedUIs = new HashMap<>();

    public UIManager(GameWorld world, ShapeRenderer shapeRenderer, SpriteBatch batch, BitmapFont font) {
        this.world = world;
        this.shapeRenderer = shapeRenderer;
        this.batch = batch;
        this.font = font;

        defaultUI = new BuildingUI(world, shapeRenderer, batch, font);

        // Pre-create specialized UIs
        specializedUIs.put(Factory.class, new FactoryUI(world, shapeRenderer, batch, font));
        // Add others as needed: TowerUI, MineUI, etc.
    }

    public EntityUI getUIFor(Entity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("UIFactory.getUIFor() called with null entity");
        }
        return specializedUIs.getOrDefault(entity.getClass(), defaultUI);
    }
}
