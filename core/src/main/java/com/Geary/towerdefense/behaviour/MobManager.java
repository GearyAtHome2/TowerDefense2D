package com.Geary.towerdefense.behaviour;

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
                    int tmpEnemyHealth = enemy.health;
                    enemy.applyDamage(friendly.health);
                    friendly.applyDamage(tmpEnemyHealth);
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
}
