package com.Geary.towerdefense.entity;

import com.Geary.towerdefense.entity.mob.Enemy;
import com.Geary.towerdefense.world.GameWorld;

import java.util.List;

public class Tower {
    public float xPos, yPos;
    public float range = 207f; // pixels
    public float maxCooldown = 0.1f;
    public float cooldown = maxCooldown;
    private int damage = 1;
    private float accuracy = 0.7f; // 0 = always miss, 1 = perfect accuracy

    // NEW: track the current enemy target
    public Enemy currentTarget = null;

    // NEW: current gun angle in radians
    public float gunAngle = 0f;

    public Tower(float x, float y) {
        this.xPos = x;
        this.yPos = y;
    }

    // --- Find the closest enemy in range ---
    public Enemy findTarget(List<Enemy> enemies) {
        Enemy closest = null;
        float closestDistance = Float.MAX_VALUE;

        float centerX = xPos + GameWorld.cellSize / 2f;
        float centerY = yPos + GameWorld.cellSize / 2f;

        for (Enemy e : enemies) {
            float dx = e.getCenterX() - centerX;
            float dy = e.getCenterY() - centerY;
            float dist = dx * dx + dy * dy; // squared distance

            if (dist <= range * range && dist < closestDistance) {
                closestDistance = dist;
                closest = e;
            }
        }
        return closest;
    }

    public Enemy findTargetFurthestProgressed(List<Enemy> enemies) {
        Enemy best = null;
        float mostProgressed = 0;

        float centerX = xPos + GameWorld.cellSize / 2f;
        float centerY = yPos + GameWorld.cellSize / 2f;

        for (Enemy e : enemies) {
            float dx = e.getCenterX() - centerX;
            float dy = e.getCenterY() - centerY;
            float distSqu = dx * dx + dy * dy; // squared distance

            float totalProgress = e.pathIndex+e.tileProgress;

            if (distSqu <= (range * range) && totalProgress < mostProgressed) {
                mostProgressed = totalProgress;
                best = e;
            }
        }
        return best;
    }

    public Bullet shoot(Enemy target) {
        float centerX = xPos + GameWorld.cellSize / 2f;
        float centerY = yPos + GameWorld.cellSize / 2f;
        float[] lead = calculateLead(target);
        float dxLead = lead[0] - centerX;
        float dyLead = lead[1] - centerY;
        float leadAngle = (float) Math.atan2(dyLead, dxLead);
        float angleDiff = shortestAngleDiff(gunAngle, leadAngle);
        float shootThreshold = (float) Math.toRadians(10); // 10 degrees
        if (Math.abs(angleDiff) > shootThreshold) {
            return null;
        }

        float angle = gunAngle;

        float maxDeviation = (1 - accuracy) * (float)Math.PI / 6f;
        float deviation = (float)((Math.random() * 2 - 1) * maxDeviation);
        angle += deviation;

        return new Bullet(centerX, centerY, angle, damage);
    }


    // --- Distance helper ---
    public float getDistanceTo(Enemy enemy) {
        float dx = (xPos + GameWorld.cellSize / 2f) - enemy.getCenterX();
        float dy = (yPos + GameWorld.cellSize / 2f) - enemy.getCenterY();
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    // Check if tower is aligned enough to fire at the target
    public boolean canShoot() {
        if (currentTarget == null) return false;

        float centerX = xPos + GameWorld.cellSize / 2f;
        float centerY = yPos + GameWorld.cellSize / 2f;

        float[] lead = calculateLead(currentTarget);
        float dxLead = lead[0] - centerX;
        float dyLead = lead[1] - centerY;
        float leadAngle = (float) Math.atan2(dyLead, dxLead);

        float angleDiff = shortestAngleDiff(gunAngle, leadAngle);
        float shootThreshold = (float) Math.toRadians(10); // 10 degrees

        return Math.abs(angleDiff) <= shootThreshold;
    }


    public float[] calculateLead(Enemy e) {
        float sx = xPos + GameWorld.cellSize / 2f;
        float sy = yPos + GameWorld.cellSize / 2f;

        float ex = e.getCenterX();
        float ey = e.getCenterY();

        float vx = e.vx;
        float vy = e.vy;

        float dx = ex - sx;
        float dy = ey - sy;

        float a = vx*vx + vy*vy - Bullet.SPEED*Bullet.SPEED;
        float b = 2 * (dx*vx + dy*vy);
        float c = dx*dx + dy*dy;

        float discriminant = b*b - 4*a*c;
        if (discriminant < 0) return new float[]{ex, ey}; // can't hit, aim at current

        float sqrtDisc = (float)Math.sqrt(discriminant);
        float t1 = (-b + sqrtDisc) / (2*a);
        float t2 = (-b - sqrtDisc) / (2*a);
        float t = Math.max(t1, t2);
        if (t < 0) t = Math.min(t1, t2);
        if (t < 0) return new float[]{ex, ey};

        float aimX = ex + vx * t;
        float aimY = ey + vy * t;

        return new float[]{aimX, aimY};
    }

    public void updateGunAngle(float delta) {
        if (currentTarget == null) return;

        float centerX = xPos + GameWorld.cellSize / 2f;
        float centerY = yPos + GameWorld.cellSize / 2f;

        // Compute desired angle to target (or lead point)
        float[] lead = calculateLead(currentTarget);
        float dx = lead[0] - centerX;
        float dy = lead[1] - centerY;
        float desiredAngle = (float)Math.atan2(dy, dx);

        // Gradually rotate toward desiredAngle
        float rotationSpeed = 1f; // radians per second
        float diff = shortestAngleDiff(gunAngle, desiredAngle);

        // Clamp rotation to rotationSpeed * delta
        float maxStep = rotationSpeed * delta;
        if (diff > maxStep) diff = maxStep;
        if (diff < -maxStep) diff = -maxStep;

        gunAngle += diff;
    }

    private float shortestAngleDiff(float from, float to) {
        float diff = to - from;
        while (diff < -Math.PI) diff += 2*Math.PI;
        while (diff >  Math.PI) diff -= 2*Math.PI;
        return diff;
    }
}
