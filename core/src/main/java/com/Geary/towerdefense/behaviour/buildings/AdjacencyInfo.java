package com.Geary.towerdefense.behaviour.buildings;

public class AdjacencyInfo {
    public boolean north;
    public boolean south;
    public boolean east;
    public boolean west;

    public boolean hasAny() {
        return north || south || east || west;
    }
}
