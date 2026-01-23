package com.Geary.towerdefense.behaviour;

import com.Geary.towerdefense.entity.Entity;
import com.Geary.towerdefense.entity.mob.Mob;
import com.Geary.towerdefense.entity.mob.bullet.Bullet;
import com.Geary.towerdefense.entity.mob.enemy.Enemy;
import com.Geary.towerdefense.entity.mob.friendly.Friendly;
import com.Geary.towerdefense.entity.spawner.EnemySpawner;
import com.Geary.towerdefense.entity.spawner.FriendlySpawner;
import com.Geary.towerdefense.entity.spawner.Spawner;
import com.Geary.towerdefense.world.GameWorld;

public class MobManager {

    private final GameWorld world;
    private final SparkManager sparkManager;

    public MobManager(GameWorld world, SparkManager sparkManager) {
        this.world = world;
        this.sparkManager = sparkManager;
    }

    public void update(float delta) {
        for (Enemy e : world.enemies) e.update(delta);
        for (Friendly f : world.friends) f.update(delta);

        handleMobCollisions();
        handleSpawnerHit();

        removeDeadMobs(world.enemies);
        removeDeadMobs(world.friends);

        if (!world.bullets.isEmpty()){
        world.bullets.removeIf(b -> !b.update(delta, world.enemies));}
    }

    private void handleSpawnerHit() {
        for (Enemy enemy : world.enemies) {
            if (!enemy.isAlive()) continue;
            if (world.friendlySpawners.isEmpty()) continue;
            //for now - will want to loop over them in future?

            FriendlySpawner friendlySpawner = world.friendlySpawners.get(0);
            if (overlaps(enemy, friendlySpawner)) {
                friendlySpawner.applyDamage(enemy.damage);
                enemy.health = 0;
            }
        }
        for (Friendly friendly : world.friends) {
            if (!friendly.isAlive()) continue;
            if (world.enemySpawners.isEmpty()) continue;
            //for now - will want to loop over them in future?
            EnemySpawner enemySpawner = world.enemySpawners.get(0);
            if (overlaps(friendly, enemySpawner)) {
                enemySpawner.applyDamage(friendly.damage);
                friendly.health = 0;
            }
        }
    }

    private void handleMobCollisions() {
        for (Enemy enemy : world.enemies) {
            if (!enemy.isAlive()) continue;

            for (Friendly friendly : world.friends) {
                if (!friendly.isAlive()) continue;

                if (overlaps(enemy, friendly)) {
                    if (enemy.collisionCooldown <= 0 || friendly.collisionCooldown <= 0) {
                        enemy.applyDamage(friendly.damage);
                        friendly.applyDamage(enemy.damage);

                        applyBounce(friendly, enemy);
                        enemy.collisionCooldown = 0.04f;//keep an eye on this. in theory as long as it's over 1/60 should be good?
                        friendly.collisionCooldown = 0.04f;//1/60 ~= 0.0167
                    }
                }
            }
        }
    }

    private void removeDeadMobs(java.util.List<? extends Mob> mobs) {
        mobs.removeIf(m -> {
            if (m.health <= 0) {
                sparkManager.spawn(m.getCenterX(), m.getCenterY());
                return true;
            }
            return false;
        });
    }

    //todo: this doesn't seem to work when I pass it two mobs?
    private boolean overlaps(Entity a, Spawner b) {
        float ax = a.xPos + a.collisionRadius;
        float ay = a.yPos + a.collisionRadius;
        float bx = b.xPos + b.collisionRadius;
        float by = b.yPos + b.collisionRadius;
        float dx = ax - bx;
        float dy = ay - by;
        float r = a.collisionRadius + b.collisionRadius;
        return dx * dx + dy * dy <= r * r;
    }

    /** Checks if two mobs are overlapping */
    private boolean overlaps(Mob a, Mob b) {
        float eps = 0.001f;//tiny buffer to avoid a slight jitter apparently
        return a.xPos < b.xPos + b.size - eps &&
            a.xPos + a.size > b.xPos + eps &&
            a.yPos < b.yPos + b.size - eps &&
            a.yPos + a.size > b.yPos + eps;
    }

    private void applyBounce(Friendly friendly, Enemy enemy) {
        float fx = friendly.getCenterX();
        float fy = friendly.getCenterY();
        float ex = enemy.getCenterX();
        float ey = enemy.getCenterY();
        float dx = fx - ex;
        float dy = fy - ey;

        float len = (float)Math.sqrt(dx*dx + dy*dy);
        if (len == 0) return;

        dx /= len;
        dy /= len;

        float friendlyHitAffected = enemy.knockBackPower/ friendly.weight;
        friendly.vx += dx * friendlyHitAffected;
        friendly.vy += dy * friendlyHitAffected;

        float enemyHitAffected = friendly.knockBackPower/ enemy.weight;
        enemy.vx -= dx * enemyHitAffected;
        enemy.vy -= dy * enemyHitAffected;

    }

    //todo: for kb bullets in future. Make sure I keep this relatively small to avoid pushing things out of lanes?
    private void applyBounce(Mob mob, Bullet bullet) {
        if (bullet.getKnockBack() ==0){

        }
        float fx = mob.getCenterX();
        float fy = mob.getCenterY();
        float ex = bullet.getCenterX();
        float ey = bullet.getCenterY();
        float dx = fx - ex;
        float dy = fy - ey;

        float len = (float)Math.sqrt(dx*dx + dy*dy);
        if (len == 0) return;

        dx /= len;
        dy /= len;

        float mobAffected = bullet.getKnockBack() / mob.weight;
        mob.vx += dx * mobAffected;
        mob.vy += dy * mobAffected;

    }

}
