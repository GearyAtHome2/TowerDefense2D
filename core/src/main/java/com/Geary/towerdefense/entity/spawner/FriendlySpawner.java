package com.Geary.towerdefense.entity.spawner;

import com.Geary.towerdefense.entity.mob.Mob;
import com.Geary.towerdefense.entity.mob.friendly.Friendly;
import com.Geary.towerdefense.entity.mob.friendly.ManAtArms;
import com.Geary.towerdefense.entity.mob.friendly.Serf;
import com.badlogic.gdx.graphics.Color;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.random;

public class FriendlySpawner extends Spawner {
    public float maxCooldown = 1.2f;
    public float cooldown = maxCooldown;

    private final List<Mob> spawnableMobs = new ArrayList<>();

    public FriendlySpawner(float x, float y) {
        super(x, y);
        isConnectedToNetwork = true;
        this.name = "Friendly spawner";

        // safe default: existing Serf
        for (int i=0; i< 7 ; i++) {
            spawnableMobs.add(new Serf(0,0)); // Serf must have a public static MobStats STATS
        }
        for (int i=0; i<7 ; i++) {
            spawnableMobs.add(new ManAtArms(0,0)); // Serf must have a public static MobStats STATS
        }
    }

    @Override
    protected Color getColor() {
        return Color.GREEN;
    }

    public Friendly spawn() {
        return new Serf(
            getCenterX() - (int) (random()*14)+7,
            getCenterY() - (int) (random()*14)+7
        );
    }

    public List<Friendly> deathRattleSpawns() {
        List<Friendly> deathrattleSpawns = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            deathrattleSpawns.add(spawn());
        }
        return deathrattleSpawns;
    }

    /** Minimal new method: let modal query spawnable mobs */
    public List<Mob> getSpawnableMobs() {
        return spawnableMobs;
    }
}
