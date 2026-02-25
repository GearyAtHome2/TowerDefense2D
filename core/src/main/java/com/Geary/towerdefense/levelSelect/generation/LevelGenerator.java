package com.Geary.towerdefense.levelSelect.generation;

import com.Geary.towerdefense.entity.Entity;
import com.Geary.towerdefense.levelSelect.levels.LevelData;

import java.util.LinkedHashMap;
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

    public LevelData getLevel(Entity.Order primary, Entity.Order secondary, int tier) {

        if (secondary != Entity.Order.NEUTRAL) {
            if (tier < 7) tier = 3; // all non-final merged levels are t3.
        }

        Map<String, LevelData> sourceMap = getSourceMap(primary, secondary);

        if (sourceMap.isEmpty()) {
            System.out.println("WARNING: source map empty for "
                + primary + " / " + secondary);
            return null;
        }

        int currentTier = tier;

        while (currentTier <= 10) { // adjust max tier if needed

            for (Map.Entry<String, LevelData> entry : sourceMap.entrySet()) {

                LevelData level = entry.getValue();

                if (level.getTier() == currentTier) {
                    removeFromBackingMap(entry.getKey(), primary, secondary);
                    System.out.println("generated level: "
                        + level.getDisplayName() + ", tier: " + currentTier);
                    return level;
                }
            }

            currentTier++; // try next tier up
        }

        System.out.println("WARNING: No level found for tier "
            + tier + " or higher ("
            + primary + " / " + secondary + ")");

        return null;
    }

    private Map<String, LevelData> getSourceMap(Entity.Order primary, Entity.Order secondary) {

        if (secondary != Entity.Order.NEUTRAL) {
            return mergedLevels.entrySet()
                .stream()
                .filter(entry -> {
                    LevelData levelData = entry.getValue();

                    boolean matchesPrimary =
                        levelData.getPrimaryOrder() == primary
                            || levelData.getSecondaryOrder() == primary;

                    boolean matchesSecondary =
                        levelData.getPrimaryOrder() == secondary
                            || levelData.getSecondaryOrder() == secondary;

                    return matchesPrimary && matchesSecondary;
                })
                .collect(java.util.stream.Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue,
                    (a, b) -> a,
                    LinkedHashMap::new
                ));
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

    private void removeFromBackingMap(String key, Entity.Order primary, Entity.Order secondary) {

        if (secondary != Entity.Order.NEUTRAL) {
            mergedLevels.remove(key);
            return;
        }

        switch (primary) {
            case FIRE -> fireLevels.remove(key);
            case WATER -> waterLevels.remove(key);
            case DARK -> darkLevels.remove(key);
            case LIGHT -> lightLevels.remove(key);
            case NATURE -> natureLevels.remove(key);
            case TECH -> techLevels.remove(key);
        }
    }
}
