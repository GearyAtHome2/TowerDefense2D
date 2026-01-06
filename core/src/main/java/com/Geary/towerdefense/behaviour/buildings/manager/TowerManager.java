package com.Geary.towerdefense.behaviour.buildings.manager;

import com.Geary.towerdefense.entity.mob.Bullet;
import com.Geary.towerdefense.entity.buildings.Tower;
import com.Geary.towerdefense.entity.world.Cell;
import com.Geary.towerdefense.world.GameWorld;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;

import java.util.List;

public class TowerManager {
    private final GameWorld world;
    private final OrthographicCamera camera;
    private boolean towerPlacementButtonActive = false;
    private boolean towerPlacementKbActive = false;

    public TowerManager(GameWorld world, OrthographicCamera camera) {
        this.world = world;
        this.camera = camera;
    }

    public void setPlacementKeyboardActive(boolean isActive){
        towerPlacementKbActive = isActive;
    }

    public boolean isPlacementActive() {
        return towerPlacementButtonActive || towerPlacementKbActive;
    }

    public void togglePlacementClick(Vector3 uiClick, float buttonX, float buttonY, float buttonW, float buttonH) {
        if (uiClick.x >= buttonX && uiClick.x <= buttonX + buttonW &&
            uiClick.y >= buttonY && uiClick.y <= buttonY + buttonH) {
            towerPlacementButtonActive = !towerPlacementButtonActive;
        }
    }

    public void togglePlacementKb(boolean ctrlHeld) {
        towerPlacementKbActive = ctrlHeld;
    }

    public boolean handlePlacement() {
        if (!isPlacementActive()) {
            world.ghostTower = null;
            return false;
        }

        Vector3 worldPos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(worldPos);
        int x = (int) (worldPos.x / world.cellSize);
        int y = (int) (worldPos.y / world.cellSize);

        // Check bounds
        if (x < 0 || x >= world.gridWidth || y < 0 || y >= world.gridHeight) {
            world.ghostTower = null;
            return false;
        }

        // Tile must be valid
        boolean canPlace = !world.occupied[x][y] &&
            (world.grid[x][y].type == Cell.Type.EMPTY || world.grid[x][y].type == Cell.Type.HOME);

        if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT) && canPlace) {
            Tower tower = new Tower(x * world.cellSize, y * world.cellSize);
            world.towers.add(tower);
            world.grid[x][y].building = tower;
            world.occupied[x][y] = true;
            world.ghostTower = null;
            return true;
        }
        else if (canPlace) {
            if (world.ghostTower == null) {
                world.ghostTower = new Tower(x * world.cellSize, y * world.cellSize);
            } else {
                world.ghostTower.xPos = x * world.cellSize;
                world.ghostTower.yPos = y * world.cellSize;
            }
        }
        else {
            world.ghostTower = null;
        }
        return false;
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
                //todo: tower behaviour switch here - maybe this is an unlockable tech?
//                tower.currentTarget = tower.findTarget(world.enemies);
                tower.currentTarget = tower.findTargetFurthestProgressed(world.enemies);
            }

            tower.updateGunAngle(delta);

            if (tower.cooldown <= 0 && tower.currentTarget != null && tower.canShoot()) {
                bullets.add(tower.shoot(tower.currentTarget));
                tower.cooldown = tower.maxCooldown;
            }
        }
    }

    public void deleteTower(Tower tower){
        if (tower == null) return;

        int x = (int) tower.xPos / world.cellSize;
        int y = (int) tower.yPos / world.cellSize;

        world.towers.remove(tower);

        if (x >= 0 && x < world.gridWidth && y >= 0 && y < world.gridHeight) {
            if (world.grid[x][y].building == tower) {
                world.grid[x][y].building = null;
            }
            world.occupied[x][y] = false;
        }
    }
}
