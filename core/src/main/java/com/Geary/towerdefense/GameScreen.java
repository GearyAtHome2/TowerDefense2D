package com.Geary.towerdefense;

import com.Geary.towerdefense.UI.CameraController;
import com.Geary.towerdefense.UI.GameUI;
import com.Geary.towerdefense.UI.displays.building.*;
import com.Geary.towerdefense.UI.displays.building.specialized.factory.FactoryMenu;
import com.Geary.towerdefense.UI.displays.mob.MobSelectionHandler;
import com.Geary.towerdefense.UI.render.*;
import com.Geary.towerdefense.UI.render.icons.IconStore;
import com.Geary.towerdefense.entity.Entity;
import com.Geary.towerdefense.entity.buildings.Building;
import com.Geary.towerdefense.entity.mob.Mob;
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

    private PlacementHandler placementHandler;
    private BuildingSelectionHandler buildingSelectionHandler;
    private MobSelectionHandler mobSelectionHandler;

    private OrthographicCamera uiCamera;
    private Viewport uiViewport;

    private OrthographicCamera worldCamera;
    private Viewport worldViewport;

    private TransportRenderer transportRenderer;
    private TowerRenderer towerRenderer;
    private MineRenderer mineRenderer;
    private FactoryRenderer factoryRenderer;

    private BuildingUIManager buildingUIManager;
    private Building selectedBuilding = null;
    private BuildingUI activeBuildingUI;

    private UIManager uIManager;
    private Entity selectedMob = null;
    private EntityUI activeMobUI;

    private GameInputProcessor inputProcessor;
    private GameStateManager gameStateManager;



    boolean escConsumedByMenu = false;

    @Override
    public void show() {
        IconStore.load();
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
        placementHandler = new PlacementHandler(world.getTowerManager(), world.getTransportManager(), world.getMineManager(), world.getFactoryManager());
        buildingSelectionHandler = new BuildingSelectionHandler(world, worldViewport);
        mobSelectionHandler = new MobSelectionHandler(world, worldViewport);
    }

    private void initRenderers() {
        towerRenderer = new TowerRenderer(world, shapeRenderer);
        transportRenderer = new TransportRenderer(world, shapeRenderer);
        worldRenderer = new WorldRenderer(world, shapeRenderer);
        resourceRenderer = new ResourceRenderer(world, shapeRenderer);
        mineRenderer = new MineRenderer(world, shapeRenderer);
        factoryRenderer = new FactoryRenderer(world, shapeRenderer);
    }

    private void initUI() {
        gameUI = new GameUI(shapeRenderer, batch, uiFont, uiViewport, world, world.getTowerManager(), world.getTransportManager(), world.getGameStateManager());

        buildingUIManager = new BuildingUIManager(world, shapeRenderer, batch, uiFont);
        uIManager = new UIManager(world, shapeRenderer, batch, uiFont);
        activeBuildingUI = null; // default UI
        activeMobUI = null; // default UI
    }

    private void initInputProcessor() {
        inputProcessor = new GameInputProcessor(world.getTowerManager(), world.getMineManager(), world.getTransportManager(), world.getFactoryManager(), cameraController, uiViewport);

        inputProcessor.setUiClickListener(uiClick -> gameUI.handleUiClick(uiClick));

        inputProcessor.setWorldClickListener((x, y) -> {

            if (world.getActiveFactoryMenu() != null) {
                boolean consumed = world.getActiveFactoryMenu()
                    .handleClick(x, y);

                if (world.getActiveFactoryMenu().shouldClose()) {
                    world.closeFactoryMenu();
                }

                if (consumed) {
                    return; // 🚨 STOP propagation
                }
            }
            // First: forward click to current UI (for buttons)
            if (selectedBuilding != null && activeBuildingUI != null) {
                activeBuildingUI.handleClick(x, y, worldCamera);
                if (activeBuildingUI.consumeDeleteRequest()) {
                    world.deleteBuilding(selectedBuilding);
                    selectedBuilding = null;
                    activeBuildingUI = null;
                    return;
                }
            }

            Building clickedBuilding = buildingSelectionHandler.getBuildingAtScreen(x, y);
            if (clickedBuilding != null) {

                selectedBuilding = clickedBuilding;
                activeBuildingUI = buildingUIManager.getUIFor(clickedBuilding);
            } else {
                selectedBuilding = null;
                activeBuildingUI = null;
            }
            Mob clickedMob = mobSelectionHandler.getMobAtScreen(x, y);
            if (clickedMob != null) {
                System.out.println("mob clicked.");
                selectedMob = clickedMob;
                activeMobUI = uIManager.getUIFor(clickedMob);
            } else {
                selectedMob = null;
                activeMobUI = null;
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
        FactoryMenu menu = world.getActiveFactoryMenu();
        if (menu != null) {
            menu.update();
            menu.updateHover(Gdx.input.getX(), Gdx.input.getY(), uiViewport);
            if (menu.shouldClose()) {
                world.closeFactoryMenu();
                escConsumedByMenu = true;
            }
            inputProcessor.setActiveModal(menu, worldCamera);
        } else {
            //only handle placements if we're not in a menu
            placementHandler.handlePlacements();
            placementHandler.handleKeyboardInput(
                Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT),
                Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT),
                Gdx.input.isKeyPressed(Input.Keys.Z),
                Gdx.input.isKeyPressed(Input.Keys.X));
            inputProcessor.setActiveModal(null, null);
        }
        if (!escConsumedByMenu && Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            gameStateManager.togglePause();
        }
        cameraController.update();
        if (!gameStateManager.paused) {
            world.update(delta);
        }

        drawWorld(delta);
        gameUI.updateHover(Gdx.input.getX(), Gdx.input.getY());
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
        worldRenderer.drawActors(shapeRenderer, world.getSparkManager(), towerRenderer, transportRenderer, mineRenderer, factoryRenderer);
        if (selectedBuilding != null) {
            activeBuildingUI.drawPopup(selectedBuilding, worldCamera.zoom);
        }

        if (selectedMob != null) {
            if (selectedMob instanceof Mob && ((Mob) selectedMob).health < 1){
                selectedMob = null;
            } else {
                activeMobUI.drawPopup(selectedMob, worldCamera.zoom);
            }
        }
        if (world.getActiveFactoryMenu() != null) {
            world.getActiveFactoryMenu().layout(); // layout for size/position

            uiViewport.apply();
            batch.setProjectionMatrix(uiCamera.combined);
            shapeRenderer.setProjectionMatrix(uiCamera.combined);
            world.getActiveFactoryMenu().draw(shapeRenderer, batch); // pass UI camera
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
        uiViewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        uiCamera.position.set(uiViewport.getWorldWidth() / 2f, uiViewport.getWorldHeight() / 2f, 0);
        uiCamera.update();
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
