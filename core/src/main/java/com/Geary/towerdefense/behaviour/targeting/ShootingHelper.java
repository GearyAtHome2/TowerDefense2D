package com.Geary.towerdefense.behaviour.targeting;

import com.Geary.towerdefense.entity.buildings.tower.Tower;
import com.Geary.towerdefense.entity.mob.bullet.Bullet;
import com.Geary.towerdefense.entity.mob.enemy.Enemy;

public class ShootingHelper {


    public static boolean canShoot(Tower tower) {
        if (tower.currentTarget == null) return false;

        float centerX = tower.getCentreX();
        float centerY = tower.getCentreY();

        float[] lead = tower.calculateLead(tower.currentTarget);
        float dxLead = lead[0] - centerX;
        float dyLead = lead[1] - centerY;
        float leadAngle = (float) Math.atan2(dyLead, dxLead);

        float angleDiff = AimingHelper.shortestAngleDiff(tower.gunAngle, leadAngle);
        float shootThreshold = (float) Math.toRadians(25);

        return Math.abs(angleDiff) <= shootThreshold;
    }

    public static Bullet shoot(Tower tower, Enemy target, Bullet ammo) {
        float centerX = tower.getCentreX();
        float centerY = tower.getCentreY();

        float[] lead = tower.calculateLead(target);
        float deltaX = lead[0] - centerX;
        float deltaY = lead[1] - centerY;

        float leadAngle = (float) Math.atan2(deltaY, deltaX);
        float angleDiff = AimingHelper.shortestAngleDiff(tower.gunAngle, leadAngle);
        float shootThreshold = (float) Math.toRadians(25);
        if (Math.abs(angleDiff) > shootThreshold) return null;

        float angle = tower.gunAngle;
        float maxDeviation = (1f - tower.accuracy) * (float) Math.PI / 6f;
        angle += (Math.random() * 2f - 1f) * maxDeviation;
        return tower.selectedAmmo.createInstance(
            tower.getCentreX(),
            tower.getCentreY(),
            angle
        );
    }
}
