package com.Geary.towerdefense.behaviour;

import com.Geary.towerdefense.entity.sprite.Spark;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.ArrayList;
import java.util.List;

public class SparkManager {

    private final List<Spark> sparks = new ArrayList<>();
    private final int maxSparks;
    private static final float defaultSparkLifeTime = 0.8f;

    public SparkManager(int maxSparks) {
        this.maxSparks = maxSparks;
        for (int i = 0; i < maxSparks; i++) {
            sparks.add(new Spark(0, 0, 0)); // initially invisible
        }
    }

    public void spawn(float x, float y) {
        for (Spark s : sparks) {
            if (!s.isVisible()) {
                s.reset(x, y, defaultSparkLifeTime);
                break;
            }
        }
    }

    public void update(float delta) {
        for (Spark s : sparks) {
            if (s.isVisible()) s.update(delta);
        }
    }

    public void draw(ShapeRenderer sr) {
        for (Spark s : sparks) {
            if (s.isVisible()) s.draw(sr);
        }
    }
}
