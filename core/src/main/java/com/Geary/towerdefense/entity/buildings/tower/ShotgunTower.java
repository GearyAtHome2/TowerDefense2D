package com.Geary.towerdefense.entity.buildings.tower;

import com.Geary.towerdefense.entity.mob.bullet.BasicBullet;
import com.Geary.towerdefense.entity.mob.bullet.Bullet;
import com.badlogic.gdx.graphics.Color;

import java.util.List;

public class ShotgunTower extends Tower {

    public Bullet selectedAmmo;

    public ShotgunTower(float x, float y) {//todo: update these towers to instantiate with a bullet type that works for firing.
        super(x, y, "Shotgun Tower", new BasicBullet(), 0.6f, 0.5f, 175f);
        this.targetingStrategy = TargetingStrategy.CLOSEST;
        this.simultShots = 5;
        List<Bullet> supportedBullets = List.of(new BasicBullet(), new BasicBullet(), new BasicBullet(), new BasicBullet(), new BasicBullet(), new BasicBullet());
        setSupportedAmmo(supportedBullets);
    }

    // --- UI Info ---
    @Override
    public List<String> getInfoLines() {
        return List.of(
            this.name,
            "Cooldown: " + (int) Math.ceil(cooldown * 10f),
            "Range: " + range
        );
    }

    @Override
    public Color getInfoTextColor() {
        return Color.CYAN;
    }

}
