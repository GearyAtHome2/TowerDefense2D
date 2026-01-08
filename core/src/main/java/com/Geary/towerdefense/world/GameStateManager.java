package com.Geary.towerdefense.world;

import com.Geary.towerdefense.entity.resources.Resource;
import com.Geary.towerdefense.entity.resources.mapEntity.ResourceType;
import com.badlogic.gdx.Input;

import java.util.EnumMap;
import java.util.Map;

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


    public void addRawResources(Map<Resource.RawResourceType, Float> resources) {
        for (ResourceType type : resources.keySet()) {
            gameState.rawResources.put((Resource.RawResourceType) type, gameState.rawResources.getOrDefault(type, 0.0) + resources.get(type));
        }

    }

    // Adds a single resource amount (delta-safe)
    public void addResource(ResourceType type, double amount) {
        if (type instanceof Resource.RawResourceType) {
            Resource.RawResourceType rawType = (Resource.RawResourceType) type;
            gameState.rawResources.put(rawType, gameState.rawResources.getOrDefault(rawType, 0.0) + amount);
        } else if (type instanceof Resource.RefinedResourceType) {
            Resource.RefinedResourceType refinedType = (Resource.RefinedResourceType) type;
            gameState.refinedResources.put(refinedType, gameState.refinedResources.getOrDefault(refinedType, 0.0) + amount);
        }
    }

    // Consumes a single resource amount, clamping at zero
    public boolean consumeResource(ResourceType type, double amount) {
        if (type instanceof Resource.RawResourceType) {
            Resource.RawResourceType rawType = (Resource.RawResourceType) type;
            double current = gameState.rawResources.getOrDefault(rawType, 0.0);
            if (current < amount) return false; // Not enough
            gameState.rawResources.put(rawType, current - amount);
            return true;
        } else if (type instanceof Resource.RefinedResourceType) {
            Resource.RefinedResourceType refinedType = (Resource.RefinedResourceType) type;
            double current = gameState.refinedResources.getOrDefault(refinedType, 0.0);
            if (current < amount) return false; // Not enough
            gameState.refinedResources.put(refinedType, current - amount);
            return true;
        }
        return false;
    }

}
