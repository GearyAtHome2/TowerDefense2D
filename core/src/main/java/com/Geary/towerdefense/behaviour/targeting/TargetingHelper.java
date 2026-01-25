package com.Geary.towerdefense.behaviour.targeting;

import com.Geary.towerdefense.entity.buildings.tower.Tower;
import com.Geary.towerdefense.entity.mob.enemy.Enemy;

import java.util.List;

public class TargetingHelper {

    public static Enemy findClosest(Tower tower, List<Enemy> enemies) {
        Enemy closest = null;
        float closestDistance = Float.MAX_VALUE;

        float centerX = tower.getCentreX();
        float centerY = tower.getCentreY();

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

        float centerX = tower.getCentreX();
        float centerY = tower.getCentreY();

        for (Enemy e : enemies) {
            float dx = e.getCenterX() - centerX;
            float dy = e.getCenterY() - centerY;
            float distSqu = dx * dx + dy * dy;

            float tileProgress = e.getPathIndex();

            if (distSqu <= (tower.range * tower.range) && tileProgress > mostProgressed) {
                mostProgressed = tileProgress;
                best = e;
            }
        }
        return best;
    }
}
