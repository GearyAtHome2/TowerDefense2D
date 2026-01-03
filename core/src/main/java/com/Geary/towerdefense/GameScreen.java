package com.Geary.towerdefense;

import com.Geary.towerdefense.UI.CameraController;
import com.Geary.towerdefense.UI.GameUI;
import com.Geary.towerdefense.UI.TowerUI;
import com.Geary.towerdefense.UI.render.TowerRenderer;
import com.Geary.towerdefense.UI.render.WorldRenderer;
import com.Geary.towerdefense.behaviour.MobManager;
import com.Geary.towerdefense.behaviour.SparkManager;
import com.Geary.towerdefense.behaviour.SpawnerManager;
import com.Geary.towerdefense.behaviour.TowerManager;
import com.Geary.towerdefense.entity.Tower;
import com.Geary.towerdefense.world.GameStateManager;
import com.Geary.towerdefense.world.GameWorld;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
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

    private static final int SPARK_POOLSIZE = 100;

    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont uiFont;

    private GameUI gameUI;
    private WorldRenderer worldRenderer;

    private GameWorld world;
    private CameraController cameraController;
    private TowerManager towerManager;
    private SparkManager sparkManager;
    private MobManager mobManager;
    private SpawnerManager spawnerManager;

    private OrthographicCamera uiCamera;
    private Viewport uiViewport;

    private OrthographicCamera worldCamera;
    private Viewport worldViewport;

    private TowerRenderer towerRenderer;
    private TowerUI towerUI;

    private GameInputProcessor inputProcessor;
    private GameStateManager gameStateManager;

    private Tower selectedTower = null;

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
        sparkManager = new SparkManager(SPARK_POOLSIZE);
        mobManager = new MobManager(world, sparkManager);
        spawnerManager = new SpawnerManager(world);

        gameUI = new GameUI(shapeRenderer, batch, uiFont, uiViewport, world, towerManager);
        gameStateManager = new GameStateManager();
        worldRenderer = new WorldRenderer(world, shapeRenderer);
        towerUI = new TowerUI(world, shapeRenderer, batch, uiFont);
        inputProcessor = new GameInputProcessor(towerManager, cameraController, uiViewport);
        inputProcessor.setTowerClickListener(this::handleWorldClickAt); // pass your existing method
        Gdx.input.setInputProcessor(inputProcessor);
    }

    @Override
    public void render(float delta) {
        gameStateManager.updateGameSpeedKeys();
        delta *= gameStateManager.gameSpeed;
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (!gameStateManager.paused) {
            cameraController.update();
            towerManager.handlePlacement();
            mobManager.update(delta);
            spawnerManager.update(delta);
            sparkManager.update(delta);
        }

        drawWorld(delta);
        gameUI.drawUI(gameStateManager.paused, gameStateManager.gameSpeed);

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) gameStateManager.togglePause();

        towerManager.togglePlacementKp(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT));
        if (towerManager.isPlacementActive()) {
            selectedTower = null;
        }
    }

    private void drawWorld(float delta) {
        worldViewport.apply();
        worldCamera.update();
        batch.setProjectionMatrix(worldCamera.combined);
        shapeRenderer.setProjectionMatrix(worldCamera.combined);
        worldRenderer.drawGridLines();
        worldRenderer.drawCells();
        towerRenderer.drawTowerRanges(shapeRenderer);
        if (!gameStateManager.paused) {
            towerManager.updateTowers(world.bullets, delta);
        }

        worldRenderer.drawActors(batch, sparkManager, towerRenderer);

        if (selectedTower != null) {
            towerUI.drawTowerPopup(selectedTower, worldCamera.zoom);
        }
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

    private boolean towerContains(Tower tower, float x, float y) {
        return x >= tower.xPos
            && x <= tower.xPos + world.cellSize
            && y >= tower.yPos
            && y <= tower.yPos + world.cellSize;
    }

    private void handleWorldClickAt(int screenX, int screenY) {
        Vector3 worldClick = new Vector3(screenX, screenY, 0);
        worldViewport.unproject(worldClick);

        Tower clicked = null;
        for (Tower tower : world.towers) {
            if (towerContains(tower, worldClick.x, worldClick.y)) {
                clicked = tower;
                break;
            }
        }

        selectedTower = clicked;
    }

    @Override
    public void resize(int width, int height) {
        if (worldViewport != null) worldViewport.update(width, height, true);
        if (uiViewport != null) uiViewport.update(width, height, true);
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
        shapeRenderer.dispose();
        uiFont.dispose();
    }
}
