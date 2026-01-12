package com.Geary.towerdefense.UI.displays.tooltip;

import com.Geary.towerdefense.UI.displays.tooltip.entity.DefaultEntityUI;
import com.Geary.towerdefense.UI.displays.tooltip.entity.EntityUI;
import com.Geary.towerdefense.UI.displays.tooltip.entity.FactoryUI;
import com.Geary.towerdefense.UI.displays.tooltip.entity.SpawnerUI;
import com.Geary.towerdefense.entity.Entity;
import com.Geary.towerdefense.entity.buildings.Factory;
import com.Geary.towerdefense.entity.spawner.FriendlySpawner;
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

    private final EntityUI defaultUI;
    private final Map<Class<?>, EntityUI> specializedUIs = new HashMap<>();

    public UIManager(GameWorld world, ShapeRenderer shapeRenderer, SpriteBatch batch, BitmapFont font) {
        this.world = world;
        this.shapeRenderer = shapeRenderer;
        this.batch = batch;
        this.font = font;

        defaultUI = new DefaultEntityUI(world, shapeRenderer, batch, font);

        // Register specialized UIs
        specializedUIs.put(Factory.class, new FactoryUI(world, shapeRenderer, batch, font));
        specializedUIs.put(FriendlySpawner.class, new SpawnerUI(world, shapeRenderer, batch, font));
        // Add more: TowerUI, MineUI, etc.
    }

    public EntityUI getUIFor(Entity entity) {
        if (entity == null) throw new IllegalArgumentException("UIManager.getUIFor() called with null entity");

        for (Map.Entry<Class<?>, EntityUI> entry : specializedUIs.entrySet()) {
            if (entry.getKey().isAssignableFrom(entity.getClass())) {
                return entry.getValue();
            }
        }

        return defaultUI;
    }
}
