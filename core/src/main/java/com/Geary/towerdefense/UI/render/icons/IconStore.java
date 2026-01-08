package com.Geary.towerdefense.UI.render.icons;

import com.Geary.towerdefense.entity.resources.Resource;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import java.util.EnumMap;
import java.util.Map;

public class IconStore {

    private static final Map<Resource.RawResourceType, TextureRegion> RAW_ICONS = new EnumMap<>(Resource.RawResourceType.class);
    private static final Map<Resource.RefinedResourceType, TextureRegion> REFINED_ICONS = new EnumMap<>(Resource.RefinedResourceType.class);

    public static void load() {
        for (Resource.RawResourceType type : Resource.RawResourceType.values()) {
            RAW_ICONS.put(
                type,
                new TextureRegion(new Texture("resources/raw/" + type.name().toLowerCase() + ".png"))
            );
        }

        for (Resource.RefinedResourceType type : Resource.RefinedResourceType.values()) {

            //todo: add this once I've made the .png for refined icons
//            REFINED_ICONS.put(
//                type,
//                new TextureRegion(new Texture("resources/refined/" + type.name().toLowerCase() + ".png"))
//            );
        }
    }

    public static TextureRegion raw(Resource.RawResourceType type) {

        return RAW_ICONS.get(type);
    }

    public static TextureRegion refined(Resource.RefinedResourceType type) {
        return REFINED_ICONS.get(type);
    }
}
