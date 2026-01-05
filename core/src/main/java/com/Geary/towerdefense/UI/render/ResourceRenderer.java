package com.Geary.towerdefense.UI.render;

import com.Geary.towerdefense.entity.resources.Resource;
import com.Geary.towerdefense.entity.world.Cell;
import com.Geary.towerdefense.world.GameWorld;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class ResourceRenderer {

    private final GameWorld world;

    private static final float MARGIN = 20f;
    private static final float SIZE = GameWorld.cellSize - 2 * MARGIN;

    private final ShapeRenderer sr;

    public ResourceRenderer(GameWorld world, ShapeRenderer sr) {
        this.world = world;
        this.sr = sr;
    }

    public void drawResources() {
        for (int x = 0; x < world.gridWidth; x++) {
            for (int y = 0; y < world.gridHeight; y++) {
                Cell cell = world.grid[x][y];
                if (cell.resource != null) {
                    drawResource(cell);
                }
            }
        }
    }

    private void drawResource(Cell cell) {
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(getColor(cell.resource));

        float cx = cell.x + GameWorld.cellSize / 2f;
        float cy = cell.y + GameWorld.cellSize / 2f;

        sr.circle(cx, cy, SIZE / 2f);
        sr.end();
    }

    private Color getColor(Resource resource) {
        return switch (resource.type) {
            case STONE -> Color.LIGHT_GRAY;
            case COPPER -> Color.GOLDENROD;
            case TIN -> Color.SLATE;
            case IRON -> Color.LIGHT_GRAY;
            case COAL -> Color.DARK_GRAY;
        };
    }
}
