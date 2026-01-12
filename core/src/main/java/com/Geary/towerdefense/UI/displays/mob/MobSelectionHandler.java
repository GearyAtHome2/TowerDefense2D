package com.Geary.towerdefense.UI.displays.mob;

import com.Geary.towerdefense.UI.displays.UIClickManager;
import com.Geary.towerdefense.entity.mob.Mob;
import com.Geary.towerdefense.entity.mob.enemy.Enemy;
import com.Geary.towerdefense.entity.mob.friendly.Friendly;
import com.Geary.towerdefense.world.GameWorld;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MobSelectionHandler {

    private final GameWorld world;
    private final Viewport worldViewport;

    public MobSelectionHandler(GameWorld world, Viewport worldViewport) {
        this.world = world;
        this.worldViewport = worldViewport;
    }

    public Mob getMobAtScreen(int screenX, int screenY) {
        if (!UIClickManager.isClickInGameArea(screenY)) {
            return null;
        }

        Vector3 worldClick = new Vector3(screenX, screenY, 0);
        worldViewport.unproject(worldClick);

        return getMobAtWorld(worldClick.x, worldClick.y);
    }

    public Mob getMobAtWorld(float worldX, float worldY) {
        for (Friendly friendly : world.friends) {
            if (isInsideMob(worldX, worldY, friendly)) {
                return friendly;
            }
        }
        for (Enemy enemy : world.enemies) {
            if (isInsideMob(worldX, worldY, enemy)) {
                return enemy;
            }
        }
        return null;
    }

    private boolean isInsideMob(float x, float y, Mob mob) {
        return x >= mob.xPos - mob.size/2 &&
            x <= mob.xPos + mob.size/2 &&
            y >= mob.yPos - mob.size/2 &&
            y <= mob.yPos + mob.size/2;
    }

}
