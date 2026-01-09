package com.Geary.towerdefense.behaviour;

import com.Geary.towerdefense.entity.mob.Bullet;
import com.Geary.towerdefense.entity.mob.Enemy;
import com.Geary.towerdefense.entity.mob.Friendly;
import com.Geary.towerdefense.entity.mob.Mob;
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

        handleCollisions();

        removeDeadMobs(world.enemies);
        removeDeadMobs(world.friends);

        world.bullets.removeIf(b -> !b.update(delta, world.enemies));
    }

    private void handleCollisions() {
        for (Enemy enemy : world.enemies) {
            if (!enemy.isAlive()) continue;

            for (Friendly friendly : world.friends) {
                if (!friendly.isAlive()) continue;

                if (overlaps(enemy, friendly)) {
                    if (enemy.collisionCooldown <= 0 && friendly.collisionCooldown <= 0) {
                        int tmpEnemyHealth = enemy.damage;
                        enemy.applyDamage(friendly.damage);
                        friendly.applyDamage(tmpEnemyHealth);

                        applyBounce(friendly, enemy);

                        enemy.collisionCooldown = 0.1f;
                        friendly.collisionCooldown = 0.1f;
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

    /** Checks if two mobs are overlapping */
    private boolean overlaps(Mob a, Mob b) {
        float ax = a.x + a.collisionRadius;
        float ay = a.y + a.collisionRadius;
        float bx = b.x + b.collisionRadius;
        float by = b.y + b.collisionRadius;
        float dx = ax - bx;
        float dy = ay - by;
        float r = a.collisionRadius + b.collisionRadius;
        return dx * dx + dy * dy <= r * r;
    }

    private void applyBounce(Friendly friendly, Enemy enemy) {
        float strength = 8f;//25 is very high

        friendly.pathImpulse -= strength;
        enemy.pathImpulse -= strength;
    }


    //todo: for kb bullets in future. Make sure I keep this relatively small to avoid pushing things out of lanes?
    private void applyKnockback(Mob a, Bullet b) {
        float ax = a.getCenterX();
        float ay = a.getCenterY();
        float bx = b.getCenterX();
        float by = b.getCenterY();

        float dx = ax - bx;
        float dy = ay - by;

        float len = (float) Math.sqrt(dx * dx + dy * dy);
        if (len == 0) return;

        dx /= len;
        dy /= len;

        float strength = 120f; // tune

        a.kbX += dx * strength;
        a.kbY += dy * strength;
    }

}
