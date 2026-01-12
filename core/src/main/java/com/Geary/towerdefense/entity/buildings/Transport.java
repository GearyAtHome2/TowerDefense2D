package com.Geary.towerdefense.entity.buildings;

import com.Geary.towerdefense.Direction;

import java.util.EnumSet;
import java.util.List;

public class Transport extends Building {
    public EnumSet<Direction> directions;

    public Transport(float x, float y, EnumSet<Direction> directions) {
        super(x, y);
        this.directions = directions;
        this.name = "Transport";
    }

    public void updateAnimationState(float delta) {
        animationState += delta;
        if (animationState > 1) animationState--;
    }

    @Override
    public List<String> getInfoLines() {
        String networkConnectivity = isConnectedToNetwork? "Connected" : "Not connected to network";
        return List.of(
            this.name,
            networkConnectivity
        );
    }
}
