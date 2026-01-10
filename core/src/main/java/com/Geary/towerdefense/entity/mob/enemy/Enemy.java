package com.Geary.towerdefense.entity.mob.enemy;

import com.Geary.towerdefense.entity.mob.Mob;
import com.Geary.towerdefense.entity.mob.MobStats;
import com.badlogic.gdx.graphics.Texture;

public class Enemy extends Mob {

    public Enemy(float startX, float startY, Texture texture, MobStats stats) {
        super(startX, startY, texture, stats);
        this.useCustomTurnLogic = true;
    }
}
