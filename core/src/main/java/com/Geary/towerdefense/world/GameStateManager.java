package com.Geary.towerdefense.world;

import com.badlogic.gdx.Input;

public class GameStateManager {
    public boolean paused = false;
    public float gameSpeed = 1f;

    public void togglePause() {
        paused = !paused;
    }

    public void updateGameSpeedKeys() {
        if (com.badlogic.gdx.Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) gameSpeed = 1f;
        if (com.badlogic.gdx.Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) gameSpeed = 3f;
        if (com.badlogic.gdx.Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) gameSpeed = 9f;
    }
}
