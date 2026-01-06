package com.Geary.towerdefense.UI.displays;

import com.Geary.towerdefense.entity.buildings.Building;
import com.Geary.towerdefense.world.GameWorld;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;

public class BuildingSelectionHandler {

    private final GameWorld world;
    private final Viewport worldViewport;

    public BuildingSelectionHandler(GameWorld world, Viewport worldViewport) {
        this.world = world;
        this.worldViewport = worldViewport;
    }

    public Building getBuildingAtScreen(int screenX, int screenY) {
        Vector3 worldClick = new Vector3(screenX, screenY, 0);
        worldViewport.unproject(worldClick);


        for (Building b : world.mines) {
            if (isClickInsideBuilding(worldClick, b)) {
                return b;
            }
        }
        for (Building b : world.towers) {
            if (isClickInsideBuilding(worldClick, b)) {
                return b;
            }
        }

        for (Building b : world.transports) {
            if (isClickInsideBuilding(worldClick, b)) {
                return b;
            }
        }


        return null;
    }

    private boolean isClickInsideBuilding(Vector3 click, Building b) {
        return click.x >= b.xPos && click.x <= b.xPos + world.cellSize &&
            click.y >= b.yPos && click.y <= b.yPos + world.cellSize;
    }
}
