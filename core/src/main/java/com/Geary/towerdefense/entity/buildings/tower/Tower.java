package com.Geary.towerdefense.entity.buildings.tower;

import com.Geary.towerdefense.behaviour.targeting.AimingHelper;
import com.Geary.towerdefense.behaviour.targeting.ShootingHelper;
import com.Geary.towerdefense.behaviour.targeting.TargetingHelper;
import com.Geary.towerdefense.entity.buildings.Building;
import com.Geary.towerdefense.entity.mob.bullet.Bullet;
import com.Geary.towerdefense.entity.mob.enemy.Enemy;
import com.badlogic.gdx.graphics.Color;

import java.util.ArrayList;
import java.util.List;

public abstract class Tower extends Building implements Cloneable {

    // Tower stats
    public float range; // pixels
    public float maxCooldown; // seconds
    public float cooldown = maxCooldown;
    public float accuracy; // 0 = always miss, 1 = perfect accuracy
    public int simultShots = 1;
    public int burst = 0;
    public List<Bullet> supportedAmmo;
    public Bullet selectedAmmo;

    protected enum TargetingStrategy {
        CLOSEST,
        FURTHEST_PROGRESSED
    }

    public TargetingStrategy targetingStrategy = TargetingStrategy.CLOSEST;
    public Enemy currentTarget = null;
    public float gunAngle = (float) Math.PI / 2f;

    public Tower(float x, float y, String name, Bullet ammo, float maxCooldown, float accuracy, float range) {
        super(x, y);
        this.name = name;
        this.selectedAmmo = ammo;
        this.maxCooldown = maxCooldown;
        this.accuracy = accuracy;
        this.range = range;
    }

    public void setSupportedAmmo(List<Bullet> supportedAmmo){
        this.supportedAmmo = supportedAmmo;
    }

    @Override
    public Tower clone() {
        try {
            return (Tower) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(); // can't happen
        }
    }

    public Enemy findTarget(List<Enemy> enemies){
        return switch (targetingStrategy) {
            case CLOSEST -> findClosestTarget(enemies);
            case FURTHEST_PROGRESSED -> findTargetFurthestProgressed(enemies);
        };
    }

    // --- Targeting ---
    private Enemy findClosestTarget(List<Enemy> enemies) {
        return TargetingHelper.findClosest(this, enemies);
    }


    private Enemy findTargetFurthestProgressed(List<Enemy> enemies) {
        return TargetingHelper.findFurthestProgressed(this, enemies);
    }

    // --- Shooting / CanShoot ---
    public boolean canShoot() {
        return isConnectedToNetwork && ShootingHelper.canShoot(this);
    }

    public List<Bullet> shoot(Enemy target) {
        if (target == null || selectedAmmo == null) return List.of();
        List<Bullet> bullets = new ArrayList<>();
        for (int i = 0; i < simultShots; i++) {
            Bullet b = ShootingHelper.shoot(this, target, selectedAmmo);
            if (b != null) {
                bullets.add(b);
            }
        }
        return bullets;
    }

    // --- Gun rotation ---
    public void updateGunAngle(float delta) {
        AimingHelper.updateGunAngle(this, delta);
    }

    // --- Utility ---
    public float getDistanceTo(Enemy enemy) {
        float dx = (this.getCentreX()) - enemy.getCenterX();
        float dy = (this.getCentreY()) - enemy.getCenterY();
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    // --- Lead calculation ---
    public float[] calculateLead(Enemy e) {
        float startX = this.getCentreX();
        float startY = this.getCentreY();

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

    public List<Bullet> getSupportedAmmo(){
        return supportedAmmo;
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
