package com.Geary.towerdefense.behaviour;

import com.Geary.towerdefense.entity.Bullet;
import com.Geary.towerdefense.entity.Tower;
import com.Geary.towerdefense.entity.world.Cell;
import com.Geary.towerdefense.world.GameWorld;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;

import java.util.List;

public class TowerManager {
    private final GameWorld world;
    private final OrthographicCamera camera;
    private boolean towerPlacementButtonActive = false;
    private boolean towerPlacementCtrlActive = false;

    public TowerManager(GameWorld world, OrthographicCamera camera) {
        this.world = world;
        this.camera = camera;
    }

    public boolean isPlacementActive() {
        return towerPlacementButtonActive || towerPlacementCtrlActive;
    }

    public void togglePlacementClick(Vector3 uiClick, float buttonX, float buttonY, float buttonW, float buttonH) {
        if (uiClick.x >= buttonX && uiClick.x <= buttonX + buttonW &&
            uiClick.y >= buttonY && uiClick.y <= buttonY + buttonH) {
            towerPlacementButtonActive = !towerPlacementButtonActive;
        }
    }

    public void togglePlacementKp(boolean ctrlHeld) {
        towerPlacementCtrlActive = ctrlHeld;
    }

    public void handlePlacement() {
        if (!isPlacementActive()) return;

        if (com.badlogic.gdx.Gdx.input.isButtonJustPressed(com.badlogic.gdx.Input.Buttons.LEFT)) {
            Vector3 worldPos = new Vector3(com.badlogic.gdx.Gdx.input.getX(), com.badlogic.gdx.Gdx.input.getY(), 0);
            camera.unproject(worldPos);

            int x = (int) (worldPos.x / world.cellSize);
            int y = (int) (worldPos.y / world.cellSize);

            if (x >= 0 && x < world.gridWidth &&
                y >= 0 && y < world.gridHeight &&
                !world.occupied[x][y] &&
                (world.grid[x][y].type == Cell.Type.TOWER || world.grid[x][y].type == Cell.Type.HOME)) {

                world.towers.add(new Tower(x * world.cellSize, y * world.cellSize));
                world.occupied[x][y] = true;
            }
        }
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

            // Rotate gun toward target
            tower.updateGunAngle(delta);

            // Shoot if cooldown finished
            if (tower.cooldown <= 0 && tower.currentTarget != null && tower.canShoot()) {
                bullets.add(tower.shoot(tower.currentTarget));
                tower.cooldown = tower.maxCooldown;
            }
        }
    }
}
