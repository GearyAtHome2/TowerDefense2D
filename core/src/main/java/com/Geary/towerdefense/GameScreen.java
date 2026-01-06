package com.Geary.towerdefense;

import com.Geary.towerdefense.UI.BuildingUI;
import com.Geary.towerdefense.UI.CameraController;
import com.Geary.towerdefense.UI.GameUI;
import com.Geary.towerdefense.UI.displays.BuildingSelectionHandler;
import com.Geary.towerdefense.UI.render.*;
import com.Geary.towerdefense.entity.buildings.Building;
import com.Geary.towerdefense.world.GameStateManager;
import com.Geary.towerdefense.world.GameWorld;
import com.Geary.towerdefense.world.PlacementHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
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
    private ResourceRenderer resourceRenderer;

    private GameWorld world;
    private CameraController cameraController;
//    private TowerManager towerManager;
//    private TransportManager transportManager;
//    private MineManager mineManager;
//    private SparkManager sparkManager;
//    private MobManager mobManager;
//    private SpawnerManager spawnerManager;

    private PlacementHandler placementHandler;
    private BuildingSelectionHandler buildingSelectionHandler;

    private OrthographicCamera uiCamera;
    private Viewport uiViewport;

    private OrthographicCamera worldCamera;
    private Viewport worldViewport;

    private TransportRenderer transportRenderer;
    private TowerRenderer towerRenderer;
    private MineRenderer mineRenderer;

    private BuildingUI buildingUI;

    private GameInputProcessor inputProcessor;
    private GameStateManager gameStateManager;

    private Building selectedBuilding = null;

    @Override
    public void show() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        shapeRenderer.setAutoShapeType(true);
        uiFont = new BitmapFont();
        uiFont.getData().setScale(1.5f);
        world = new GameWorld();
        setupWorldCamera();
        world.initManagers(worldCamera);
        setupUICamera();
        initManagers();
        initHandlers();
        initRenderers();
        initUI();
        initInputProcessor();
    }

    private void initManagers() {
        gameStateManager = new GameStateManager();
        cameraController = new CameraController(worldCamera, worldViewport, world);
    }

    public void initHandlers() {
        placementHandler = new PlacementHandler(world.getTowerManager(), world.getTransportManager(), world.getMineManager());
        buildingSelectionHandler = new BuildingSelectionHandler(world, worldViewport);
    }

    private void initRenderers() {
        towerRenderer = new TowerRenderer(world, shapeRenderer);
        transportRenderer = new TransportRenderer(world, shapeRenderer);
        worldRenderer = new WorldRenderer(world, shapeRenderer);
        resourceRenderer = new ResourceRenderer(world, shapeRenderer);
        mineRenderer = new MineRenderer(world, shapeRenderer);
    }

    private void initUI() {
        gameUI = new GameUI(shapeRenderer, batch, uiFont, uiViewport, world, world.getTowerManager(), world.getTransportManager());
        buildingUI = new BuildingUI(world, shapeRenderer, batch, uiFont);
    }

    private void initInputProcessor() {
        inputProcessor = new GameInputProcessor(world.getTowerManager(), world.getMineManager(), world.getTransportManager(), cameraController, uiViewport);

        inputProcessor.setUiClickListener(uiClick -> gameUI.handleUiClick(uiClick));

        inputProcessor.setWorldClickListener((x, y) -> {
            Building clicked = buildingSelectionHandler.getBuildingAtScreen(x, y);

            if (selectedBuilding != null) {
                buildingUI.handleClick(x, y, worldCamera);
                if (buildingUI.consumeDeleteRequest()) {
                    world.deleteBuilding(selectedBuilding);
                    selectedBuilding = null;
                    return;
                }
            }

            if (clicked != null) {
                selectedBuilding = clicked;
            } else {
                selectedBuilding = null;
                buildingUI.clear();
            }
        });

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
            placementHandler.handlePlacements();
            world.update(delta);
        }
        placementHandler.handleKeyboardInput(
            Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT),
            Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT),
            Gdx.input.isKeyPressed(Input.Keys.Z)
        );

        drawWorld(delta);
        gameUI.drawUI(gameStateManager.paused, gameStateManager.gameSpeed);
    }

    private void drawWorld(float delta) {
        drawWorldStatic();
        drawWorldActors(delta);
    }

    private void drawWorldStatic() {
        worldViewport.apply();
        worldCamera.update();
        batch.setProjectionMatrix(worldCamera.combined);
        shapeRenderer.setProjectionMatrix(worldCamera.combined);
        worldRenderer.drawGridLines();
        worldRenderer.drawCells();
        resourceRenderer.drawResources();
    }

    private void drawWorldActors(float delta) {
        worldRenderer.drawActors(batch, world.getSparkManager(), towerRenderer, transportRenderer, mineRenderer);
        if (selectedBuilding != null) {
            buildingUI.drawBuildingPopup(selectedBuilding, worldCamera.zoom);
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
        uiCamera = createCamera(uiViewport, uiViewport.getWorldWidth() / 2f, uiViewport.getWorldHeight() / 2f);
    }

    private OrthographicCamera createCamera(Viewport viewport, float posX, float posY) {
        OrthographicCamera cam = new OrthographicCamera();
        viewport.setCamera(cam);
        cam.position.set(posX, posY, 0);
        cam.update();
        return cam;
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
