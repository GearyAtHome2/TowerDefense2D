package com.Geary.towerdefense.entity.mob;

import com.badlogic.gdx.graphics.Color;

public record MobStats(
    String name,
    String effectText,
    String flavourText,
    float size,
    int health,
    int damage,
    float speed,
    float knockbackDamping,
    float ranMoveProb,
    Color color,
    int spawnTime, // Set to 0 for enemies if unused
    int armour    // New addition to record
) {}
