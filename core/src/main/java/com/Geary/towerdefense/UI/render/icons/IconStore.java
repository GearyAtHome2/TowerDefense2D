package com.Geary.towerdefense.UI.render.icons;

import com.Geary.towerdefense.behaviour.targeting.TargetingHelper;
import com.Geary.towerdefense.entity.Entity;
import com.Geary.towerdefense.entity.resources.Resource;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import javax.swing.*;
import java.util.*;

import static com.Geary.towerdefense.entity.Entity.Order.WATER;

public class IconStore {

    private static final Map<Resource.RawResourceType, TextureRegion> RAW_ICONS = new EnumMap<>(Resource.RawResourceType.class);
    private static final Map<Resource.RefinedResourceType, TextureRegion> REFINED_ICONS = new EnumMap<>(Resource.RefinedResourceType.class);
    private static final Map<String, TextureRegion> MOB_ICONS = new HashMap<>();
    private static final Map<String, TextureRegion> AMMO_ICONS = new HashMap<>();
    private static final Map<Icon, TextureRegion> SYMBOL_ICONS = new EnumMap<>(Icon.class);

    // --- NEW: Shared 3x3 level icons per order ---
    private static final EnumMap<Entity.Order, TextureRegion> LEVEL_3X3_ICONS = new EnumMap<>(Entity.Order.class);

    // --- NEW: Preloaded level select tiles (order -> size -> list of TextureRegions) ---
    private static final EnumMap<Entity.Order, Map<Integer, List<TextureRegion>>> LEVEL_SELECT_TILES = new EnumMap<>(Entity.Order.class);

    public static void load() {
        // --- Existing icon loading ---
        for (Resource.RawResourceType type : Resource.RawResourceType.values()) {
            RAW_ICONS.put(
                type,
                new TextureRegion(new Texture("icons/resources/raw/" + type.name().toLowerCase() + ".png"))
            );
        }

        for (Resource.RefinedResourceType type : Resource.RefinedResourceType.values()) {
            REFINED_ICONS.put(
                type,
                new TextureRegion(new Texture("icons/resources/refined/" + type.name().toLowerCase() + ".png"))
            );
        }

        for (Icon type : Icon.values()) {
            SYMBOL_ICONS.put(
                type,
                new TextureRegion(new Texture("icons/" + type.name().toLowerCase() + ".png"))
            );
        }

        // --- Shared 3x3 level icons ---
        String dir = "mapStructures/level/";
        for (Entity.Order order : Entity.Order.values()) {
            if (order == Entity.Order.NEUTRAL) continue;
            String assetName = "dark_placeholder"; // placeholder for now
            Texture tex = new Texture(dir + assetName + ".png");
            LEVEL_3X3_ICONS.put(order, new TextureRegion(tex));
        }

        // --- Preload all levelSelectTileNxN assets dynamically ---
        for (Entity.Order order : Entity.Order.values()) {
            if (order == Entity.Order.NEUTRAL) continue;

            Map<Integer, List<TextureRegion>> sizeMap = new HashMap<>();
            for (int size = 1; size <= 4; size++) {
                List<TextureRegion> tiles = new ArrayList<>();
                int suffix = 1;

                while (true) {
                    String path = "mapStructures/" + order.name() + "/" + size + "x" + size + "_" + suffix + ".png";

                    FileHandle fh = Gdx.files.internal(path);
                    if (!fh.exists()) break;
                    Texture tex = new Texture(fh);
                    tiles.add(new TextureRegion(tex));
                    suffix++;
                }

                if (!tiles.isEmpty()) sizeMap.put(size, tiles);
            }
            LEVEL_SELECT_TILES.put(order, sizeMap);
        }
    }

    // --- Existing getters ---
    public static TextureRegion getSymbol(Icon symbol) {
        return SYMBOL_ICONS.get(symbol);
    }

