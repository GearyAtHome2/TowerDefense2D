package com.Geary.towerdefense.levelSelect.generation;

import com.Geary.towerdefense.entity.Entity;
import com.Geary.towerdefense.levelSelect.levels.LevelData;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class LevelGenerator {

    private final Map<String, LevelData> fireLevels = new LinkedHashMap<>();
    private final Map<String, LevelData> waterLevels = new LinkedHashMap<>();
    private final Map<String, LevelData> darkLevels = new LinkedHashMap<>();
    private final Map<String, LevelData> lightLevels = new LinkedHashMap<>();
    private final Map<String, LevelData> natureLevels = new LinkedHashMap<>();
    private final Map<String, LevelData> techLevels = new LinkedHashMap<>();
    private final Map<String, LevelData> mergedLevels = new LinkedHashMap<>();

    private final LevelLoader loader = new LevelLoader();

    public LevelGenerator() {
        loadAllLevels();
    }

    private void loadAllLevels() {
        fireLevels.putAll(loader.loadLevels("levelData/fire.json"));
        waterLevels.putAll(loader.loadLevels("levelData/water.json"));
        darkLevels.putAll(loader.loadLevels("levelData/dark.json"));
        lightLevels.putAll(loader.loadLevels("levelData/light.json"));
        natureLevels.putAll(loader.loadLevels("levelData/nature.json"));
        techLevels.putAll(loader.loadLevels("levelData/tech.json"));
        mergedLevels.putAll(loader.loadLevels("levelData/merged.json"));
    }

    public LevelData getLevel(Entity.Order primary, Entity.Order secondary, int index) {
        Map<String, LevelData> sourceMap = getSourceMap(primary, secondary);
        if (sourceMap.isEmpty()) {
            System.out.println("WARNING: source map is empty for primary=" + primary + ", secondary=" + secondary);
            return null;
        }

        List<LevelData> levels = sourceMap.values().stream().toList();
        if (index < levels.size()) return levels.get(index);
        return levels.get(0); // fallback
    }

    public List<LevelData> getAllLevels(Entity.Order primary, Entity.Order secondary) {
        return getSourceMap(primary, secondary).values().stream().toList();
    }

    private Map<String, LevelData> getSourceMap(Entity.Order primary, Entity.Order secondary) {
        if (secondary != Entity.Order.NEUTRAL) {
            return mergedLevels;
        }
        return switch (primary) {
            case FIRE -> fireLevels;
            case WATER -> waterLevels;
            case DARK -> darkLevels;
            case LIGHT -> lightLevels;
            case NATURE -> natureLevels;
            case TECH -> techLevels;
            default -> new LinkedHashMap<>();
        };
    }
}
