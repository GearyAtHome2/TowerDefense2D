package com.Geary.towerdefense.entity.mob;

import com.badlogic.gdx.graphics.Texture;

public class Enemy extends Mob {

    public Enemy(float startX, float startY) {
        super(startX, startY, new Texture("enemy.png"));
        this.useCustomTurnLogic = true;
    }
}