    public static TextureRegion targetingStrategy(TargetingHelper.TargetingStrategy strategy) {
        String key = strategy.name().toLowerCase().replace(" ", "_");
        TextureRegion icon = MOB_ICONS.get(key);
        if (icon != null) return icon;
        try {
            Texture texture = new Texture("icons/targetingStrategy/" + key + ".png");
            icon = new TextureRegion(texture);
            MOB_ICONS.put(key, icon);
            return icon;
        } catch (Exception e) {
            System.err.println("Missing mob icon: " + key);
            return null;
        }
    }

    public static TextureRegion rawResource(Resource.RawResourceType type) {
        return RAW_ICONS.get(type);
    }

    public static TextureRegion refinedResource(Resource.RefinedResourceType type) {
        return REFINED_ICONS.get(type);
    }

    public static TextureRegion mob(String mobName) {
        if (mobName == null) return null;
        String key = mobName.toLowerCase().replace(" ", "_");
        TextureRegion icon = MOB_ICONS.get(key);
        if (icon != null) return icon;
        try {
            Texture texture = new Texture("icons/mobs/" + key + ".png");
            icon = new TextureRegion(texture);
            MOB_ICONS.put(key, icon);
            return icon;
        } catch (Exception e) {
            System.err.println("Missing mob icon: " + key);
            return null;
        }
    }

    public static TextureRegion ammo(String ammoName) {
        if (ammoName == null) return null;
        String key = ammoName.toLowerCase().replace(" ", "_");
        TextureRegion icon = AMMO_ICONS.get(key);
        if (icon != null) return icon;
        try {
            Texture texture = new Texture("icons/ammo/" + key + ".png");
            icon = new TextureRegion(texture);
            AMMO_ICONS.put(key, icon);
            return icon;
        } catch (Exception e) {
            System.err.println("Missing ammo icon: " + key);
            return null;
        }
    }

    public static TextureRegion randomMapTileForOrder(Entity.Order order, boolean edge) {
        String dir = "mapStructures/" + order.name() + "/";
        String assetName;
        switch (order) {
            case NATURE -> assetName = "tree";
            case WATER -> assetName = "sea";
            default -> assetName = "default";
        }
        if (edge) assetName += "_border";
        int assetSuffix = new Random().nextInt(4) + 1;
        Texture texture = new Texture(dir + assetName + assetSuffix + ".png");
        return new TextureRegion(texture);
    }

    public static TextureRegion levelSelectTileNxN(Entity.Order order, int size) {
        Map<Integer, List<TextureRegion>> sizeMap = LEVEL_SELECT_TILES.get(order);
        if (sizeMap == null) return null;
        if (order == WATER && size < 4) {
            size = 1;
        }
        List<TextureRegion> tiles = sizeMap.get(size);

        if (tiles == null || tiles.isEmpty()) return null;
        return tiles.get(new Random().nextInt(tiles.size()));
    }

    public static TextureRegion level3x3ForOrder(Entity.Order order) {
        return LEVEL_3X3_ICONS.get(order);
    }

    public enum Icon {
        ARROW_SYMBOL;

        public String getName() {
            return this.name();
        }
    }

    public static void dispose() {
        RAW_ICONS.values().forEach(tr -> tr.getTexture().dispose());
        REFINED_ICONS.values().forEach(tr -> tr.getTexture().dispose());
        MOB_ICONS.values().forEach(tr -> tr.getTexture().dispose());
        AMMO_ICONS.values().forEach(tr -> tr.getTexture().dispose());
        SYMBOL_ICONS.values().forEach(tr -> tr.getTexture().dispose());
        LEVEL_3X3_ICONS.values().forEach(tr -> tr.getTexture().dispose());

        // Dispose preloaded level select tiles
        for (Map<Integer, List<TextureRegion>> sizeMap : LEVEL_SELECT_TILES.values()) {
            for (List<TextureRegion> tiles : sizeMap.values()) {
                for (TextureRegion tr : tiles) {
                    tr.getTexture().dispose();
                }
            }
        }
        LEVEL_SELECT_TILES.clear();
    }
}
