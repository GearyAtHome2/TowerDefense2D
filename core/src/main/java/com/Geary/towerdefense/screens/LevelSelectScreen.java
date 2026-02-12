package com.Geary.towerdefense.screens;

import com.Geary.towerdefense.TowerDefenseGame;
import com.Geary.towerdefense.entity.resources.Resource;
import com.Geary.towerdefense.world.LevelData;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;

import java.util.HashMap;
import java.util.Map;

public class LevelSelectScreen implements Screen {

    private final TowerDefenseGame game;

    public LevelSelectScreen(TowerDefenseGame game) {
        this.game = game;
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {

        if (Gdx.input.justTouched()) {

            Map<Resource.RawResourceType, Integer> resources = new HashMap<>();
            resources.put(Resource.RawResourceType.IRON, 2);
            resources.put(Resource.RawResourceType.COAL, 2);

            LevelData level = new LevelData(
                "level1",
                "Test Level",
                resources
            );

            game.startLevel(level);
        }
    }


    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {}
}
