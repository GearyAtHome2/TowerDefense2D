package com.Geary.towerdefense.entity.mob;

import com.badlogic.gdx.graphics.Color;

public record MobStats(
    String name,
    float size,
    int health,
    int damage,
    float speed,
    float knockbackDamping,
    float ranMoveProb,
    Color color
) {}
