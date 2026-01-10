package com.Geary.towerdefense.behaviour;

import com.Geary.towerdefense.entity.mob.enemy.Enemy;
import com.Geary.towerdefense.entity.mob.friendly.Friendly;
import com.Geary.towerdefense.entity.spawner.EnemySpawner;
import com.Geary.towerdefense.entity.spawner.FriendlySpawner;
import com.Geary.towerdefense.world.GameWorld;

import static com.Geary.towerdefense.world.GameWorld.cellSize;

public class SpawnerManager {
    private final GameWorld world;

    public SpawnerManager(GameWorld world) {
        this.world = world;
    }

    public void update(float delta) {
        for (EnemySpawner enemySpawner : world.enemySpawners) {
            enemySpawner.cooldown -= delta;
            if (enemySpawner.cooldown <= 0) {
                Enemy enemy = enemySpawner.spawn();
                enemy.setPath(world.path, cellSize, false);
                world.enemies.add(enemy);
                enemySpawner.cooldown += enemySpawner.maxCooldown;//todo: undo this for spawning
            }
        }
        for (FriendlySpawner friendSpawner : world.friendlySpawners) {
            friendSpawner.cooldown -= delta;
            if (friendSpawner.cooldown <= 0) {
                Friendly friendly = friendSpawner.spawn();
                friendly.setPath(world.path, cellSize, true);
                world.friends.add(friendly);
                friendSpawner.cooldown += friendSpawner.maxCooldown;
            }
        }
    }
}
