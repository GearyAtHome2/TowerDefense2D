package com.Geary.towerdefense.UI.render.icons;

import com.Geary.towerdefense.entity.resources.Resource;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.EnumMap;
import java.util.Map;

public class IconStore {

    private static final Map<Resource.RawResourceType, TextureRegion> RAW_ICONS = new EnumMap<>(Resource.RawResourceType.class);
    private static final Map<Resource.RefinedResourceType, TextureRegion> REFINED_ICONS = new EnumMap<>(Resource.RefinedResourceType.class);
    private static final Map<Icon, TextureRegion> SYMBOL_ICONS = new EnumMap<>(Icon.class);

    public static void load() {
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


    }

    public static TextureRegion getSymbol(Icon symbol) {
        return SYMBOL_ICONS.get(symbol);
    }

    public static TextureRegion rawResource(Resource.RawResourceType type) {
        return RAW_ICONS.get(type);
    }

    public static TextureRegion refinedResource(Resource.RefinedResourceType type) {
        return REFINED_ICONS.get(type);
    }


    public enum Icon {
        ARROW_SYMBOL;

        public String getName() {
            return this.name();
        }
    }
}
