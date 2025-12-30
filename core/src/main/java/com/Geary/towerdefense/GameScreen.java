package com.Geary.towerdefense;

import com.Geary.towerdefense.UI.CameraController;
import com.Geary.towerdefense.UI.TowerRenderer;
import com.Geary.towerdefense.behaviour.EnemyManager;
import com.Geary.towerdefense.behaviour.SpawnerManager;
import com.Geary.towerdefense.behaviour.TowerManager;
import com.Geary.towerdefense.entity.Bullet;
import com.Geary.towerdefense.entity.Enemy;
import com.Geary.towerdefense.entity.Spawner;
import com.Geary.towerdefense.entity.Tower;
import com.Geary.towerdefense.entity.world.Cell;
import com.Geary.towerdefense.world.GameWorld;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameScreen implements Screen {

    private static final int UI_BAR_HEIGHT = 90;
    private static final float PLACE_TOWER_X = 80;
    private static final float PLACE_TOWER_Y = 20;
    private static final float PLACE_TOWER_WIDTH = 150;
    private static final float PLACE_TOWER_HEIGHT = 40;

    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont uiFont;

    private GameWorld world;
    private CameraController cameraController;
    private TowerManager towerManager;
    private EnemyManager enemyManager;
    private SpawnerManager spawnerManager;

    private OrthographicCamera uiCamera;
    private Viewport uiViewport;

    private OrthographicCamera worldCamera;
    private Viewport worldViewport;

    private TowerRenderer towerRenderer;

    private boolean paused = false;
    private float gameSpeed = 1f; // normal speed

    @Override
    public void show() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        uiFont = new BitmapFont();
        uiFont.getData().setScale(1.5f);

        // --- Initialize World & Managers ---
        world = new GameWorld();
        towerRenderer = new TowerRenderer(world);
        setupWorldCamera();
        setupUICamera();

        cameraController = new CameraController(worldCamera, worldViewport, world);
        towerManager = new TowerManager(world, worldCamera);
        enemyManager = new EnemyManager(world);
        spawnerManager = new SpawnerManager(world);

        // --- Input ---
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean scrolled(float amountX, float amountY) {
                cameraController.scrolled(amountX, amountY);
                return true;
            }
        });
    }

    @Override
    public void render(float delta) {
        setGameSpeed();
        delta = delta * gameSpeed;
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!paused) {
            cameraController.update();
            towerManager.handlePlacement();
            enemyManager.update(delta);
            spawnerManager.update(delta);
        }

        drawWorld(delta);
        drawUI();

        // Pause toggle
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) paused = !paused;

        // Handle tower placement button toggle
        if (Gdx.input.justTouched()) {
            Vector3 uiClick = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            uiViewport.unproject(uiClick);
            towerManager.togglePlacement(uiClick, PLACE_TOWER_X, PLACE_TOWER_Y, PLACE_TOWER_WIDTH, PLACE_TOWER_HEIGHT);
        }
    }

    private void drawWorld(float delta) {
        // --- World viewport ---
        worldViewport.apply();
        worldCamera.update();
        batch.setProjectionMatrix(worldCamera.combined);
        shapeRenderer.setProjectionMatrix(worldCamera.combined);

        drawGridLines();
//        drawBaseCells();
        drawPathCells();

        towerRenderer.drawTowerRanges(shapeRenderer);
        towerManager.updateTowers(world.bullets, delta);
        drawActors();
    }

    private void drawGridLines() {
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
    private void drawBaseCells() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (int x = 0; x < world.gridWidth; x++) {
            for (int y = 0; y < world.gridHeight; y++) {
                Cell cell = world.grid[x][y];
                if (cell.type == Cell.Type.TOWER) {
                    shapeRenderer.setColor(0, 1, 0, 0.35f);
                    shapeRenderer.rect(cell.x, cell.y, world.cellSize, world.cellSize);
                }
            }
        }

        shapeRenderer.end();
    }
    private void drawPathCells() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        for (int x = 0; x < world.gridWidth; x++) {
            for (int y = 0; y < world.gridHeight; y++) {
                Cell cell = world.grid[x][y];

                if (cell.type == Cell.Type.PATH) {
                    shapeRenderer.setColor(1f, 0f, 0f, 0.5f);

                } else if (cell.type == Cell.Type.TURN) {
                    shapeRenderer.setColor(0.9f, 0.6f, 0.3f, 0.6f);
                    float x0 = cell.x;
                    float y0 = cell.y;
                    float x1 = cell.x + GameWorld.cellSize;
                    float y1 = cell.y + GameWorld.cellSize;
                    if (cell.turnType == Cell.TurnType.TL_BR) {
                        // Top-left → bottom-right diagonal
                        shapeRenderer.line(x0, y1, x1, y0);
                    } else if (cell.turnType == Cell.TurnType.BL_TR) {
                        // Bottom-left → top-right diagonal
                        shapeRenderer.line(x0, y0, x1, y1);
                    }
                } else {
                    continue;
                }

                shapeRenderer.rect(cell.x, cell.y, world.cellSize, world.cellSize);
            }
        }
        shapeRenderer.end();
    }

    private void drawActors() {
        batch.begin();
        for (Enemy e : world.enemies) e.draw(batch);
        for (Tower t : world.towers) t.draw(batch);
        for (Bullet b : world.bullets) b.draw(batch);
        for (Spawner s : world.spawners) s.draw(batch);
        batch.end();
    }

    private void drawUI() {
        uiViewport.apply();
        shapeRenderer.setProjectionMatrix(uiCamera.combined);
        batch.setProjectionMatrix(uiCamera.combined);

        // --- UI bar ---
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0.1f, 0.1f, 0.1f, 1f);
        shapeRenderer.rect(0, 0, uiViewport.getWorldWidth(), UI_BAR_HEIGHT);
        shapeRenderer.end();

        // --- Tower button ---
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(towerManager.isPlacementActive() ? 0f : 0.3f, 0.6f, 0.3f, 1f);
        shapeRenderer.rect(PLACE_TOWER_X, PLACE_TOWER_Y, PLACE_TOWER_WIDTH, PLACE_TOWER_HEIGHT);
        shapeRenderer.end();

        batch.begin();
        uiFont.draw(batch, "Place Tower", PLACE_TOWER_X + 10, PLACE_TOWER_Y + 25);
        uiFont.draw(batch, "ESC = Pause", 20, UI_BAR_HEIGHT - 20);
        uiFont.draw(batch, "Towers: " + world.towers.size(), 200, UI_BAR_HEIGHT - 20);
        uiFont.draw(batch, "Enemies: " + world.enemies.size(), 400, UI_BAR_HEIGHT - 20);
        uiFont.draw(batch, "gamespeed: " + gameSpeed, 400, UI_BAR_HEIGHT - 50);


        if (paused) {
            uiFont.getData().setScale(2.5f);
            uiFont.draw(batch, "PAUSED", uiViewport.getWorldWidth() / 2f - 70, uiViewport.getWorldHeight() / 2f);
            uiFont.getData().setScale(1.5f);
        }
        batch.end();
    }

    private void setupWorldCamera() {
        worldCamera = new OrthographicCamera();
        worldViewport = new ScreenViewport(worldCamera);
        worldViewport.setScreenBounds(
            0,
            UI_BAR_HEIGHT,
            Gdx.graphics.getWidth(),
            Gdx.graphics.getHeight() - UI_BAR_HEIGHT
        );
        worldCamera.position.set(
            (world.gridWidth * world.cellSize) / 2f,
            (world.gridHeight * world.cellSize) / 2f,
            0
        );
        worldCamera.update();
    }

    private void setupUICamera() {
        uiCamera = new OrthographicCamera();
        uiViewport = new ScreenViewport(uiCamera);
        uiCamera.position.set(uiViewport.getWorldWidth() / 2f, uiViewport.getWorldHeight() / 2f, 0);
        uiCamera.update();
    }

    public void setGameSpeed(){
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) gameSpeed = 1f;
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) gameSpeed = 2f;
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) gameSpeed = 4f;
    }

    @Override
    public void resize(int width, int height) {
        if (worldViewport != null) worldViewport.update(width, height, true);
        if (uiViewport != null) uiViewport.update(width, height, true);
    }

    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void hide() {}
    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        uiFont.dispose();
    }
}
