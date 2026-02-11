package com.Geary.towerdefense.entity.buildings.tower;

import com.Geary.towerdefense.behaviour.targeting.TargetingHelper;
import com.Geary.towerdefense.entity.mob.bullet.BasicBullet;
import com.Geary.towerdefense.entity.mob.bullet.Bullet;
import com.Geary.towerdefense.entity.mob.bullet.BulletRepr;
import com.badlogic.gdx.graphics.Color;

import java.util.List;

public class ShotgunTower extends Tower {

    public Bullet selectedAmmo;

    public ShotgunTower(float x, float y) {
        super(x, y, "Shotgun Tower", new BasicBullet(), 0.6f, 0.5f, 175f);
        this.targetingStrategy = TargetingHelper.TargetingStrategy.CLOSEST;
        this.simultShots = 5;
        List<BulletRepr<? extends Bullet>> supportedBullets = List.of(new BulletRepr<>(new BasicBullet()),new BulletRepr<>(new BasicBullet()), new BulletRepr<>(new BasicBullet()), new BulletRepr<>(new BasicBullet()), new BulletRepr<>(new BasicBullet()), new BulletRepr<>(new BasicBullet()));
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
