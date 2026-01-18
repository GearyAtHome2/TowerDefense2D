package com.Geary.towerdefense.entity.buildings.tower;

import com.Geary.towerdefense.entity.mob.bullet.BasicBullet;
import com.Geary.towerdefense.entity.mob.bullet.Bullet;
import com.badlogic.gdx.graphics.Color;

import java.util.List;

public class ShotgunTower extends Tower {

    // Prototype bullet; used to create new bullets when shooting
    public Bullet selectedAmmo;

    public ShotgunTower(float x, float y) {
        super(x, y, "Shotgun Tower", new BasicBullet(), 0.9f, 0.3f, 175f);
        this.simultShots = 5;
    }

//    // --- Targeting ---
//    public Enemy findTarget(List<Enemy> enemies) {
//        return TargetingHelper.findClosest(this, enemies);
//    }
//
//    public Enemy findTargetFurthestProgressed(List<Enemy> enemies) {
//        return TargetingHelper.findFurthestProgressed(this, enemies);
//    }
//
//    // --- Shooting / CanShoot ---
//    public boolean canShoot() {
//        return isConnectedToNetwork && ShootingHelper.canShoot(this);
//    }
//
//    public Bullet shoot(Enemy target) {
//        if (target == null || selectedAmmo == null) return null;
//        return ShootingHelper.shoot(this, target);
//    }
//
//    // --- Gun rotation ---
//    public void updateGunAngle(float delta) {
//        AimingHelper.updateGunAngle(this, delta);
//    }
//
//    // --- Utility ---
//    public float getDistanceTo(Enemy enemy) {
//        float dx = (xPos) - enemy.getCenterX();
//        float dy = (yPos) - enemy.getCenterY();
//        return (float) Math.sqrt(dx * dx + dy * dy);
//    }
//
//    // --- Lead calculation ---
//    public float[] calculateLead(Enemy e) {
//        float startX = xPos;
//        float startY = yPos;
//
//        float targetX = e.getCenterX();
//        float targetY = e.getCenterY();
//
//        float targetVX = e.vx;
//        float targetVY = e.vy;
//
//        float dx = targetX - startX;
//        float dy = targetY - startY;
//
//        float a = targetVX * targetVX + targetVY * targetVY - selectedAmmo.getSpeed() * selectedAmmo.getSpeed();
//        float b = 2f * (dx * targetVX + dy * targetVY);
//        float c = dx * dx + dy * dy;
//
//        float discriminant = b * b - 4f * a * c;
//        if (discriminant < 0f) return new float[]{targetX, targetY};
//
//        float sqrtDisc = (float) Math.sqrt(discriminant);
//        float t1 = (-b + sqrtDisc) / (2f * a);
//        float t2 = (-b - sqrtDisc) / (2f * a);
//
//        float t = Math.max(t1, t2);
//        if (t < 0f) t = Math.min(t1, t2);
//        if (t < 0f) return new float[]{targetX, targetY};
//
//        float aimX = targetX + targetVX * t;
//        float aimY = targetY + targetVY * t;
//
//        return new float[]{aimX, aimY};
//    }

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
        return Color.CYAN;
    }

}
