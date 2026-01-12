package com.Geary.towerdefense.behaviour.targeting;

import com.Geary.towerdefense.entity.buildings.Tower;

public class AimingHelper {

    public static float shortestAngleDiff(float from, float to) {
        float diff = to - from;
        while (diff < -Math.PI) diff += 2 * Math.PI;
        while (diff > Math.PI) diff -= 2 * Math.PI;
        return diff;
    }

    public static float updateGunAngle(Tower tower, float delta) {
        if (tower.currentTarget == null) return tower.gunAngle;

        float centerX = tower.xPos;
        float centerY = tower.yPos;

        float[] lead = tower.calculateLead(tower.currentTarget);
        float dx = lead[0] - centerX;
        float dy = lead[1] - centerY;
        float desiredAngle = (float) Math.atan2(dy, dx);

        float rotationSpeed = 1f; // radians per second
        float diff = shortestAngleDiff(tower.gunAngle, desiredAngle);

        float maxStep = rotationSpeed * delta;
        if (diff > maxStep) diff = maxStep;
        if (diff < -maxStep) diff = -maxStep;

        tower.gunAngle += diff;
        return tower.gunAngle;
    }
}
