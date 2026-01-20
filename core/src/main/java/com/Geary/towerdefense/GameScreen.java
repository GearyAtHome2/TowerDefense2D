package com.Geary.towerdefense;

import com.Geary.towerdefense.UI.CameraController;
import com.Geary.towerdefense.UI.gameUI.GameUI;
import com.Geary.towerdefense.UI.displays.modal.Modal;
import com.Geary.towerdefense.UI.displays.tooltip.UIClickManager;
import com.Geary.towerdefense.UI.displays.tooltip.UIManager;
import com.Geary.towerdefense.UI.displays.tooltip.entity.EntitySelectionHandler;
import com.Geary.towerdefense.UI.displays.tooltip.entity.EntityUI;
import com.Geary.towerdefense.UI.render.*;
import com.Geary.towerdefense.UI.render.icons.IconStore;
import com.Geary.towerdefense.UI.render.production.FactoryRenderer;
import com.Geary.towerdefense.entity.Entity;
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
    private EntitySelectionHandler entitySelectionHandler;

    private OrthographicCamera worldCamera;
    private Viewport worldViewport;

    private OrthographicCamera uiCamera;
    private Viewport uiViewport;

    private TransportRenderer transportRenderer;
    private TowerRenderer towerRenderer;
    private MineRenderer mineRenderer;
    private FactoryRenderer factoryRenderer;

    private UIManager uiManager;

    private Entity selectedEntity;
    private Entity highlightedEntity;
    private EntityUI activeEntityUI;

    private GameInputProcessor inputProcessor;
    private final Vector3 mouseWorld = new Vector3();

    private boolean escConsumedByMenu;

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

    private void initHandlers() {
        placementHandler = new PlacementHandler(
            world.getTowerManager(),
            world.getTransportManager(),
            world.getProductionManager(),
            world.getFactoryManager()
        );
        entitySelectionHandler = new EntitySelectionHandler(world, worldViewport);
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
//            world.getTowerManager(),
            world.getGameStateManager(),
            world.getTransportManager()
        );
        uiManager = new UIManager(world, shapeRenderer, batch, uiFont, uiCamera);
    }

    private void initInputProcessor() {
        inputProcessor = new GameInputProcessor(
            world.getTowerManager(),
            world.getProductionManager(),
            world.getTransportManager(),
            world.getFactoryManager(),
            cameraController,
            uiViewport
        );

        inputProcessor.setUiClickListener(gameUI::handleUiClick);
        inputProcessor.setWorldClickListener(this::handleWorldClick);
        inputProcessor.setUiScrollListener(gameUI);

        Gdx.input.setInputProcessor(inputProcessor);
    }

    private void handleWorldClick(int x, int y) {
        Modal modal = world.getActiveModal();
        if (modal != null) {
            modal.handleClick(x, y);
            if (modal.shouldClose()) {
                world.closeModal();
            }
            return;
        }

        // --- forward clicks to active entity UI buttons ---
        if (selectedEntity != null && activeEntityUI != null) {
            activeEntityUI.handleClick(x, y, worldCamera);
            if (selectedEntity instanceof Building building) {
                if (activeEntityUI.consumeDeleteRequest()) {
                    world.deleteBuilding(building);
                    clearEntitySelection();
                    return;
                }
            }
        }

        selectEntity(x, y);
    }


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

        drawWorld();
        gameUI.updateHover(Gdx.input.getX(), Gdx.input.getY());
        gameUI.drawUI(gameStateManager.paused, gameStateManager.gameSpeed);
    }

    private void updateMenus() {
        Modal modal = world.getActiveModal();
        if (modal != null) {
            modal.update();

            modal.updateHover(
                Gdx.input.getX(),
                Gdx.input.getY(),
                uiViewport
            );

            inputProcessor.setActiveModal(modal, worldCamera);
        } else {
            inputProcessor.setActiveModal(null, null);
        }
    }

    private void updatePlacement() {
        if (world.getActiveModal() != null) return;

        placementHandler.handlePlacements();
        placementHandler.handleKeyboardInput(
            Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT),
            Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT),
            Gdx.input.isKeyPressed(Input.Keys.Z),
            Gdx.input.isKeyPressed(Input.Keys.X)
        );
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
            highlightedEntity = null;
            return;
        }

        highlightedEntity = entitySelectionHandler.getEntityAtScreen(
            Gdx.input.getX(),
            Gdx.input.getY()
        );
    }


    private void drawWorld() {
        applyWorldCamera();

        batch.setProjectionMatrix(worldCamera.combined);
        shapeRenderer.setProjectionMatrix(worldCamera.combined);

        // --- World layers ---
        worldRenderer.drawGridLines();
        worldRenderer.drawCells();
        resourceRenderer.drawResources();

        worldRenderer.drawActors(
            shapeRenderer,
            world.getSparkManager(),
            towerRenderer,
            transportRenderer,
            mineRenderer,
            factoryRenderer
        );

        drawEntityHighlights();
        // --- Selected entity UI ---
        if (selectedEntity != null) {
            if (selectedEntity instanceof Mob mob && !mob.isAlive()) {
                clearEntitySelection();
            } else if (activeEntityUI != null) {
                activeEntityUI.drawPopup(selectedEntity, worldCamera.zoom);
            }
        }


        // --- Modal UI ---
        drawModal();
    }


    private void drawModal() {
        Modal menu = world.getActiveModal();
        if (menu == null) return;

        uiViewport.apply();
        batch.setProjectionMatrix(uiCamera.combined);
        shapeRenderer.setProjectionMatrix(uiCamera.combined);

        menu.layout();
        menu.draw(shapeRenderer, batch);
    }

    private void drawEntityHighlights() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        if (highlightedEntity != null) {
            drawEntityHighlight(highlightedEntity, new Color(1, 1, 0, 0.25f));
        }
        if (selectedEntity != null) {
            drawEntityHighlight(selectedEntity, new Color(1, 1, 1, 0.35f));
        }
        shapeRenderer.end();
    }

    private void drawEntityHighlight(Entity entity, Color color) {
        shapeRenderer.setColor(color);
        float r = entity.collisionRadius;
        shapeRenderer.rect(
            entity.xPos - r,
            entity.yPos - r,
            r * 2f,
            r * 2f
        );
    }

    private void selectEntity(int x, int y) {
        Entity entity = entitySelectionHandler.getEntityAtScreen(x, y);

        if (entity == null) {
            clearEntitySelection();
            return;
        }

        selectedEntity = entity;
        activeEntityUI = uiManager.getUIFor(entity);

    }

    private void clearEntitySelection() {
        selectedEntity = null;
        activeEntityUI = null;
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
        uiCamera.position.set(
            uiViewport.getWorldWidth() / 2f,
            uiViewport.getWorldHeight() / 2f,
            0
        );
        uiCamera.update();
    }

    private void applyWorldCamera() {
        worldViewport.apply();
        worldCamera.update();
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
