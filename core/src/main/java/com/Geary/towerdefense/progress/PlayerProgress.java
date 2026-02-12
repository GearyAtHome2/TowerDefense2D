package com.Geary.towerdefense.progress;

import java.util.HashSet;
import java.util.Set;

public class PlayerProgress {

    private Set<String> completedLevels = new HashSet<>();
    private Set<String> unlockedLevels = new HashSet<>();
    private int currency = 0;

    public boolean isLevelUnlocked(String levelId) {
        return unlockedLevels.contains(levelId);
    }

    public boolean isLevelCompleted(String levelId) {
        return completedLevels.contains(levelId);
    }

    public void completeLevel(String levelId) {
        completedLevels.add(levelId);
        unlockNext(levelId);
    }

    private void unlockNext(String levelId) {
        // your progression logic
    }

    public void addCurrency(int amount) {
        currency += amount;
    }

    public int getCurrency() {
        return currency;
    }
}
