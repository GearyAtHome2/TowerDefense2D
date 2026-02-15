package com.Geary.towerdefense.levelSelect;

import com.Geary.towerdefense.entity.Entity;

public class LevelGridCell {

    public enum Type {
        PATH,
        LEVEL,
        BACKGROUND
    }

    private Entity.Order order;

    private final int gridX;
    private final int gridY;

    private Type type;

    public LevelGridCell(int gridX, int gridY) {
        this.gridX = gridX;
        this.gridY = gridY;
        this.type = Type.BACKGROUND;
        this.order = Entity.Order.NEUTRAL;
    }

    public int getGridX() { return gridX; }
    public int getGridY() { return gridY; }

    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }

    public Entity.Order getOrder() { return order; }
    public void setOrder(Entity.Order order) { this.order = order; }
}
