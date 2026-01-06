package com.Geary.towerdefense.behaviour.buildings.manager;

import com.Geary.towerdefense.entity.buildings.Tower;
import com.Geary.towerdefense.entity.mob.Bullet;
import com.Geary.towerdefense.entity.world.Cell;
import com.Geary.towerdefense.world.GameWorld;
import com.badlogic.gdx.graphics.OrthographicCamera;

import java.util.List;

public class TowerManager extends BuildingManager<Tower> {

    public TowerManager(GameWorld world, OrthographicCamera camera) {
        super(world, camera);
    }

    @Override
    protected boolean canPlaceAt(Cell cell, int x, int y) {
        return !world.occupied[x][y] &&
            (cell.type == Cell.Type.EMPTY || cell.type == Cell.Type.HOME);
    }

    @Override
    protected void handleLeftClick(Cell cell, int x, int y) {
        Tower tower = new Tower(x * world.cellSize, y * world.cellSize);
        world.towers.add(tower);
        cell.building = tower;
        world.occupied[x][y] = true;
    }

    @Override
    protected void updateGhost(Cell cell, int x, int y) {
        if (world.ghostTower == null) world.ghostTower = new Tower(x * world.cellSize, y * world.cellSize);
        else {
            world.ghostTower.xPos = x * world.cellSize;
            world.ghostTower.yPos = y * world.cellSize;
        }
    }

    @Override
    protected void resetGhost() {
        world.ghostTower = null;
    }

    /**
     * Update towers: acquire targets, rotate gun, handle cooldown, and shoot bullets
     */
    public void updateTowers(List<Bullet> bullets, float delta) {
        for (Tower tower : world.towers) {
            tower.cooldown -= delta;

            // Acquire or refresh target
            if (tower.currentTarget == null || tower.currentTarget.health <= 0 ||
                tower.getDistanceTo(tower.currentTarget) > tower.range) {
                tower.currentTarget = tower.findTarget(world.enemies);
                tower.currentTarget = tower.findTargetFurthestProgressed(world.enemies);
            }

            tower.updateGunAngle(delta);

            if (tower.cooldown <= 0 && tower.currentTarget != null && tower.canShoot()) {
                bullets.add(tower.shoot(tower.currentTarget));
                tower.cooldown = tower.maxCooldown;
            }
        }
    }

    public void deleteTower(Tower tower) {
        deleteBuilding(tower, world.towers);
    }
}
