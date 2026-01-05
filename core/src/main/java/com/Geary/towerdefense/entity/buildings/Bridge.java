package com.Geary.towerdefense.entity.buildings;

import com.Geary.towerdefense.Direction;

import java.util.EnumSet;

public class Bridge extends Transport {

    public Bridge(float x, float y, EnumSet<Direction> directions) {
        super(x, y, directions);
    }
}
