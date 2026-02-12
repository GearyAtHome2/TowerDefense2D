package com.Geary.towerdefense;

import com.Geary.towerdefense.progress.PlayerProgress;
import com.Geary.towerdefense.screens.LevelSelectScreen;
import com.Geary.towerdefense.screens.TitleScreen;
import com.Geary.towerdefense.world.LevelData;
import com.badlogic.gdx.Game;

public class TowerDefenseGame extends Game {

    private PlayerProgress playerProgress;

    @Override
    public void create() {
        playerProgress = new PlayerProgress();
        setScreen(new TitleScreen(this));
    }

    public PlayerProgress getPlayerProgress() {
        return playerProgress;
    }

    public void startLevel(LevelData levelData) {
        setScreen(new GameScreen(this, levelData));
    }

    public void goToLevelSelect() {
        setScreen(new LevelSelectScreen(this));
    }
}
