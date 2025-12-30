package com.Geary.towerdefense.lwjgl3;

import com.Geary.towerdefense.TowerDefenseGame;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

public class Lwjgl3Launcher {
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        config.setTitle("Tower Defense");
        config.setWindowedMode(800, 600);
        new Lwjgl3Application(new TowerDefenseGame(), config);
    }
}
