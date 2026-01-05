package com.Geary.towerdefense.UI.displays;

import com.Geary.towerdefense.entity.buildings.Tower;
import com.Geary.towerdefense.world.GameWorld;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;

public class TowerSelectionHandler {

    private final GameWorld world;
    private final Viewport worldViewport;

    public TowerSelectionHandler(GameWorld world, Viewport worldViewport) {
        this.world = world;
        this.worldViewport = worldViewport;
    }

    /** Returns the tower clicked at screen coordinates, or null */
    public Tower getTowerAtScreen(int screenX, int screenY) {
        Vector3 worldClick = new Vector3(screenX, screenY, 0);
        worldViewport.unproject(worldClick);

        for (Tower tower : world.towers) {
            if (tower.contains(worldClick.x, worldClick.y, world.cellSize)) {
                return tower;
            }
        }
        return null;
    }
}
