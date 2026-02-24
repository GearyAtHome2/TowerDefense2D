package com.Geary.towerdefense.levelSelect.generation;

import com.Geary.towerdefense.levelSelect.levels.LevelData;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LevelLoader {

    private final Json json;

    public LevelLoader() {
        json = new Json();
        json.setIgnoreUnknownFields(true); // just in case JSON has extra fields
    }

    public Map<String, LevelData> loadLevels(String fileName) {
        FileHandle file = Gdx.files.internal(fileName);
        Map<String, LevelData> levelMap = new LinkedHashMap<>();

        if (!file.exists()) {
            System.err.println("Level file not found: " + fileName);
            return levelMap;
        }

        LevelData[] levelArray = json.fromJson(LevelData[].class, file);
        List<LevelData> levels = Arrays.asList(levelArray);

        for (LevelData level : levels) {
            levelMap.put(level.getId(), level);
        }

        System.out.println("Loaded " + levels.size() + " levels from " + fileName);
        return levelMap;
    }
}
