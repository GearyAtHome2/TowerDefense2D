package com.Geary.towerdefense.entity.mob.friendly.neutral;

import com.Geary.towerdefense.entity.mob.MobStats;
import com.Geary.towerdefense.entity.resources.Resource;
import com.badlogic.gdx.graphics.Color;


public class ManAtArms extends FNeutral {

    public static final MobStats STATS = new MobStats(
        "Man At Arms",
        "",
        "This man has been given access to arms, and he is At Them.",
        0.13f, 220, 5, 0.69f, 12f, 0.2f,
        Color.OLIVE,
        2, // spawnTime
        0  // armour
    );

    public ManAtArms(float x, float y) {
        super(x, y, STATS, ManAtArms.class);
        this.coinCost = 3;
        this.refinedResourceCost.put(Resource.RefinedResourceType.BASIC_WEAPONS, 4.0);
    }
}
