package com.Geary.towerdefense.levelSelect.levels;

import com.Geary.towerdefense.entity.Entity;
import com.Geary.towerdefense.entity.mob.enemy.Enemy;
import com.Geary.towerdefense.entity.resources.Resource;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LevelData implements Json.Serializable {

    private String id;
    private String displayName;
    private HashMap<Resource.RawResourceType, Integer> resourceAllocation;
    private Entity.Order primaryOrder;
    private Entity.Order secondaryOrder;

    private String texturePath; // JSON only
    private transient TextureRegion texture; // runtime only
    private int tier;
    private ArrayList<EnemySpawnInfo> enemies;

    public LevelData() {
        this.id = "";
        this.displayName = "";
        this.resourceAllocation = new HashMap<>();
        this.primaryOrder = Entity.Order.NEUTRAL;
        this.secondaryOrder = Entity.Order.NEUTRAL;
        this.tier = 0;
        this.texturePath = null;
        this.texture = null;
        this.enemies = new ArrayList<>();
    }

    public LevelData(String id, String displayName,
                     HashMap<Resource.RawResourceType, Integer> resourceAllocation,
                     Entity.Order primaryOrder, Entity.Order secondaryOrder,
                     String texturePath, ArrayList<EnemySpawnInfo> enemies, int tier) {
        this.id = id;
        this.displayName = displayName;
        this.resourceAllocation = resourceAllocation;
        this.primaryOrder = primaryOrder;
        this.secondaryOrder = secondaryOrder;
        this.texturePath = texturePath;
        this.enemies = enemies;
        this.texture = (texturePath != null) ? new TextureRegion(new Texture(texturePath)) : null;
        this.tier = tier;
    }

    // ----------------- Getters / Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public HashMap<Resource.RawResourceType, Integer> getResourceAllocation() { return resourceAllocation; }
    public void setResourceAllocation(HashMap<Resource.RawResourceType, Integer> resourceAllocation) { this.resourceAllocation = resourceAllocation; }

    public Entity.Order getPrimaryOrder() { return primaryOrder; }
    public void setPrimaryOrder(Entity.Order primaryOrder) { this.primaryOrder = primaryOrder; }

    public Entity.Order getSecondaryOrder() { return secondaryOrder; }
    public void setSecondaryOrder(Entity.Order secondaryOrder) { this.secondaryOrder = secondaryOrder; }

    public String getTexturePath() { return texturePath; }
    public void setTexturePath(String texturePath) { this.texturePath = texturePath; }

    public TextureRegion getTexture() {
        if (texture == null && texturePath != null) {
            texture = new TextureRegion(new Texture(texturePath));
        }
        return texture;
    }
    public void setTexture(TextureRegion texture) { this.texture = texture; }

    public ArrayList<EnemySpawnInfo> getEnemies() { return enemies; }
    public void setEnemies(ArrayList<EnemySpawnInfo> enemies) { this.enemies = enemies; }

    public boolean isMerged() {
        return secondaryOrder != Entity.Order.NEUTRAL;
    }

    public int getTier(){ return tier;}

    // ----------------- JSON Serialization
    @Override
    public void write(Json json) {
        json.writeValue("id", id);
        json.writeValue("displayName", displayName);

        // Convert enum keys to string for JSON
        Map<String, Integer> stringMap = new HashMap<>();
        for (Map.Entry<Resource.RawResourceType, Integer> entry : resourceAllocation.entrySet()) {
            stringMap.put(entry.getKey().name(), entry.getValue());
        }
        json.writeValue("resourceAllocation", stringMap);

        json.writeValue("primaryOrder", primaryOrder.name());
        json.writeValue("secondaryOrder", secondaryOrder.name());
        json.writeValue("texturePath", texturePath);
        json.writeValue("enemies", enemies);
        json.writeValue("tier", tier);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        this.id = jsonData.getString("id", "");
        this.displayName = jsonData.getString("displayName", "");

        // Properly convert JSON keys to enums
        this.resourceAllocation = new HashMap<>();
        JsonValue resMap = jsonData.get("resourceAllocation");
        if (resMap != null) {
            for (JsonValue entry = resMap.child; entry != null; entry = entry.next) {
                Resource.RawResourceType key = Resource.RawResourceType.valueOf(entry.name());
                int value = entry.asInt();
                this.resourceAllocation.put(key, value);
            }
        }

        this.primaryOrder = Entity.Order.valueOf(jsonData.getString("primaryOrder", "NEUTRAL"));
        this.secondaryOrder = Entity.Order.valueOf(jsonData.getString("secondaryOrder", "NEUTRAL"));
        this.texturePath = jsonData.getString("texturePath", null);
        this.enemies = json.readValue("enemies", ArrayList.class, EnemySpawnInfo.class, jsonData);
        this.tier = jsonData.getInt("tier", 0);
    }

    // ----------------- Enemy info
    public static class EnemySpawnInfo {
        private String enemyType;
        private int cost;

        public EnemySpawnInfo() {}
        public EnemySpawnInfo(String enemyType, int cost) { this.enemyType = enemyType; this.cost = cost; }

        public String getEnemyType() { return enemyType; }
        public void setEnemyType(String enemyType) { this.enemyType = enemyType; }

        public int getCost() { return cost; }
        public void setCost(int cost) { this.cost = cost; }

        public Class<? extends Enemy> getEnemyClass() { return EnemyRegistry.getClassByName(enemyType); }
    }
}
