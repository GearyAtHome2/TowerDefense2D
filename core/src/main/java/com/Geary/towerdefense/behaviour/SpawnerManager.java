package com.Geary.towerdefense.behaviour;

import com.Geary.towerdefense.entity.Spawner;
import com.Geary.towerdefense.world.GameWorld;

public class SpawnerManager {
    private final GameWorld world;

    public SpawnerManager(GameWorld world) {
        this.world = world;
    }

    public void update(float delta) {
        for (Spawner spawner : world.spawners) {
            spawner.cooldown -= delta;
            if (spawner.cooldown <= 0) {
                world.enemies.add(spawner.spawn());
                spawner.cooldown += spawner.maxCooldown;//todo: undo this for spawning
            }
        }
    }
}
