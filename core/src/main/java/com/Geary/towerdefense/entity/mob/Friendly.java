package com.Geary.towerdefense.entity.mob;

import com.badlogic.gdx.graphics.Texture;

public class Friendly extends Mob {

    public Friendly(float startX, float startY) {
        super(startX, startY, new Texture("friendly.png"));
        this.turnMultiplier = -1; // opposite direction
        this.reversed = true;     // path index reversed
    }
}
