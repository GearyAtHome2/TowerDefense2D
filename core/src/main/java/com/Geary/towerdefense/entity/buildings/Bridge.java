package com.Geary.towerdefense.entity.buildings;

import com.Geary.towerdefense.Direction;

import java.util.EnumSet;
import java.util.List;

public class Bridge extends Transport {

    public Bridge(float x, float y, EnumSet<Direction> directions) {
        super(x, y, directions);
        this.name = "Bridge";
    }

    @Override
    public List<String> getInfoLines() {
        return List.of(
            this.name
        );
    }
}
