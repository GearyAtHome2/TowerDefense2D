package com.Geary.towerdefense.levelSelect;

import com.Geary.towerdefense.entity.Entity;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.EnumMap;

public class LevelGridCell {

    public final int xIndex;
    public final int yIndex;

    private final EnumMap<Entity.Order, Float> orderInfluence;
    private boolean isPath = false;
    private boolean isLevel = false;
    public LevelData levelData;

    private TextureRegion cachedIcon;

    // Multi-tile support
    private LevelGridCell parentLevelCell; // bottom-left anchor for multi-tile level
    private int regionWidth = 1;
    private int regionHeight = 1;


    public LevelGridCell(int x, int y) {
        this.xIndex = x;
        this.yIndex = y;
        orderInfluence = new EnumMap<>(Entity.Order.class);
        for (Entity.Order o : Entity.Order.values()) {
            orderInfluence.put(o, 0f);
        }
        this.parentLevelCell = this; // default parent is self
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
        this.parentLevelCell = this;
        this.regionWidth = 3;
        this.regionHeight = 3;
    }
    public boolean isLevel() { return isLevel; }

    public Entity.Order getPrimaryOrder() {
        return getParentLevelCell().levelData.getPrimaryOrder();
    }

    public void setCachedIcon(TextureRegion icon) {
        this.cachedIcon = icon;
    }
    public TextureRegion getCachedIcon() { return cachedIcon; }

    public int getX() { return xIndex; }
    public int getY() { return yIndex; }

    // --- Multi-tile helpers ---
    public void setRegion(LevelGridCell parent, int width, int height) {
        this.parentLevelCell = parent;
        this.regionWidth = width;
        this.regionHeight = height;
        this.isLevel = true;
    }

    public void setRegion(int width, int height) {
        this.regionWidth = width;
        this.regionHeight = height;
    }

    public LevelGridCell getParentLevelCell() { return parentLevelCell; }
    public int getRegionWidth() { return regionWidth; }
    public int getRegionHeight() { return regionHeight; }

    // bottom-left X of the level region
    public int getRegionX() {
        return parentLevelCell.getX();
    }

    // bottom-left Y of the level region
    public int getRegionY() {
        return parentLevelCell.getY();
    }

    // exit X depending on path direction
    public int getRegionExitX(int dirX) {
        return dirX > 0 ? getRegionX() + regionWidth - 1 : getRegionX();
    }

    // exit Y (for now, vertical center of region)
    public int getRegionExitY() {
        return getRegionY() + regionHeight / 2;
    }

}
