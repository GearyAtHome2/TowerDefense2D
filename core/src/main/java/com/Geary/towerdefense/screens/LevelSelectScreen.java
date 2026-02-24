package com.Geary.towerdefense.screens;

import com.Geary.towerdefense.TowerDefenseGame;
import com.Geary.towerdefense.UI.displays.modal.Modal;
import com.Geary.towerdefense.UI.displays.modal.levelSelect.LevelSelectModal;
import com.Geary.towerdefense.UI.render.icons.TooltipRenderer;
import com.Geary.towerdefense.levelSelect.CameraController;
import com.Geary.towerdefense.levelSelect.LevelGridCell;
import com.Geary.towerdefense.levelSelect.LevelPopupRenderer;
import com.Geary.towerdefense.levelSelect.generation.LevelGridGenerator;
import com.Geary.towerdefense.levelSelect.generation.OrderAssetRenderer;
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

import java.util.List;

public class LevelSelectScreen implements Screen {

    private final TowerDefenseGame game;
    private List<LevelGridCell> levelCells;

    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private ScreenViewport viewport;
    private BitmapFont uiFont;
    private TooltipRenderer tooltipRenderer;

    private final Vector3 tmpVec = new Vector3();

    private OrthographicCamera uiCamera;
    private ScreenViewport uiViewport;

    private LevelGridCell hoveredCell;

    private Modal activeModal;

    private LevelGridGenerator gridGenerator;
    private OrderAssetRenderer orderAssetRenderer;
    private CameraController cameraController;
    private LevelPopupRenderer popupRenderer;

    private boolean clickConsumed = false; // prevents repeat firing

    public LevelSelectScreen(TowerDefenseGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        camera = new OrthographicCamera();
        viewport = new ScreenViewport(camera);
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        uiFont = new BitmapFont();
        uiFont.getData().setScale(1.2f);
        tooltipRenderer = new TooltipRenderer(uiFont);

        camera.position.set(
            LevelGridGenerator.GRID_WIDTH * LevelGridGenerator.CELL_SIZE * 0.5f,
            LevelGridGenerator.GRID_HEIGHT * LevelGridGenerator.CELL_SIZE * 0.5f,
            0
        );
        camera.update();

        gridGenerator = new LevelGridGenerator();
        levelCells = gridGenerator.generateMap();

        orderAssetRenderer = new OrderAssetRenderer();
        orderAssetRenderer.scanGrid(gridGenerator.getGrid());

        cameraController = new CameraController(camera);
        cameraController.setupInput();

        popupRenderer = new LevelPopupRenderer(batch, shapeRenderer, uiFont, camera, levelCells);

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

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        cameraController.handlePan(delta);
        cameraController.handleDrag();

        detectHover();
        handleClick();

        cameraController.clampToGrid(
            LevelGridGenerator.GRID_WIDTH,
            LevelGridGenerator.GRID_HEIGHT,
            LevelGridGenerator.CELL_SIZE,
            viewport.getWorldWidth(),
            viewport.getWorldHeight()
        );

        camera.update();

        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        gridGenerator.drawGrid(shapeRenderer, batch);
        orderAssetRenderer.renderAreas(shapeRenderer);
        orderAssetRenderer.render(batch);

        popupRenderer.drawPopup(hoveredCell);
        if (activeModal != null) {

            activeModal.update();

            uiViewport.apply();
            uiCamera.update();

            batch.setProjectionMatrix(uiCamera.combined);
            shapeRenderer.setProjectionMatrix(uiCamera.combined);

            activeModal.layout();
            activeModal.draw(shapeRenderer, batch);
        }
    }

    private void detectHover() {
        tmpVec.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(tmpVec);

        hoveredCell = null;

        for (LevelGridCell cell : levelCells) {
            if (!cell.isLevel()) continue;

            float regionWidth = cell.getRegionWidth() * LevelGridGenerator.CELL_SIZE;
            float regionHeight = cell.getRegionHeight() * LevelGridGenerator.CELL_SIZE;

            float regionX = (cell.getRegionX() - 1) * LevelGridGenerator.CELL_SIZE;
            float regionY = (cell.getRegionY() - 1) * LevelGridGenerator.CELL_SIZE;

            boolean inside =
                tmpVec.x >= regionX &&
                    tmpVec.x <= regionX + regionWidth &&
                    tmpVec.y >= regionY &&
                    tmpVec.y <= regionY + regionHeight;

            if (inside) {
                hoveredCell = cell;
                break;
            }
        }

        popupRenderer.updateHoveredLevel(
            hoveredCell != null ? hoveredCell.levelData : null
        );
    }

    private void handleClick() {
        if (!Gdx.input.isButtonJustPressed(Input.Buttons.LEFT)) return;
        if (activeModal != null) {
            activeModal.handleClick(
                Gdx.input.getX(),
                Gdx.input.getY()
            );
            if (activeModal.shouldClose()) {
                activeModal = null;
            }
            return;
        }
        // Otherwise open modal
        if (hoveredCell != null && hoveredCell.isLevel()) {
            createLevelModal(hoveredCell);
        }
    }

    private void createLevelModal(LevelGridCell cell) {
        LevelSelectModal modal = new LevelSelectModal(
            cell.levelData,
            uiFont,
            camera
        );

        modal.setToOpen();

        // You’ll need:
        activeModal = modal;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        uiViewport.update(width, height, true);
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        uiFont.dispose();
    }
}
