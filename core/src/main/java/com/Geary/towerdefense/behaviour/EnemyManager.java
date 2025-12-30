package com.Geary.towerdefense.behaviour;

import com.Geary.towerdefense.entity.Enemy;
import com.Geary.towerdefense.world.GameWorld;

public class EnemyManager {
    private final GameWorld world;

    public EnemyManager(GameWorld world) {
        this.world = world;
    }

    public void update(float delta) {
        // Update enemies
        for (Enemy e : world.enemies) {
            e.update(delta, world.path, world.cellSize);
        }

        // Remove dead enemies
        world.enemies.removeIf(e -> e.health <= 0);

        // Update bullets and remove finished ones
        world.bullets.removeIf(b -> !b.update(delta));
    }
}
