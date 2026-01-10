package com.Geary.towerdefense.behaviour.targeting;

import com.Geary.towerdefense.entity.mob.Bullet;
import com.Geary.towerdefense.entity.buildings.Tower;
import com.Geary.towerdefense.entity.mob.enemy.Enemy;
import com.Geary.towerdefense.world.GameWorld;

public class ShootingHelper {

    public static boolean canShoot(Tower tower) {
        if (tower.currentTarget == null) return false;

        float centerX = tower.xPos + GameWorld.cellSize / 2f;
        float centerY = tower.yPos + GameWorld.cellSize / 2f;

        float[] lead = tower.calculateLead(tower.currentTarget);
        float dxLead = lead[0] - centerX;
        float dyLead = lead[1] - centerY;
        float leadAngle = (float) Math.atan2(dyLead, dxLead);

        float angleDiff = AimingHelper.shortestAngleDiff(tower.gunAngle, leadAngle);
        float shootThreshold = (float) Math.toRadians(10);

        return Math.abs(angleDiff) <= shootThreshold;
    }

    public static Bullet shoot(Tower tower, Enemy target) {
        float centerX = tower.xPos + GameWorld.cellSize / 2f;
        float centerY = tower.yPos + GameWorld.cellSize / 2f;

        float[] lead = tower.calculateLead(target);
        float dxLead = lead[0] - centerX;
        float dyLead = lead[1] - centerY;
        float leadAngle = (float) Math.atan2(dyLead, dxLead);
        float angleDiff = AimingHelper.shortestAngleDiff(tower.gunAngle, leadAngle);
        float shootThreshold = (float) Math.toRadians(10);

        if (Math.abs(angleDiff) > shootThreshold) return null;

        float angle = tower.gunAngle;
        float maxDeviation = (1 - tower.accuracy) * (float) Math.PI / 6f;
        float deviation = (float) ((Math.random() * 2 - 1) * maxDeviation);
        angle += deviation;

        return new Bullet(centerX, centerY, angle, tower.damage);
    }
}
