package com.Geary.towerdefense.entity.buildings.tower;

import com.Geary.towerdefense.entity.mob.bullet.BasicBullet;
import com.Geary.towerdefense.entity.mob.bullet.Bullet;
import com.Geary.towerdefense.entity.mob.bullet.KnockBackBullet;
import com.badlogic.gdx.graphics.Color;

import java.util.List;

public class BasicTower extends Tower {

    // Prototype bullet; used to create new bullets when shooting
//    public Bullet selectedAmmo;

    public BasicTower(float x, float y) {
        super(x, y,"Basic Tower",  new BasicBullet(), 0.28f, 0.7f, 207f);
        List<Bullet> supportedBullets = List.of(new BasicBullet(), new KnockBackBullet());
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
