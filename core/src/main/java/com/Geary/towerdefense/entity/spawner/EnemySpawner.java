package com.Geary.towerdefense.entity.spawner;

import com.Geary.towerdefense.entity.mob.enemy.Enemy;
import com.Geary.towerdefense.entity.mob.enemy.Groblin;
import com.badlogic.gdx.graphics.Color;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.random;

public class EnemySpawner extends Spawner {

    public float maxCooldown = 1.2f;
    public float cooldown = maxCooldown;

    public EnemySpawner(float x, float y) {
        super(x+BUFFER, y+BUFFER);
        this.name="Enemy spawner";
    }

    @Override
    protected Color getColor() {
        return Color.RED;
    }

    public Enemy spawn() {
        return new Groblin(
            getCenterX() - (int) (random() * 14),
            getCenterY() - (int) (random() * 14)
        ) {
        };
    }

    public List<Enemy> deathRattleSpawns(){
        List<Enemy> deathrattleSpawns = new ArrayList<>();
        for (int i=0; i< 20; i++){
            deathrattleSpawns.add(spawn());
        }
        return deathrattleSpawns;
    }
}
