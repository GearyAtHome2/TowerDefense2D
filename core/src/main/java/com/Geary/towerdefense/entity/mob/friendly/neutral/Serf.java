package com.Geary.towerdefense.entity.mob.friendly.neutral;

import com.Geary.towerdefense.entity.mob.MobStats;
import com.badlogic.gdx.graphics.Color;

public class Serf extends FNeutral {

    public static final MobStats STATS = new MobStats(
        "Serf",
        "Serf Effect text...",
        "Expendable, dependable, and commendable. We love Serfs",
        0.1f, 220, 5, 0.71f, 8f, 0.3f,
        Color.GREEN,
        2,
        0
    );

    public Serf(float x, float y) {
        super(x, y, STATS, Serf.class);
    }
}
