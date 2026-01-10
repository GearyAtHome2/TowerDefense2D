package com.Geary.towerdefense.entity.buildings;

import com.Geary.towerdefense.behaviour.targeting.AimingHelper;
import com.Geary.towerdefense.behaviour.targeting.ShootingHelper;
import com.Geary.towerdefense.behaviour.targeting.TargetingHelper;
import com.Geary.towerdefense.entity.mob.Bullet;
import com.Geary.towerdefense.entity.mob.enemy.Enemy;
import com.Geary.towerdefense.world.GameWorld;
import com.badlogic.gdx.graphics.Color;

import java.util.List;

public class Tower extends Building {
    public float range = 207f; // pixels
    public float maxCooldown = 0.1f;
    public float cooldown = maxCooldown;
    public int damage = 1;
    public float accuracy = 0.7f; // 0 = always miss, 1 = perfect accuracy

    // NEW: track the current enemy target
    public Enemy currentTarget = null;

    // NEW: current gun angle in radians
    public float gunAngle = (float) Math.PI / 2f;

    public Tower(float x, float y) {
        super(x, y);
    }

    // --- Targeting ---
    public Enemy findTarget(List<Enemy> enemies) {
        return TargetingHelper.findClosest(this, enemies);
    }

    public Enemy findTargetFurthestProgressed(List<Enemy> enemies) {
        return TargetingHelper.findFurthestProgressed(this, enemies);
    }

    // --- Shooting / CanShoot ---
    public boolean canShoot() {
        //possible feature here - some towers should be able to shoot outside of network maybe?
        // Needs a big downside though, maybe some "local network" thing.
        return isConnectedToNetwork && ShootingHelper.canShoot(this);
    }

    public Bullet shoot(Enemy target) {
        return ShootingHelper.shoot(this, target);
    }

    // --- Aiming / Gun rotation ---
    public void updateGunAngle(float delta) {
        AimingHelper.updateGunAngle(this, delta);
    }

    // --- Utility ---
    public float getDistanceTo(Enemy enemy) {
        float dx = (xPos + GameWorld.cellSize / 2f) - enemy.getCenterX();
        float dy = (yPos + GameWorld.cellSize / 2f) - enemy.getCenterY();
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    // --- Lead calculation stays here ---
    public float[] calculateLead(Enemy e) {
        float sx = xPos + GameWorld.cellSize / 2f;
        float sy = yPos + GameWorld.cellSize / 2f;

        float ex = e.getCenterX();
        float ey = e.getCenterY();

        float vx = e.vx;
        float vy = e.vy;

        float dx = ex - sx;
        float dy = ey - sy;

        float a = vx * vx + vy * vy - Bullet.SPEED * Bullet.SPEED;
        float b = 2 * (dx * vx + dy * vy);
        float c = dx * dx + dy * dy;

        float discriminant = b * b - 4 * a * c;
        if (discriminant < 0) return new float[]{ex, ey};

        float sqrtDisc = (float) Math.sqrt(discriminant);
        float t1 = (-b + sqrtDisc) / (2 * a);
        float t2 = (-b - sqrtDisc) / (2 * a);
        float t = Math.max(t1, t2);
        if (t < 0) t = Math.min(t1, t2);
        if (t < 0) return new float[]{ex, ey};

        float aimX = ex + vx * t;
        float aimY = ey + vy * t;

        return new float[]{aimX, aimY};
    }

    @Override
    public List<String> getInfoLines() {
        return List.of(
            "Cooldown: " + (int)Math.ceil(cooldown * 10),
            "Range: " + range
        );
    }

    public Color getInfoTextColor() {
        return Color.GOLDENROD; // default
    }

}
