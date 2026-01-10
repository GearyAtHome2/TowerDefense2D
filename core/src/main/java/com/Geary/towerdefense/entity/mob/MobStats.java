package com.Geary.towerdefense.entity.mob;

public record MobStats(
    String name,
    float size,
    int health,
    int damage,
    float speed,
    float knockbackDamping,
    float ranMoveProb
) {}
