package com.Geary.towerdefense.behaviour.targeting;

import com.Geary.towerdefense.entity.buildings.Tower;
import com.Geary.towerdefense.entity.mob.Enemy;
import com.Geary.towerdefense.world.GameWorld;

import java.util.List;

public class TargetingHelper {

    public static Enemy findClosest(Tower tower, List<Enemy> enemies) {
        Enemy closest = null;
        float closestDistance = Float.MAX_VALUE;

        float centerX = tower.xPos + GameWorld.cellSize / 2f;
        float centerY = tower.yPos + GameWorld.cellSize / 2f;

        for (Enemy e : enemies) {
            float dx = e.getCenterX() - centerX;
            float dy = e.getCenterY() - centerY;
            float dist = dx * dx + dy * dy;

            if (dist <= tower.range * tower.range && dist < closestDistance) {
                closestDistance = dist;
                closest = e;
            }
        }
        return closest;
    }

    public static Enemy findFurthestProgressed(Tower tower, List<Enemy> enemies) {
        Enemy best = null;
        float mostProgressed = 0;

        float centerX = tower.xPos + GameWorld.cellSize / 2f;
        float centerY = tower.yPos + GameWorld.cellSize / 2f;

        for (Enemy e : enemies) {
            float dx = e.getCenterX() - centerX;
            float dy = e.getCenterY() - centerY;
            float distSqu = dx * dx + dy * dy;

            float totalProgress = e.getPathIndex() + e.getTileProgress();

            if (distSqu <= (tower.range * tower.range) && totalProgress > mostProgressed) {
                mostProgressed = totalProgress;
                best = e;
            }
        }
        return best;
    }
}
