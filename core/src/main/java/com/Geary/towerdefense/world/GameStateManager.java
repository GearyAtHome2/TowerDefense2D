package com.Geary.towerdefense.world;

import com.Geary.towerdefense.entity.resources.Resource;
import com.badlogic.gdx.Input;

import java.util.EnumMap;

public class GameStateManager {
    public boolean paused = false;
    public float gameSpeed = 1f;
    public GameState gameState;

    public GameStateManager() {
        this.gameState = new GameState();
    }

    public void togglePause() {
        paused = !paused;
    }

    public void updateGameSpeedKeys() {
        if (com.badlogic.gdx.Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) gameSpeed = 1f;
        if (com.badlogic.gdx.Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) gameSpeed = 3f;
        if (com.badlogic.gdx.Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) gameSpeed = 9f;
    }

    public EnumMap<Resource.RawResourceType, Double> getRawResourceCount() {
        return gameState.rawResources;
    }

    public EnumMap<Resource.RefinedResourceType, Double> getRefinedResourceCount() {
        return gameState.refinedResources;
    }

    public void addResources(EnumMap<Resource.RawResourceType, Float> resources) {
        for (Resource.RawResourceType type : resources.keySet()) {
            gameState.rawResources.put(type, gameState.rawResources.getOrDefault(type, 0.0) + resources.get(type));
        }
    }
}
