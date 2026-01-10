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

        for (Friendly friendly : world.friends) {
            if (isClickInsideMob(worldClick, friendly)) {
                System.out.println("found a click on a friendly mob");
                return friendly;
            }
        }
        for (Enemy enemy : world.enemies) {
            if (isClickInsideMob(worldClick, enemy)) {
                return enemy;
            }
        }
        return null;
    }

    private boolean isClickInsideMob(Vector3 click, Mob mob) {
        return click.x >= mob.xPos &&
            click.x <= mob.xPos + mob.size &&
            click.y >= mob.yPos &&
            click.y <= mob.yPos + mob.size;
    }
}
