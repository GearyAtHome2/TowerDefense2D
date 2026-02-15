package com.Geary.towerdefense.screens;

import com.Geary.towerdefense.TowerDefenseGame;
import com.Geary.towerdefense.UI.render.icons.TooltipRenderer;
import com.Geary.towerdefense.levelSelect.CameraController;
import com.Geary.towerdefense.levelSelect.LevelGridGenerator;
import com.Geary.towerdefense.levelSelect.LevelPopupRenderer;
import com.Geary.towerdefense.levelSelect.LevelData;
import com.badlogic.gdx.Gdx;
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
    private List<LevelData> levels;

    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private ScreenViewport viewport;
    private BitmapFont uiFont;
    private TooltipRenderer tooltipRenderer;
    private final Vector3 tmpVec = new Vector3();

    private LevelData hoveredLevel;

    private LevelGridGenerator gridGenerator;
    private CameraController cameraController;
    private LevelPopupRenderer popupRenderer;

    public LevelSelectScreen(TowerDefenseGame game) {
        this.game = game;
    }

    @Override
    public void show() {
        levels = game.getLevels();

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        camera = new OrthographicCamera();
        viewport = new ScreenViewport(camera);
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        uiFont = new BitmapFont();
        uiFont.getData().setScale(1.2f);
        tooltipRenderer = new TooltipRenderer(uiFont);

        camera.position.set(LevelGridGenerator.GRID_WIDTH * LevelGridGenerator.CELL_SIZE * 0.5f,
            LevelGridGenerator.GRID_HEIGHT * LevelGridGenerator.CELL_SIZE * 0.5f, 0);
        camera.update();

        gridGenerator = new LevelGridGenerator();
        gridGenerator.generateGrid(levels);

        cameraController = new CameraController(camera);
        cameraController.setupInput();

        popupRenderer = new LevelPopupRenderer(batch, shapeRenderer, uiFont, camera, levels);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);       // black background (change if you like)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        cameraController.handlePan(delta);
        cameraController.handleDrag();
        detectHover();

        cameraController.clampToGrid(LevelGridGenerator.GRID_WIDTH, LevelGridGenerator.GRID_HEIGHT, LevelGridGenerator.CELL_SIZE,
            viewport.getWorldWidth(), viewport.getWorldHeight());

        camera.update();
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        gridGenerator.drawGrid(shapeRenderer);
        popupRenderer.drawPopup(hoveredLevel);
    }

    private void detectHover() {
        tmpVec.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(tmpVec);
        hoveredLevel = null;

        for (int i = 0; i < levels.size(); i++) {
            float lx = (LevelGridGenerator.GRID_WIDTH / 2 + i * 3) * LevelGridGenerator.CELL_SIZE;
            float ly = (LevelGridGenerator.GRID_HEIGHT / 2) * LevelGridGenerator.CELL_SIZE;
            float dx = tmpVec.x - lx, dy = tmpVec.y - ly;
            if (dx * dx + dy * dy <= LevelGridGenerator.CELL_SIZE * LevelGridGenerator.CELL_SIZE) {
                hoveredLevel = levels.get(i);
                break;
            }
        }

        popupRenderer.updateHoveredLevel(hoveredLevel);
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
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
