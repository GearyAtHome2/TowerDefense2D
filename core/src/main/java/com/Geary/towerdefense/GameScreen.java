package com.Geary.towerdefense;

import com.Geary.towerdefense.UI.CameraController;
import com.Geary.towerdefense.UI.GameUI;
import com.Geary.towerdefense.UI.displays.UIClickManager;
import com.Geary.towerdefense.UI.displays.building.*;
import com.Geary.towerdefense.UI.displays.building.specialized.factory.FactoryMenu;
import com.Geary.towerdefense.UI.displays.mob.MobSelectionHandler;
import com.Geary.towerdefense.UI.render.*;
import com.Geary.towerdefense.UI.render.icons.IconStore;
import com.Geary.towerdefense.entity.buildings.Building;
import com.Geary.towerdefense.entity.mob.Mob;
import com.Geary.towerdefense.world.GameStateManager;
import com.Geary.towerdefense.world.GameWorld;
import com.Geary.towerdefense.world.PlacementHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
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

    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont uiFont;

    private GameWorld world;
    private GameStateManager gameStateManager;
    private CameraController cameraController;

    private GameUI gameUI;
    private WorldRenderer worldRenderer;
    private ResourceRenderer resourceRenderer;

    private PlacementHandler placementHandler;
    private BuildingSelectionHandler buildingSelectionHandler;
    private MobSelectionHandler mobSelectionHandler;

    private OrthographicCamera worldCamera;
    private Viewport worldViewport;

    private OrthographicCamera uiCamera;
    private Viewport uiViewport;

    private TransportRenderer transportRenderer;
    private TowerRenderer towerRenderer;
    private MineRenderer mineRenderer;
    private FactoryRenderer factoryRenderer;

    private BuildingUIManager buildingUIManager;
    private UIManager uiManager;

    private Building selectedBuilding;
    private BuildingUI activeBuildingUI;

    private Mob selectedMob;
    private Mob highlightedMob;
    private EntityUI activeMobUI;

    private GameInputProcessor inputProcessor;

    private final Vector3 mouseWorld = new Vector3();

    private boolean escConsumedByMenu = false;

    // ------------------------------------------------------------------------

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

    // ------------------------------------------------------------------------
    // Initialisation
    // ------------------------------------------------------------------------

    private void initManagers() {
        gameStateManager = new GameStateManager();
        cameraController = new CameraController(worldCamera, worldViewport, world);
    }

    private void initHandlers() {
        placementHandler = new PlacementHandler(
            world.getTowerManager(),
            world.getTransportManager(),
            world.getMineManager(),
            world.getFactoryManager()
        );

        buildingSelectionHandler = new BuildingSelectionHandler(world, worldViewport);
        mobSelectionHandler = new MobSelectionHandler(world, worldViewport);
    }

    private void initRenderers() {
        towerRenderer = new TowerRenderer(world, shapeRenderer);
        transportRenderer = new TransportRenderer(world, shapeRenderer);
        mineRenderer = new MineRenderer(world, shapeRenderer);
        factoryRenderer = new FactoryRenderer(world, shapeRenderer);

        worldRenderer = new WorldRenderer(world, shapeRenderer);
        resourceRenderer = new ResourceRenderer(world, shapeRenderer);
    }

    private void initUI() {
        gameUI = new GameUI(
            shapeRenderer,
            batch,
            uiFont,
            uiViewport,
            world,
            world.getTowerManager(),
            world.getTransportManager(),
            world.getGameStateManager()
        );

        buildingUIManager = new BuildingUIManager(world, shapeRenderer, batch, uiFont);
        uiManager = new UIManager(world, shapeRenderer, batch, uiFont);
    }

    // ------------------------------------------------------------------------
    // Input
    // ------------------------------------------------------------------------

    private void initInputProcessor() {
        inputProcessor = new GameInputProcessor(
            world.getTowerManager(),
            world.getMineManager(),
            world.getTransportManager(),
            world.getFactoryManager(),
            cameraController,
            uiViewport
        );

        inputProcessor.setUiClickListener(gameUI::handleUiClick);

        inputProcessor.setWorldClickListener((x, y) -> {

            FactoryMenu menu = world.getActiveFactoryMenu();
            if (menu != null) {
                boolean consumed = menu.handleClick(x, y);

                if (menu.shouldClose()) {
                    world.closeFactoryMenu();
                }

                if (consumed) return;
            }

            if (selectedBuilding != null && activeBuildingUI != null) {
                activeBuildingUI.handleClick(x, y, worldCamera);
                if (activeBuildingUI.consumeDeleteRequest()) {
                    world.deleteBuilding(selectedBuilding);
                    clearBuildingSelection();
                    return;
                }
            }

            selectBuilding(x, y);
            selectMob(x, y);
        });

        Gdx.input.setInputProcessor(inputProcessor);
    }

    // ------------------------------------------------------------------------
    // Render Loop
    // ------------------------------------------------------------------------

    @Override
    public void render(float delta) {
        gameStateManager.updateGameSpeedKeys();
        delta *= gameStateManager.gameSpeed;

        clearScreen();

        updateMenus();
        updatePlacement();
        updatePause();
        updateWorld(delta);
        updateHover();

        drawWorld(delta);
        gameUI.updateHover(Gdx.input.getX(), Gdx.input.getY());
        gameUI.drawUI(gameStateManager.paused, gameStateManager.gameSpeed);
    }

    // ------------------------------------------------------------------------
    // Updates
    // ------------------------------------------------------------------------

    private void updateMenus() {
        FactoryMenu menu = world.getActiveFactoryMenu();
        if (menu != null) {
            menu.update();
            menu.updateHover(Gdx.input.getX(), Gdx.input.getY(), uiViewport);
            inputProcessor.setActiveModal(menu, worldCamera);
        } else {
            inputProcessor.setActiveModal(null, null);
        }
    }

    private void updatePlacement() {
        if (world.getActiveFactoryMenu() == null) {
            placementHandler.handlePlacements();
            placementHandler.handleKeyboardInput(
                Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT),
                Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT),
                Gdx.input.isKeyPressed(Input.Keys.Z),
                Gdx.input.isKeyPressed(Input.Keys.X)
            );
        }
    }

    private void updatePause() {
        if (!escConsumedByMenu && Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            gameStateManager.togglePause();
        }
    }

    private void updateWorld(float delta) {
        cameraController.update();
        if (!gameStateManager.paused) {
            world.update(delta);
        }
    }

    private void updateHover() {
        if (!UIClickManager.isClickInGameArea(Gdx.input.getY())) {
            highlightedMob = null;
            return;
        }

        worldViewport.apply();
        worldCamera.update();

        mouseWorld.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        worldViewport.unproject(mouseWorld);

        highlightedMob = mobSelectionHandler.getMobAtWorld(mouseWorld.x, mouseWorld.y);
    }

    // ------------------------------------------------------------------------
    // Drawing
    // ------------------------------------------------------------------------

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
        worldRenderer.drawActors(
            shapeRenderer,
            world.getSparkManager(),
            towerRenderer,
            transportRenderer,
            mineRenderer,
            factoryRenderer
        );

        if (selectedBuilding != null && activeBuildingUI != null) {
            activeBuildingUI.drawPopup(selectedBuilding, worldCamera.zoom);
        }

        if (selectedMob != null) {
            if (selectedMob.health < 1) {
                clearMobSelection();
            } else {
                activeMobUI.drawPopup(selectedMob, worldCamera.zoom);
            }
        }

        drawHighlightedMob();

        if (world.getActiveFactoryMenu() != null) {
            uiViewport.apply();
            batch.setProjectionMatrix(uiCamera.combined);
            shapeRenderer.setProjectionMatrix(uiCamera.combined);
            world.getActiveFactoryMenu().layout();
            world.getActiveFactoryMenu().draw(shapeRenderer, batch);
        }
    }

    private void drawHighlightedMob() {
        if (highlightedMob == null || highlightedMob == selectedMob) return;

        float r = highlightedMob.collisionRadius;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(Color.YELLOW);
        shapeRenderer.rect(
            highlightedMob.xPos - r,
            highlightedMob.yPos - r,
            (r * 2f),
            (r * 2f)
        );
        shapeRenderer.end();
    }

    // ------------------------------------------------------------------------
    // Selection Helpers
    // ------------------------------------------------------------------------

    private void selectBuilding(int x, int y) {
        Building b = buildingSelectionHandler.getBuildingAtScreen(x, y);
        if (b != null) {
            selectedBuilding = b;
            activeBuildingUI = buildingUIManager.getUIFor(b);
        } else {
            clearBuildingSelection();
        }
    }

    private void selectMob(int x, int y) {
        Mob mob = mobSelectionHandler.getMobAtScreen(x, y);
        if (mob != null) {
            selectedMob = mob;
            activeMobUI = uiManager.getUIFor(mob);
        } else {
            clearMobSelection();
        }
    }

    private void clearBuildingSelection() {
        selectedBuilding = null;
        activeBuildingUI = null;
    }

    private void clearMobSelection() {
        selectedMob = null;
        activeMobUI = null;
    }

    // ------------------------------------------------------------------------
    // Cameras
    // ------------------------------------------------------------------------

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
        uiCamera.position.set(
            uiViewport.getWorldWidth() / 2f,
            uiViewport.getWorldHeight() / 2f,
            0
        );
        uiCamera.update();
    }


    private void clearScreen() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    @Override
    public void resize(int width, int height) {
        worldViewport.update(width, height, true);
        uiViewport.update(width, height, true);
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
