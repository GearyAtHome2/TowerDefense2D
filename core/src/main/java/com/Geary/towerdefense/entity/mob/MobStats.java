package com.Geary.towerdefense.entity.mob;

public record MobStats(
    int health,
    int damage,
    float speed,
    float knockbackDamping,
    float ranMoveProb
) {}
