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

    public EnumMap<Resource.ResourceType, Float> getResourceCount() {
        return gameState.resources;
    }

    public void addResources(EnumMap<Resource.ResourceType, Float> resources) {
        for (Resource.ResourceType type : resources.keySet()) {
            gameState.resources.put(type, gameState.resources.getOrDefault(type, 0f) + resources.get(type));
        }
    }
}
