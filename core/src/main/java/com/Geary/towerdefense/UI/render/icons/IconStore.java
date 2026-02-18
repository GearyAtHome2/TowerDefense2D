package com.Geary.towerdefense.UI.render.icons;

import com.Geary.towerdefense.behaviour.targeting.TargetingHelper;
import com.Geary.towerdefense.entity.Entity;
import com.Geary.towerdefense.entity.resources.Resource;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import javax.swing.*;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class IconStore {

    private static final Map<Resource.RawResourceType, TextureRegion> RAW_ICONS = new EnumMap<>(Resource.RawResourceType.class);
    private static final Map<Resource.RefinedResourceType, TextureRegion> REFINED_ICONS = new EnumMap<>(Resource.RefinedResourceType.class);
    private static final Map<String, TextureRegion> MOB_ICONS = new HashMap<>();
    private static final Map<String, TextureRegion> AMMO_ICONS = new HashMap<>();
    private static final Map<Icon, TextureRegion> SYMBOL_ICONS = new EnumMap<>(Icon.class);

    // --- NEW: Shared 3x3 level icons per order ---
    private static final EnumMap<Entity.Order, TextureRegion> LEVEL_3X3_ICONS = new EnumMap<>(Entity.Order.class);

    public static void load() {
        // --- Existing icons ---
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

        // --- NEW: Initialize shared 3x3 level icons ---
        String dir = "mapStructures/level/";
        for (Entity.Order order : Entity.Order.values()) {
            if (order == Entity.Order.NEUTRAL) continue; // skip NEUTRAL

            String assetName;
            switch (order) {
                case TECH -> assetName = "dark_placeholder";
                case NATURE -> assetName = "dark_placeholder";
                case DARK -> assetName = "dark_placeholder";
                case LIGHT -> assetName = "dark_placeholder";
                case FIRE -> assetName = "dark_placeholder";
                case WATER -> assetName = "dark_placeholder";
                default -> assetName = "dark_placeholder";
            }

            Texture tex = new Texture(dir + assetName + ".png"); // load once
            LEVEL_3X3_ICONS.put(order, new TextureRegion(tex));
        }
    }

    // --- Existing getters ---
    public static TextureRegion getSymbol(Icon symbol) {
        return SYMBOL_ICONS.get(symbol);
    }

    public static TextureRegion targetingStrategy(TargetingHelper.TargetingStrategy strategy) {
        String strategyName = strategy.name().toLowerCase();
        String key = strategyName.toLowerCase().replace(" ", "_");
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
//        String dir = "mapStructures/" + order.name() + "/";
        String dir = "mapStructures/"+order.name()+"/";

        //temporarily overwriting size - will be logic for this later.

        String Nsize="";
        if (order == Entity.Order.NATURE){
            Nsize = "4";
        }
        if (order == Entity.Order.WATER){
            Nsize = "1";
        }
        String assetName = Nsize + "x" + Nsize;

        String assetSuffix = "_" + (new Random().nextInt(3) + 1);
        Texture texture = new Texture(dir + assetName + assetSuffix + ".png");
        return new TextureRegion(texture);
    }

    // --- NEW: Safe shared 3x3 level icon ---
    public static TextureRegion level3x3ForOrder(Entity.Order order) {
        return LEVEL_3X3_ICONS.get(order);
    }

    // --- Icon enum ---
    public enum Icon {
        ARROW_SYMBOL;

        public String getName() {
            return this.name();
        }
    }

    // --- NEW: Dispose all loaded textures ---
    public static void dispose() {
        RAW_ICONS.values().forEach(tr -> tr.getTexture().dispose());
        REFINED_ICONS.values().forEach(tr -> tr.getTexture().dispose());
        MOB_ICONS.values().forEach(tr -> tr.getTexture().dispose());
        AMMO_ICONS.values().forEach(tr -> tr.getTexture().dispose());
        SYMBOL_ICONS.values().forEach(tr -> tr.getTexture().dispose());
        LEVEL_3X3_ICONS.values().forEach(tr -> tr.getTexture().dispose());
    }
}
