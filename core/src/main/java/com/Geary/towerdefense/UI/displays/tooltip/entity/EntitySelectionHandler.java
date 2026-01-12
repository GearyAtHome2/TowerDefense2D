package com.Geary.towerdefense.UI.displays.tooltip.entity;

import com.Geary.towerdefense.UI.displays.tooltip.UIClickManager;
import com.Geary.towerdefense.entity.Entity;
import com.Geary.towerdefense.entity.buildings.Building;
import com.Geary.towerdefense.entity.mob.enemy.Enemy;
import com.Geary.towerdefense.entity.mob.friendly.Friendly;
import com.Geary.towerdefense.entity.spawner.Spawner;
import com.Geary.towerdefense.world.GameWorld;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;

public class EntitySelectionHandler {

    private final GameWorld world;
    private final Viewport worldViewport;
    private final Vector3 tmp = new Vector3();

    public EntitySelectionHandler(GameWorld world, Viewport worldViewport) {
        this.world = world;
        this.worldViewport = worldViewport;
    }

    /* ============================
       Screen-space entry point
       ============================ */

    public Entity getEntityAtScreen(float screenX, float screenY) {
        if (!UIClickManager.isClickInGameArea(screenY)) {
            return null;
        }

        tmp.set(screenX, screenY, 0);
        worldViewport.unproject(tmp);

        return getEntityAtWorld(tmp.x, tmp.y);
    }


    public Entity getEntityAtWorld(float worldX, float worldY) {

        // --- Priority 1: mobs (smaller, need precision) ---
        for (Friendly friendly : world.friends) {
            if (isInsideEntity(worldX, worldY, friendly)) {
                return friendly;
            }
        }

        for (Enemy enemy : world.enemies) {
            if (isInsideEntity(worldX, worldY, enemy)) {
                return enemy;
            }
        }

        for (Building b : world.mines) {
            if (isInsideEntity(worldX, worldY, b)) {
                return b;
            }
        }

        for (Building b : world.towers) {
            if (isInsideEntity(worldX, worldY, b)) {
                return b;
            }
        }

        for (Building b : world.transports) {
            if (isInsideEntity(worldX, worldY, b)) {
                return b;
            }
        }

        for (Building b : world.factories) {
            if (isInsideEntity(worldX, worldY, b)) {
                return b;
            }
        }

        for (Spawner s : world.friendlySpawners) {
            if (isInsideEntity(worldX, worldY, s)) {
                return s;
            }
        }

        for (Spawner s : world.enemySpawners) {
            if (isInsideEntity(worldX, worldY, s)) {
                return s;
            }
        }

        return null;
    }

    private boolean isInsideEntity(float x, float y, Entity entity) {
        float r = entity.collisionRadius;
        return x >= entity.xPos - r &&
            x <= entity.xPos + r &&
            y >= entity.yPos - r &&
            y <= entity.yPos + r;
    }
}
