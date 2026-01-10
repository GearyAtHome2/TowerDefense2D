package com.Geary.towerdefense.entity.mob.friendly;

import com.Geary.towerdefense.entity.mob.Mob;
import com.Geary.towerdefense.entity.mob.MobStats;
import com.badlogic.gdx.graphics.Texture;

public class Friendly extends Mob {

    public Friendly(float startX, float startY, Texture texture, MobStats stats) {
        super(startX, startY, texture, stats);
        this.turnMultiplier = -1; // opposite direction
        this.reversed = true;     // path index reversed
    }
}
