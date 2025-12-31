package com.Geary.towerdefense.UI;

import com.Geary.towerdefense.world.GameWorld;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.Viewport;

public class CameraController {

    private static final float OVERSCROLL = 0.1f;
    private static final float OVERSCROLL_NEG_Y_BUFFER = 0.2f;

    private final OrthographicCamera camera;
    private final Viewport viewport;
    private final GameWorld world;

    private boolean dragging = false;
    private int lastMouseX, lastMouseY;
    private float scrollAmount = 0f;
    private float cameraSpeed = 0.8f; // drag sensitivity

    public CameraController(OrthographicCamera camera, Viewport viewport, GameWorld world) {
        this.camera = camera;
        this.viewport = viewport;
        this.world = world;
    }

    /**
     * Call every frame
     */
    public void update() {
        handleDrag();
        handleScroll();
        clamp();
    }

    public void scrolled(float amountX, float amountY) {
        scrollAmount += amountY; // scroll up = positive
    }

    public void dragBy(int dx, int dy) {
        float worldDX = -dx * cameraSpeed * camera.zoom;
        float worldDY = dy * cameraSpeed * camera.zoom;
        camera.position.add(worldDX, worldDY, 0);
    }

    private void handleDrag() {
        // Start dragging
        if (Gdx.input.isButtonJustPressed(Input.Buttons.RIGHT)) {
            dragging = true;
            lastMouseX = Gdx.input.getX();
            lastMouseY = Gdx.input.getY();
        }

        // Stop dragging
        if (!Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
            dragging = false;
            return;
        }

        if (!dragging) return;

        int dx = Gdx.input.getX() - lastMouseX;
        int dy = Gdx.input.getY() - lastMouseY;

        float worldDX = -dx * cameraSpeed * camera.zoom;
        float worldDY = dy * cameraSpeed * camera.zoom;

        camera.position.add(worldDX, worldDY, 0);

        lastMouseX = Gdx.input.getX();
        lastMouseY = Gdx.input.getY();
    }

    private void handleScroll() {
        if (scrollAmount != 0) {
            camera.zoom += scrollAmount * 0.1f; // tweak sensitivity
            camera.zoom = Math.max(0.5f, Math.min(3f, camera.zoom)); // min/max zoom
            scrollAmount = 0;
        }
    }

    /**
     * Clamp camera position to world bounds, zoom-aware
     */
    private void clamp() {
        float halfW = viewport.getWorldWidth() * 0.5f * camera.zoom;
        float halfH = viewport.getWorldHeight() * 0.5f * camera.zoom;

        float overscrollX = world.gridWidth * world.cellSize * OVERSCROLL; // 10% of world width
        float overscrollY = world.gridHeight * world.cellSize * OVERSCROLL; // 10% of world height
        float overscrollYNegBuffer = world.gridHeight * world.cellSize * OVERSCROLL_NEG_Y_BUFFER; // 10% of world height

        float minX = halfW - overscrollX;
        float minY = halfH - overscrollY - overscrollYNegBuffer;
        float maxX = world.gridWidth * world.cellSize - halfW + overscrollX;
        float maxY = world.gridHeight * world.cellSize - halfH + overscrollY;

        // Handle worlds smaller than viewport
        if (maxX < minX) {
            camera.position.x = world.gridWidth * world.cellSize / 2f;
        } else {
            camera.position.x = Math.max(minX, Math.min(maxX, camera.position.x));
        }

        if (maxY < minY) {
            camera.position.y = world.gridHeight * world.cellSize / 2f;
        } else {
            camera.position.y = Math.max(minY, Math.min(maxY, camera.position.y));
        }
    }
}
