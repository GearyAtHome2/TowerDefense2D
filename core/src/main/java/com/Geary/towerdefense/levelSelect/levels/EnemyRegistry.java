package com.Geary.towerdefense.levelSelect.levels;

import com.Geary.towerdefense.entity.mob.enemy.Enemy;
import com.Geary.towerdefense.entity.mob.enemy.dark.Ghoul;
import com.Geary.towerdefense.entity.mob.enemy.fire.Imp;
import com.Geary.towerdefense.entity.mob.enemy.light.Acolyte;
import com.Geary.towerdefense.entity.mob.enemy.nature.Spider;
import com.Geary.towerdefense.entity.mob.enemy.neutral.Groblin;
import com.Geary.towerdefense.entity.mob.enemy.tech.Cyborg;
import com.Geary.towerdefense.entity.mob.enemy.water.Mermaid;

import java.util.HashMap;
import java.util.Map;

public class EnemyRegistry {

    private static final Map<String, Class<? extends Enemy>> registry = new HashMap<>();

    static {
        registry.put("Groblin", Groblin.class);
        registry.put("Imp", Imp.class);
        registry.put("Ghoul", Ghoul.class);
        registry.put("Mermaid", Mermaid.class);
        registry.put("Acolyte", Acolyte.class);
        registry.put("Spider", Spider.class);
        registry.put("Cyborg", Cyborg.class);
    }

    public static Class<? extends Enemy> getClassByName(String name) {
        Class<? extends Enemy> clazz = registry.get(name);
        if (clazz == null) throw new RuntimeException("Unknown enemy type: " + name);
        return clazz;
    }
}
