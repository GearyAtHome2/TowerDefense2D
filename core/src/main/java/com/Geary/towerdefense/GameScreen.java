package com.Geary.towerdefense;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.ArrayList;
import java.util.List;

public class GameScreen implements Screen {

    SpriteBatch batch;
    List<Enemy> enemies;
    List<Tower> towers;
    List<Bullet> bullets;
    List<Spawner> spawners;

    List<Cell> path = new ArrayList<>();

    ShapeRenderer shapeRenderer = new ShapeRenderer();

    // Grid settings
    private final int cellSize = 100; // pixels per grid cell
    private final int gridWidth = 8;  // 800px / 100
    private final int gridHeight = 6; // 600px / 100
    private boolean[][] occupied;     // track tower placement
    private Cell[][] grid;
    boolean paused = false;

    @Override
    public void show() {
        batch = new SpriteBatch();
        enemies = new ArrayList<>();
        towers = new ArrayList<>();
        bullets = new ArrayList<>();
        spawners = new ArrayList<>();

        occupied = new boolean[gridWidth][gridHeight];

        grid = new Cell[gridWidth][gridHeight];
        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                float px = x * cellSize;
                float py = y * cellSize;

                // Hardcoded path: row 2 from columns 0→4, then column 4 up to row 5
                if (y == 2 && x == 0) {
                    Direction dir = Direction.RIGHT;
                    Cell cell = new Cell(Cell.Type.PATH, px, py, dir);
                    path.add(cell);
                    grid[x][y] = cell;
                    spawners.add(new Spawner(px + 10, py + 10));//just make the spawner larger in future?

                } else if (y == 2 && x <= 4 && x > 0) {
                    Direction dir = (x < 4) ? Direction.RIGHT : Direction.UP;
                    Cell cell = new Cell(Cell.Type.PATH, px, py, dir);
                    path.add(cell);
                    grid[x][y] = cell;
                } else if (x == 4 && y >= 2 && y <= 5) {
                    Direction dir = Direction.UP;
                    Cell cell = new Cell(Cell.Type.PATH, px, py, dir);
                    path.add(cell);
                    grid[x][y] = cell;
                } else {
                    grid[x][y] = new Cell(Cell.Type.TOWER, px, py, Direction.NONE);
                }
            }
        }
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1, 1, 1, 0.3f); // white, semi-transparent
        for (int i = 0; i <= gridWidth; i++) {
            shapeRenderer.line(i * cellSize, 0, i * cellSize, gridHeight * cellSize);
        }
        for (int j = 0; j <= gridHeight; j++) {
            shapeRenderer.line(0, j * cellSize, gridWidth * cellSize, j * cellSize);
        }
        shapeRenderer.end();

        ShapeRenderer sr = new ShapeRenderer();
        sr.begin(ShapeRenderer.ShapeType.Line);

        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                Cell cell = grid[x][y];
                if (cell.type == Cell.Type.PATH) sr.setColor(1, 0, 0, 0.5f); // red for path
                else sr.setColor(0, 1, 0, 0.5f); // green for tower
                sr.rect(cell.x, cell.y, cellSize, cellSize);
            }
        }
        sr.end();
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            paused = !paused;
        }
        if (!paused) {
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            // Handle input for tower placement
            if (Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) {
                int x = Gdx.input.getX() / cellSize;
                int y = (Gdx.graphics.getHeight() - Gdx.input.getY()) / cellSize; // invert Y
                if (x >= 0 && x < gridWidth && y >= 0 && y < gridHeight && !occupied[x][y] && grid[x][y].type == Cell.Type.TOWER) {
                    towers.add(new Tower(x * cellSize, y * cellSize));
                    occupied[x][y] = true;
                }
            }

            enemyAct(delta);

            // Update bullets and their effect on enemies
            removeDamagers(delta);

            spawnerAct();
            drawTowerRadii();
            drawActors();
            drawGrid();
        } else {
            pauseScreen();
        }
    }

    public void pauseScreen(){
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.7f); // semi-transparent overlay
        shapeRenderer.rect(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shapeRenderer.end();

        batch.begin();
        BitmapFont font = new BitmapFont(); // simple default font
        font.setColor(1, 1, 1, 1);
        font.getData().setScale(2f);
        font.draw(batch, "PAUSED", Gdx.graphics.getWidth() / 2f - 50, Gdx.graphics.getHeight() / 2f + 50);
        font.draw(batch, "Press ESC to resume", Gdx.graphics.getWidth() / 2f - 100, Gdx.graphics.getHeight() / 2f);
        batch.end();
    }

    public void removeDamagers(float delta){
        bullets.removeIf(b -> !b.update(delta));
        enemies.removeIf(e -> e.health < 1);
    }

    public void enemyAct(float delta){
        for (Enemy e : enemies) {
            e.update(delta, path, cellSize);
        }
    }
    public void drawTowerRadii(){
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(0, 0, 1, 0.4f);
        // Towers shoot respecting cooldown
        for (Tower tower : towers) {
            float cx = tower.xPos + 50;
            float cy = tower.yPos + 50;
            shapeRenderer.circle(cx, cy, tower.range);
            tower.cooldown--;
            if (tower.cooldown < 1) {
                Enemy target = tower.findTarget(enemies);
                if (target != null) {
                    bullets.add(tower.shoot(target));
                }
                tower.cooldown = tower.maxCooldown;
            }
        }
        shapeRenderer.end();
    }


    public void spawnerAct(){
        for (Spawner spawner : spawners) {
            spawner.cooldown--;
            if (spawner.cooldown < 1) {
                enemies.add(spawner.spawn());
                spawner.cooldown = 202020;
            }
        }
    }

    public void drawActors(){
        batch.begin();
        for (Enemy e : enemies) e.draw(batch);
        for (Tower t : towers) t.draw(batch);
        for (Bullet b : bullets) b.draw(batch);
        for (Spawner s : spawners) s.draw(batch);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        batch.dispose();
    }

    public void drawGrid(){
        // Draw the grid overlay
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        // optional: draw white cell lines
        shapeRenderer.setColor(1, 1, 1, 0.3f);
        for (int i = 0; i <= gridWidth; i++) {
            shapeRenderer.line(i * cellSize, 0, i * cellSize, gridHeight * cellSize);
        }
        for (int j = 0; j <= gridHeight; j++) {
            shapeRenderer.line(0, j * cellSize, gridWidth * cellSize, j * cellSize);
        }

        // Draw colored cells for PATH / TOWER
        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                Cell cell = grid[x][y];
                if (cell.type == Cell.Type.PATH) shapeRenderer.setColor(1, 0, 0, 0.5f); // red
                else shapeRenderer.setColor(0, 1, 0, 0.5f); // green
                shapeRenderer.rect(cell.x, cell.y, cellSize, cellSize);
            }
        }
        shapeRenderer.end();
    }
}
