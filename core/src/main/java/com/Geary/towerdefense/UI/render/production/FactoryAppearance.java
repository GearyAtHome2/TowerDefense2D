package com.Geary.towerdefense.UI.render.production;

import com.badlogic.gdx.graphics.Color;

public final class FactoryAppearance {
    public final Color frameConnected;
    public final Color frameDisconnected;
    public final Color frameGhost;

    public final Color gearConnected;
    public final Color gearDisconnected;
    public final Color gearGhost;

    public final float[][] gears;

    public FactoryAppearance(
        Color frameConnected,
        Color frameDisconnected,
        Color frameGhost,
        Color gearConnected,
        Color gearDisconnected,
        Color gearGhost,
        float[][] gears
    ) {
        this.frameConnected = frameConnected;
        this.frameDisconnected = frameDisconnected;
        this.frameGhost = frameGhost;
        this.gearConnected = gearConnected;
        this.gearDisconnected = gearDisconnected;
        this.gearGhost = gearGhost;
        this.gears = gears;
    }
}
