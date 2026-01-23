package com.Geary.towerdefense.behaviour.buildings.manager;

import com.Geary.towerdefense.entity.buildings.tower.BasicTower;
import com.Geary.towerdefense.entity.buildings.tower.ShotgunTower;
import com.Geary.towerdefense.entity.buildings.tower.Tower;
import com.Geary.towerdefense.entity.mob.bullet.Bullet;
import com.Geary.towerdefense.entity.world.Cell;
import com.Geary.towerdefense.world.GameWorld;
import com.badlogic.gdx.graphics.OrthographicCamera;

import java.util.List;

public class TowerManager extends BuildingManager<Tower> {

    private Tower activelyPlacingTower = new BasicTower(0, 0);

    public final List<Tower> allTowerTypes;
    public List<Tower> unlockedTowerTypes;

    public TowerManager(GameWorld world, OrthographicCamera camera) {
        super(world, camera);

        allTowerTypes = List.of(
            new BasicTower(0, 0),
            new ShotgunTower(0, 0)
        );
        unlockedTowerTypes = allTowerTypes;
    }

    @Override
    protected boolean canPlaceAt(Cell cell, int x, int y) {
        return !world.occupied[x][y] &&
            (cell.type == Cell.Type.EMPTY || cell.type == Cell.Type.HOME);
    }

    @Override
    protected void handleLeftClick(Cell cell, int x, int y) {
        Tower tower = activelyPlacingTower.clone();
        tower.setPosition(x * world.cellSize, y * world.cellSize);

        world.towers.add(tower);
        cell.building = tower;
        world.occupied[x][y] = true;
    }

    @Override
    protected void updateGhost(Cell cell, int x, int y) {
        if (world.ghostTower == null) {
            world.ghostTower = activelyPlacingTower.clone();
            world.ghostTower.setPosition(x * world.cellSize, y * world.cellSize);
        } else {
            world.ghostTower.setPosition(x * world.cellSize, y * world.cellSize);
        }
    }

    @Override
    protected void resetGhost() {
        world.ghostTower = null;
    }

    public void setPlacementTower(Tower tower) {
        activelyPlacingTower = tower;
        BuildingManager.setActivePlacement(this);
    }

    public void updateTowers(List<Bullet> bullets, float delta) {
        for (Tower tower : world.towers) {
            tower.cooldown -= delta;

            if (tower.currentTarget == null ||
                tower.currentTarget.health <= 0 ||
                tower.getDistanceTo(tower.currentTarget) > tower.range) {

                tower.currentTarget = tower.findTarget(world.enemies);
            }

            tower.updateGunAngle(delta);

            if (tower.cooldown <= 0 &&
                tower.currentTarget != null &&
                tower.canShoot()) {
                List<Bullet> bulletsFired = tower.shoot(tower.currentTarget);
                if (!bulletsFired.isEmpty()) {
                    bullets.addAll(bulletsFired);
                    tower.cooldown = tower.maxCooldown;
                }
            }
        }
    }

//    public boolean canAffordToShoot(Tower tower){
//
//    }

    public void unlockTower(String name) {
        allTowerTypes.stream()
            .filter(t -> t.name.equals(name))
            .findFirst()
            .ifPresent(unlockedTowerTypes::add);
    }

    public void deleteTower(Tower tower) {
        deleteBuilding(tower, world.towers);
    }
}
