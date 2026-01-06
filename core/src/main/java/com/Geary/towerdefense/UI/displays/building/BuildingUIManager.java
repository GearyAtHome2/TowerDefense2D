package com.Geary.towerdefense.UI.displays.building;

import com.Geary.towerdefense.UI.displays.building.specialized.FactoryUI;
import com.Geary.towerdefense.entity.buildings.Building;
import com.Geary.towerdefense.entity.buildings.Factory;
import com.Geary.towerdefense.world.GameWorld;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.HashMap;
import java.util.Map;

public class BuildingUIManager {

    private final GameWorld world;
    private final ShapeRenderer shapeRenderer;
    private final SpriteBatch batch;
    private final BitmapFont font;

    private final BuildingUI defaultUI;
    private final Map<Class<? extends Building>, BuildingUI> specializedUIs = new HashMap<>();

    public BuildingUIManager(GameWorld world, ShapeRenderer shapeRenderer, SpriteBatch batch, BitmapFont font) {
        this.world = world;
        this.shapeRenderer = shapeRenderer;
        this.batch = batch;
        this.font = font;

        defaultUI = new BuildingUI(world, shapeRenderer, batch, font);

        // Pre-create specialized UIs
        specializedUIs.put(Factory.class, new FactoryUI(world, shapeRenderer, batch, font));
        // Add others as needed: TowerUI, MineUI, etc.
    }

    public BuildingUI getUIFor(Building building) {
        if (building == null) {
            throw new IllegalArgumentException("BuildingUIFactory.getUIFor() called with null building");
        }
        return specializedUIs.getOrDefault(building.getClass(), defaultUI);
    }
}
