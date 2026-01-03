package com.Geary.towerdefense.UI.render;

import com.Geary.towerdefense.behaviour.SparkManager;
import com.Geary.towerdefense.entity.Bullet;
import com.Geary.towerdefense.entity.mob.Enemy;
import com.Geary.towerdefense.entity.mob.Friendly;
import com.Geary.towerdefense.entity.spawner.EnemySpawner;
import com.Geary.towerdefense.entity.spawner.FriendlySpawner;
import com.Geary.towerdefense.entity.world.Cell;
import com.Geary.towerdefense.world.GameWorld;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class WorldRenderer {

    private final GameWorld world;
    private final ShapeRenderer shapeRenderer;

    public WorldRenderer(GameWorld world, ShapeRenderer shapeRenderer) {
        this.world = world;
        this.shapeRenderer = shapeRenderer;
    }

    public void drawGridLines() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1, 1, 1, 0.3f);

        for (int x = 0; x <= world.gridWidth; x++) {
            shapeRenderer.line(
                x * world.cellSize, 0,
                x * world.cellSize, world.gridHeight * world.cellSize
            );
        }

        for (int y = 0; y <= world.gridHeight; y++) {
            shapeRenderer.line(
                0, y * world.cellSize,
                world.gridWidth * world.cellSize, y * world.cellSize
            );
        }

        shapeRenderer.end();
    }

    public void drawCells() {
        drawBaseCells();
        drawPathCells();
    }

    private void drawBaseCells() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        for (int x = 0; x < world.gridWidth; x++) {
            for (int y = 0; y < world.gridHeight; y++) {
                Cell cell = world.grid[x][y];

                switch (cell.type) {
                    case TOWER -> {
                        shapeRenderer.setColor(0f, 0.8f, 0f, 0.35f);
                        shapeRenderer.rect(cell.x, cell.y, world.cellSize, world.cellSize);
                    }
                    case HOME -> {
                        shapeRenderer.setColor(0f, 1f, 0f, 0.2f);
                        shapeRenderer.rect(cell.x, cell.y, world.cellSize, world.cellSize);
                    }
                    case ENEMY -> {
                        shapeRenderer.setColor(1f, 0f, 0f, 0.2f);
                        shapeRenderer.rect(cell.x, cell.y, world.cellSize, world.cellSize);
                    }
                }
            }
        }

        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void drawPathCells() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        for (int x = 0; x < world.gridWidth; x++) {
            for (int y = 0; y < world.gridHeight; y++) {
                Cell cell = world.grid[x][y];

                switch (cell.type) {
                    case PATH -> shapeRenderer.setColor(1f, 0f, 0f, 0.5f);
                    case TURN -> {
                        shapeRenderer.setColor(0.9f, 0.6f, 0.3f, 0.6f);
                        float x0 = cell.x;
                        float y0 = cell.y;
                        float x1 = cell.x + GameWorld.cellSize;
                        float y1 = cell.y + GameWorld.cellSize;

                        if (cell.turnType == Cell.TurnType.TL_BR) {
                            shapeRenderer.line(x0, y1, x1, y0);
                        } else if (cell.turnType == Cell.TurnType.BL_TR) {
                            shapeRenderer.line(x0, y0, x1, y1);
                        }
                    }
                    default -> {continue;}
                }

                shapeRenderer.rect(cell.x, cell.y, world.cellSize, world.cellSize);
            }
        }

        shapeRenderer.end();
    }

    public void drawActors(SpriteBatch batch, SparkManager sparkManager, TowerRenderer towerRenderer) {
        // Draw enemies, friends, bullets with SpriteBatch
        batch.begin();
        for (Enemy e : world.enemies) e.draw(batch);
        for (Friendly f : world.friends) f.draw(batch);
        for (Bullet b : world.bullets) b.draw(batch);
        batch.end();

        // Draw sparks
        sparkManager.draw(shapeRenderer);

        // Draw spawners and towers
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (EnemySpawner es : world.enemySpawners) es.draw(shapeRenderer);
        for (FriendlySpawner fs : world.friendlySpawners) fs.draw(shapeRenderer);
        towerRenderer.drawTowers(shapeRenderer);
        shapeRenderer.end();
    }
}
