package com.Geary.towerdefense.behaviour;

import com.Geary.towerdefense.entity.Bullet;
import com.Geary.towerdefense.entity.Enemy;
import com.Geary.towerdefense.entity.Tower;
import com.Geary.towerdefense.entity.world.Cell;
import com.Geary.towerdefense.world.GameWorld;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;

import java.util.List;

public class TowerManager {
    private final GameWorld world;
    private final OrthographicCamera camera;
    private boolean towerPlacementActive = false;

    public TowerManager(GameWorld world, OrthographicCamera camera) {
        this.world = world;
        this.camera = camera;
    }

    public boolean isPlacementActive() {
        return towerPlacementActive;
    }

    public void togglePlacement(Vector3 uiClick, float buttonX, float buttonY, float buttonW, float buttonH) {
        if (uiClick.x >= buttonX && uiClick.x <= buttonX + buttonW &&
            uiClick.y >= buttonY && uiClick.y <= buttonY + buttonH) {
            towerPlacementActive = !towerPlacementActive;
        }
    }

    public void handlePlacement() {
        if (!towerPlacementActive) return;

        if (com.badlogic.gdx.Gdx.input.isButtonJustPressed(com.badlogic.gdx.Input.Buttons.LEFT)) {
            Vector3 worldPos = new Vector3(com.badlogic.gdx.Gdx.input.getX(), com.badlogic.gdx.Gdx.input.getY(), 0);
            camera.unproject(worldPos);
            int x = (int) (worldPos.x / world.cellSize);
            int y = (int) (worldPos.y / world.cellSize);

            if (x >= 0 && x < world.gridWidth &&
                y >= 0 && y < world.gridHeight &&
                !world.occupied[x][y] &&
                world.grid[x][y].type == Cell.Type.TOWER) {

                world.towers.add(new Tower(x * world.cellSize, y * world.cellSize));
                world.occupied[x][y] = true;
            }
        }
    }

    public void updateTowers(List<Bullet> bullets, float delta) {
        for (Tower tower : world.towers) {
            tower.cooldown -= delta;
            if (tower.cooldown <= 0) {
                Enemy target = tower.findTarget(world.enemies);
                if (target != null) bullets.add(tower.shoot(target));
                tower.cooldown = tower.maxCooldown; // reset using tower's cooldown in seconds
            }
        }
    }
}
