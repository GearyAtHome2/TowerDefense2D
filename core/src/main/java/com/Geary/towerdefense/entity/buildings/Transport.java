package com.Geary.towerdefense.entity.buildings;

import com.Geary.towerdefense.Direction;

import java.util.EnumSet;

public class Transport extends Building {
    public EnumSet<Direction> directions;

    public Transport(float x, float y, EnumSet<Direction> directions) {
        super(x, y);
        this.directions = directions;
    }

    public void updateAnimationState(float delta) {
        animationState += delta;
        if (animationState > 1) animationState--;
    }
}
