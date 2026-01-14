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
                spawnEnemy(enemySpawner.spawn());
                enemySpawner.cooldown += enemySpawner.maxCooldown;//todo: undo this for spawning
            }
        }
        for (FriendlySpawner friendSpawner : world.friendlySpawners) {
            if (friendSpawner.canSpawn()) {
                spawnFriendly((Friendly) friendSpawner.spawn());
            }
        }
        removeDeadSpawners();
    }

    public void spawnFriendly(Friendly friendly){
        friendly.setPath(world.path, cellSize, true);
        world.friends.add(friendly);
    }

    public void spawnEnemy(Enemy enemy){
        enemy.setPath(world.path, cellSize, false);
        world.enemies.add(enemy);
    }

    public void removeDeadSpawners(){
        world.friendlySpawners.removeIf(spawner -> {
            if (spawner.health < 1) {
                spawner.deathRattleSpawns().forEach(this::spawnFriendly);
                return true;
            }
            return false;
        });

        world.enemySpawners.removeIf(spawner -> {
            if (spawner.health < 1) {
                spawner.deathRattleSpawns().forEach(this::spawnEnemy);
                return true;
            }
            return false;
        });
    }

}
