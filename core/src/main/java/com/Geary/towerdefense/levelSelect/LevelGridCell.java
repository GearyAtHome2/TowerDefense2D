package com.Geary.towerdefense.levelSelect;

import com.Geary.towerdefense.entity.Entity;

import java.util.EnumMap;

public class LevelGridCell {

    public final int xIndex;
    public final int yIndex;

    private final EnumMap<Entity.Order, Float> orderInfluence;

    // --- NEW for path / level ---
    private boolean isPath = false;
    private boolean isLevel = false;
    public LevelData levelData;

    public LevelGridCell(int x, int y) {
        this.xIndex = x;
        this.yIndex = y;
        orderInfluence = new EnumMap<>(Entity.Order.class);
        for (Entity.Order o : Entity.Order.values()) {
            orderInfluence.put(o, 0f);
        }
    }

    // --- Cluster influence ---
    public void addInfluence(Entity.Order order, float amount) {
        float current = orderInfluence.get(order);
        current += amount;
        if (current > 1f) current = 1f;
        orderInfluence.put(order, current);
    }

    public float getInfluence(Entity.Order order) {
        return orderInfluence.get(order);
    }

    public Entity.Order getDominantOrder() {
        Entity.Order dominant = Entity.Order.NEUTRAL;
        float max = 0f;
        for (Entity.Order o : Entity.Order.values()) {
            float val = orderInfluence.get(o);
            if (val > max) {
                max = val;
                dominant = o;
            }
        }
        return dominant;
    }

    public Entity.Order getSecondDominantOrder() {
        Entity.Order dominant = Entity.Order.NEUTRAL;
        Entity.Order secondDominant = Entity.Order.NEUTRAL;
        float max = Float.NEGATIVE_INFINITY;
        float secondMax = Float.NEGATIVE_INFINITY;

        for (Entity.Order o : Entity.Order.values()) {
            float val = orderInfluence.getOrDefault(o, 0f);

            if (val > max) {
                // Shift the previous max down to second max
                secondMax = max;
                secondDominant = dominant;

                max = val;
                dominant = o;
            } else if (val > secondMax && o != dominant) {
                secondMax = val;
                secondDominant = o;
            }
        }

        return secondDominant;
    }

    public EnumMap<Entity.Order, Float> getOrderInfluences() {
        return orderInfluence;
    }

    // --- Path / Level ---
    public void setPath() { isPath = true; }
    public boolean isPath() { return isPath; }

    public void setLevel(LevelData levelData) {
        this.levelData = levelData;
        this.isLevel = true;
    }
    public boolean isLevel() { return isLevel; }

    public Entity.Order getPrimaryOrder() { return levelData.getPrimaryOrder(); }

}
