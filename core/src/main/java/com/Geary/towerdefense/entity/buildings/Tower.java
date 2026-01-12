package com.Geary.towerdefense.entity.buildings;

import com.Geary.towerdefense.behaviour.targeting.AimingHelper;
import com.Geary.towerdefense.behaviour.targeting.ShootingHelper;
import com.Geary.towerdefense.behaviour.targeting.TargetingHelper;
import com.Geary.towerdefense.entity.mob.bullet.BasicBullet;
import com.Geary.towerdefense.entity.mob.bullet.Bullet;
import com.Geary.towerdefense.entity.mob.enemy.Enemy;
import com.Geary.towerdefense.world.GameWorld;
import com.badlogic.gdx.graphics.Color;

import java.util.List;

public class Tower extends Building {

    // Tower stats
    public float range = 207f; // pixels
    public float maxCooldown = 0.1f; // seconds
    public float cooldown = maxCooldown;
    public int damage = 1;
    public float accuracy = 0.7f; // 0 = always miss, 1 = perfect accuracy

    // Current target and gun angle
    public Enemy currentTarget = null;
    public float gunAngle = (float) Math.PI / 2f;

    // Prototype bullet; used to create new bullets when shooting
    public Bullet selectedAmmo;

    public Tower(float x, float y) {
        super(x, y);
        // Default bullet type (prototype)
        this.selectedAmmo = new BasicBullet(0,0,0,0);
        this.name = "Tower";
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
        return isConnectedToNetwork && ShootingHelper.canShoot(this);
    }

    public Bullet shoot(Enemy target) {
        if (target == null || selectedAmmo == null) return null;
        return ShootingHelper.shoot(this, target);
    }

    // --- Gun rotation ---
    public void updateGunAngle(float delta) {
        AimingHelper.updateGunAngle(this, delta);
    }

    // --- Utility ---
    public float getDistanceTo(Enemy enemy) {
        float dx = (xPos + GameWorld.cellSize / 2f) - enemy.getCenterX();
        float dy = (yPos + GameWorld.cellSize / 2f) - enemy.getCenterY();
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    // --- Lead calculation ---
    public float[] calculateLead(Enemy e) {
        float startX = xPos + GameWorld.cellSize / 2f;
        float startY = yPos + GameWorld.cellSize / 2f;

        float targetX = e.getCenterX();
        float targetY = e.getCenterY();

        float targetVX = e.vx;
        float targetVY = e.vy;

        float dx = targetX - startX;
        float dy = targetY - startY;

        float a = targetVX * targetVX + targetVY * targetVY - selectedAmmo.getSpeed() * selectedAmmo.getSpeed();
        float b = 2f * (dx * targetVX + dy * targetVY);
        float c = dx * dx + dy * dy;

        float discriminant = b * b - 4f * a * c;
        if (discriminant < 0f) return new float[]{targetX, targetY};

        float sqrtDisc = (float) Math.sqrt(discriminant);
        float t1 = (-b + sqrtDisc) / (2f * a);
        float t2 = (-b - sqrtDisc) / (2f * a);

        float t = Math.max(t1, t2);
        if (t < 0f) t = Math.min(t1, t2);
        if (t < 0f) return new float[]{targetX, targetY};

        float aimX = targetX + targetVX * t;
        float aimY = targetY + targetVY * t;

        return new float[]{aimX, aimY};
    }

    // --- UI Info ---
    @Override
    public List<String> getInfoLines() {
        return List.of(
            this.name,
            "Cooldown: " + (int) Math.ceil(cooldown * 10f),
            "Range: " + range
        );
    }

    @Override
    public Color getInfoTextColor() {
        return Color.GOLDENROD;
    }

}
