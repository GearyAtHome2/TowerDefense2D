package com.Geary.towerdefense.UI.render;

import com.Geary.towerdefense.UI.render.production.FactoryRenderer;
import com.Geary.towerdefense.behaviour.SparkManager;
import com.Geary.towerdefense.entity.mob.bullet.Bullet;
import com.Geary.towerdefense.entity.mob.enemy.Enemy;
import com.Geary.towerdefense.entity.mob.friendly.Friendly;
import com.Geary.towerdefense.entity.spawner.EnemySpawner;
import com.Geary.towerdefense.entity.spawner.FriendlySpawner;
import com.Geary.towerdefense.entity.world.Cell;
import com.Geary.towerdefense.world.GameWorld;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
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
                    case EMPTY -> {
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
                    }
                    default -> {
                        continue;
                    }
                }
                if (!cell.bridgable) {
                    float x0 = cell.x;
                    float y0 = cell.y;
                    float x1 = cell.x + GameWorld.cellSize;
                    float y1 = cell.y + GameWorld.cellSize;
                    shapeRenderer.line(x0, y1, x1, y0);
                }

                shapeRenderer.rect(cell.x, cell.y, world.cellSize, world.cellSize);
            }
        }

        shapeRenderer.end();
    }

    public void drawActors(ShapeRenderer shapeRenderer, SparkManager sparkManager, TowerRenderer towerRenderer,
                           TransportRenderer transportRenderer, MineRenderer mineRenderer, FactoryRenderer factoryRenderer) {
        // Draw enemies, friends, bullets with SpriteBatch
        for (Enemy e : world.enemies) e.draw(shapeRenderer);
        for (Friendly f : world.friends) f.draw(shapeRenderer);
        for (Bullet b : world.bullets) b.draw(shapeRenderer);

        // Draw sparks
        sparkManager.draw(shapeRenderer);

        // Draw spawners and towers

        transportRenderer.drawTransports();
        mineRenderer.drawMines();
        factoryRenderer.drawFactories();

        for (EnemySpawner es : world.enemySpawners) es.draw(shapeRenderer);
        for (FriendlySpawner fs : world.friendlySpawners) fs.draw(shapeRenderer);
        towerRenderer.drawTowers();
        towerRenderer.drawTowerRanges();
    }
}
