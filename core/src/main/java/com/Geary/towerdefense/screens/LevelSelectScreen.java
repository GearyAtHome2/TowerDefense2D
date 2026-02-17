package com.Geary.towerdefense.screens;

import com.Geary.towerdefense.TowerDefenseGame;
import com.Geary.towerdefense.UI.render.icons.TooltipRenderer;
import com.Geary.towerdefense.levelSelect.CameraController;
import com.Geary.towerdefense.levelSelect.LevelGridCell;
import com.Geary.towerdefense.levelSelect.generation.LevelGridGenerator;
import com.Geary.towerdefense.levelSelect.LevelPopupRenderer;
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
    private List<LevelGridCell> levels; // now a list of cells

    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private OrthographicCamera camera;
    private ScreenViewport viewport;
    private BitmapFont uiFont;
    private TooltipRenderer tooltipRenderer;
    private final Vector3 tmpVec = new Vector3();

    private LevelGridCell hoveredCell; // track the hovered cell

    private LevelGridGenerator gridGenerator;
    private CameraController cameraController;
    private LevelPopupRenderer popupRenderer;

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

        camera.position.set(LevelGridGenerator.GRID_WIDTH * LevelGridGenerator.CELL_SIZE * 0.5f,
            LevelGridGenerator.GRID_HEIGHT * LevelGridGenerator.CELL_SIZE * 0.5f, 0);
        camera.update();

        gridGenerator = new LevelGridGenerator();

        levels = gridGenerator.generateMap();

        cameraController = new CameraController(camera);
        cameraController.setupInput();

        popupRenderer = new LevelPopupRenderer(batch, shapeRenderer, uiFont, camera, levels);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        cameraController.handlePan(delta);
        cameraController.handleDrag();
        detectHover();

        cameraController.clampToGrid(LevelGridGenerator.GRID_WIDTH,
            LevelGridGenerator.GRID_HEIGHT,
            LevelGridGenerator.CELL_SIZE,
            viewport.getWorldWidth(),
            viewport.getWorldHeight());

        camera.update();
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        gridGenerator.drawGrid(shapeRenderer);

        // draw popup using the hovered cell
        popupRenderer.drawPopup(hoveredCell);
    }

    private void detectHover() {
        tmpVec.set(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(tmpVec);

        hoveredCell = null;

        for (LevelGridCell cell : levels) {
            if (!cell.isLevel()) continue;

            float cellX = cell.xIndex * LevelGridGenerator.CELL_SIZE;
            float cellY = cell.yIndex * LevelGridGenerator.CELL_SIZE;
            float size  = LevelGridGenerator.CELL_SIZE;

            boolean inside =
                tmpVec.x >= cellX &&
                    tmpVec.x <= cellX + size &&
                    tmpVec.y >= cellY &&
                    tmpVec.y <= cellY + size;

            if (inside) {
                hoveredCell = cell;
                break;
            }
        }

        popupRenderer.updateHoveredLevel(
            hoveredCell != null ? hoveredCell.levelData : null
        );
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
