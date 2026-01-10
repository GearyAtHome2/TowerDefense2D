package com.Geary.towerdefense.entity.spawner;

import com.Geary.towerdefense.entity.mob.enemy.Enemy;
import com.Geary.towerdefense.entity.mob.enemy.Groblin;
import com.badlogic.gdx.graphics.Color;

public class EnemySpawner extends Spawner {

    public float maxCooldown = 3f;
    public float cooldown = maxCooldown;

    public EnemySpawner(float x, float y) {
        super(x, y);
    }

    @Override
    protected Color getColor() {
        return Color.RED;
    }

    public Enemy spawn() {
        return new Groblin(
            getCenterX() - 7,
            getCenterY() - 7
        ) {
        };
    }
}
