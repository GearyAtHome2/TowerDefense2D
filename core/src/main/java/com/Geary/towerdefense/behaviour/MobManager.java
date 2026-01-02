package com.Geary.towerdefense.behaviour;

import com.Geary.towerdefense.entity.mob.Enemy;
import com.Geary.towerdefense.entity.mob.Friendly;
import com.Geary.towerdefense.entity.mob.Mob;
import com.Geary.towerdefense.world.GameWorld;

import java.util.ArrayList;
import java.util.List;

public class MobManager {
    private final GameWorld world;

    public MobManager(GameWorld world) {
        this.world = world;
    }

    public void update(float delta) {
        List<Mob> mobs = new ArrayList<>();
        mobs.addAll(world.friends);
        mobs.addAll(world.enemies);
        for (Enemy e : world.enemies) {
            e.update(delta);
        }
        for (Friendly f : world.friends) {
            f.update(delta);
        }

        world.enemies.removeIf(e -> e.health <= 0);
        world.friends.removeIf(e -> e.health <= 0);

        // Update bullets and remove finished ones
        world.bullets.removeIf(b -> !b.update(delta, world.enemies));
    }
}
