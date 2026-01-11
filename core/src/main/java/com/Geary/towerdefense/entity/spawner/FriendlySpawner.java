package com.Geary.towerdefense.entity.spawner;

import com.Geary.towerdefense.entity.mob.friendly.Friendly;
import com.Geary.towerdefense.entity.mob.friendly.Serf;
import com.badlogic.gdx.graphics.Color;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.random;

public class FriendlySpawner extends Spawner {
    //to be removed
    public float maxCooldown = 1.2f;
    public float cooldown = maxCooldown;

    public FriendlySpawner(float x, float y) {
        super(x, y);
        isConnectedToNetwork = true;
        this.name="Friendly spawner";
    }

    public Friendly spawn() {
        return new Serf(
            getCenterX() - (int) (random()*14),
            getCenterY() - (int) (random()*14)
        );
    }

    @Override
    protected Color getColor() {
        return Color.GREEN;
    }

    public List<Friendly> deathRattleSpawns(){
        List<Friendly> deathrattleSpawns = new ArrayList<>();
        for (int i=0; i< 20; i++){
            deathrattleSpawns.add(spawn());
        }
        return deathrattleSpawns;
    }
}
