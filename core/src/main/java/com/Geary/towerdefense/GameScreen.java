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
    private static final float PLACE_TOWER_Y = 10;
    private static final float PLACE_TOWER_WIDTH = 150;
    private static final float PLACE_TOWER_HEIGHT = 40;

    private int lastMouseX, lastMouseY;

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

    private Tower selectedTower = null;
    private float popupScale = 1f;

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

            private Vector3 touchDownScreen = new Vector3();
            private boolean isDraggingCamera = false;
            private static final float DRAG_THRESHOLD = 5f;

            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button) {
                if (button == Input.Buttons.LEFT) {
                    touchDownScreen.set(screenX, screenY, 0);
                    isDraggingCamera = false;
                }
                if (button == Input.Buttons.RIGHT) {
                    lastMouseX = screenX;
                    lastMouseY = screenY;
                }
                return false;
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
                    int dx = screenX - lastMouseX;
                    int dy = screenY - lastMouseY;
                    cameraController.dragBy(dx, dy);
                    lastMouseX = screenX;
                    lastMouseY = screenY;
                }
                // Check for accidental left-click drag
                float dx = Math.abs(screenX - touchDownScreen.x);
                float dy = Math.abs(screenY - touchDownScreen.y);
                if (dx > DRAG_THRESHOLD || dy > DRAG_THRESHOLD) {
                    isDraggingCamera = true;
                }

                return false;
            }

            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                if (button != Input.Buttons.LEFT) return false;
                // Convert click to UI coordinates first
                Vector3 uiClick = new Vector3(screenX, screenY, 0);
                uiViewport.unproject(uiClick);
                // --- 1️⃣ UI click check: Place Tower button ---
                if (uiClick.x >= PLACE_TOWER_X && uiClick.x <= PLACE_TOWER_X + PLACE_TOWER_WIDTH &&
                    uiClick.y >= PLACE_TOWER_Y && uiClick.y <= PLACE_TOWER_Y + PLACE_TOWER_HEIGHT) {

                    towerManager.togglePlacementClick(uiClick, PLACE_TOWER_X, PLACE_TOWER_Y, PLACE_TOWER_WIDTH, PLACE_TOWER_HEIGHT);
                    return true;
                }
                // --- 2️⃣ World click: only if not dragging and not placing a tower ---
                if (!towerManager.isPlacementActive() && !isDraggingCamera) {
                    handleWorldClickAt(screenX, screenY);
                    return true;
                }
                return false;
            }

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

        towerManager.togglePlacementKp(Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT));
//        if (Gdx.input.justTouched()) {
//            Vector3 uiClick = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
//            uiViewport.unproject(uiClick);
//            towerManager.togglePlacementClick(uiClick, PLACE_TOWER_X, PLACE_TOWER_Y, PLACE_TOWER_WIDTH, PLACE_TOWER_HEIGHT);
//        }
//        if (Gdx.input.justTouched() && !towerManager.isPlacementActive()) {
//            handleWorldClick();
//        }
        if (towerManager.isPlacementActive()) {
            selectedTower = null;
        }
    }

    private void drawWorld(float delta) {
        // --- World viewport ---
        worldViewport.apply();
        worldCamera.update();
        batch.setProjectionMatrix(worldCamera.combined);
        shapeRenderer.setProjectionMatrix(worldCamera.combined);

        drawGridLines();
        drawBaseCells();
        drawPathCells();

        towerRenderer.drawTowerRanges(shapeRenderer);
        towerManager.updateTowers(world.bullets, delta);
        if (selectedTower != null) {
            drawTowerPopup(selectedTower);
        }
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
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        for (int x = 0; x < world.gridWidth; x++) {
            for (int y = 0; y < world.gridHeight; y++) {
                Cell cell = world.grid[x][y];
                if (cell.type == Cell.Type.TOWER) {
                    shapeRenderer.setColor(0f, 0.8f, 0f, 0.35f);
                    shapeRenderer.rect(cell.x, cell.y, world.cellSize, world.cellSize);

                } else if (cell.type == Cell.Type.HOME) {
                    shapeRenderer.setColor(0f, 1f, 0f, 0.2f);
                    shapeRenderer.rect(cell.x, cell.y, world.cellSize, world.cellSize);

                } else if (cell.type == Cell.Type.ENEMY) {
                    shapeRenderer.setColor(1f, 0f, 0f, 0.2f);
                    shapeRenderer.rect(cell.x, cell.y, world.cellSize, world.cellSize);
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
        uiFont.draw(batch, "Place Tower", PLACE_TOWER_X + 15, PLACE_TOWER_Y + 30);
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

    public void setGameSpeed() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) gameSpeed = 1f;
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) gameSpeed = 2f;
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) gameSpeed = 4f;
    }

    private void handleWorldClick() {
        Vector3 worldClick = new Vector3(
            Gdx.input.getX(),
            Gdx.input.getY(),
            0
        );

        worldViewport.unproject(worldClick);

        selectedTower = null;

        for (Tower tower : world.towers) {
            if (towerContains(tower, worldClick.x, worldClick.y)) {
                selectedTower = tower;
                break;
            }
        }
    }

    private boolean towerContains(Tower tower, float x, float y) {
        return x >= tower.xPos
            && x <= tower.xPos + world.cellSize
            && y >= tower.yPos
            && y <= tower.yPos + world.cellSize;
    }

    private void drawTowerPopup(Tower tower) {
        float baseWidth = 140;
        float baseHeight = 80;
        float padding = 8;

        float scale = getPopupScale();

        float scaledWidth = baseWidth * scale;
        float scaledHeight = baseHeight * scale;

        float x = tower.xPos + world.cellSize + 5;
        float y = tower.yPos + world.cellSize;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(1f, 1f, 0f, 1f);
        shapeRenderer.rect(
            selectedTower.xPos,
            selectedTower.yPos,
            world.cellSize,
            world.cellSize
        );
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0f, 0f, 0f, 0.8f);
        shapeRenderer.rect(x, y, scaledWidth, scaledHeight);
        shapeRenderer.end();

        batch.begin();
        float originalScaleX = uiFont.getData().scaleX;
        float originalScaleY = uiFont.getData().scaleY;
        float fontScale = 1f + (scale - 1f) * 0.65f; // smaller response
        uiFont.getData().setScale(originalScaleX * fontScale, originalScaleY * fontScale);

        uiFont.draw(batch, "Tower", x + padding, y + scaledHeight);
        uiFont.draw(batch, "Damage: " + tower.cooldown, x + padding, y + scaledHeight - 25 * scale);
        uiFont.draw(batch, "Range: " + tower.range, x + padding, y + scaledHeight - 50 * scale);
        batch.end();
        uiFont.getData().setScale(originalScaleX, originalScaleY);//reset scale to prevent explosion
    }

    private float getPopupScale() {
        float zoom = worldCamera.zoom;
        float target = 1f + (zoom - 1f) * 0.65f;
        target = Math.max(0.5f, Math.min(3f, target));
        popupScale = com.badlogic.gdx.math.MathUtils.lerp(popupScale, target, 0.1f);
        return popupScale;
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
