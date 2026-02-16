package com.Geary.towerdefense;

import com.Geary.towerdefense.levelSelect.LevelData;
import com.Geary.towerdefense.progress.PlayerProgress;
import com.Geary.towerdefense.screens.LevelSelectScreen;
import com.Geary.towerdefense.screens.TitleScreen;
import com.badlogic.gdx.Game;

import java.util.ArrayList;
import java.util.List;

public class TowerDefenseGame extends Game {

    private PlayerProgress playerProgress;
    private List<LevelData> levels;

    @Override
    public void create() {
        playerProgress = new PlayerProgress();
        setScreen(new TitleScreen(this));
    }

    public List<LevelData> getLevels() { return levels;}

    public PlayerProgress getPlayerProgress() {
        return playerProgress;
    }

    public void startLevel(LevelData levelData) {
        setScreen(new GameScreen(this, levelData));
    }

    public void goToLevelSelect() {
        setScreen(new LevelSelectScreen(this));
    }

    public void loadLevels() {
        levels = new ArrayList<>();

        // Example level 1
//        Map<Resource.RawResourceType, Integer> level1Resources = new HashMap<>();
//        level1Resources.put(Resource.RawResourceType.IRON, 2);
//        level1Resources.put(Resource.RawResourceType.COAL, 2);
//        levels.add(new LevelData("level1", "The First Level", level1Resources));
//
//        // Example level 2
//        Map<Resource.RawResourceType, Integer> level2Resources = new HashMap<>();
//        level2Resources.put(Resource.RawResourceType.IRON, 3);
//        level2Resources.put(Resource.RawResourceType.COAL, 1);
//        level2Resources.put(Resource.RawResourceType.COPPER, 2);
//        levels.add(new LevelData("level2", "Copper Challenge", level2Resources));
//
//        Map<Resource.RawResourceType, Integer> level3Resources = new HashMap<>();
//        level3Resources.put(Resource.RawResourceType.IRON, 3);
//        level3Resources.put(Resource.RawResourceType.COAL, 1);
//        level3Resources.put(Resource.RawResourceType.COPPER, 2);
//        levels.add(new LevelData("level2", "Level 3", level3Resources));

//        for (int i=0; i< 20; i++){
//            Map<Resource.RawResourceType, Integer> levelResources = new HashMap<>();
//            levelResources.put(Resource.RawResourceType.IRON, 3);
//            levelResources.put(Resource.RawResourceType.COAL, 1);
//            levelResources.put(Resource.RawResourceType.COPPER, 2);
//            levels.add(new LevelData("level"+i, "Level "+i, levelResources));
//        }
    }
}
