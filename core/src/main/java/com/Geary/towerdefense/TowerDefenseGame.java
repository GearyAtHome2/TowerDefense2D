package com.Geary.towerdefense;

import com.badlogic.gdx.Game;

public class TowerDefenseGame extends Game {
    @Override
    public void create() {
        setScreen(new GameScreen());
    }
}
