package com.Geary.towerdefense.levelSelect;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class CameraController {

    private final OrthographicCamera camera;
    private boolean dragging = false;
    private int lastMouseX, lastMouseY;
    private final float PAN_SPEED = 500f;

    public CameraController(OrthographicCamera camera) {
        this.camera = camera;
    }

    public void setupInput() {
        Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean scrolled(float amountX, float amountY) {
                camera.zoom += amountY * 0.1f;
                camera.zoom = Math.max(0.1f, Math.min(3f, camera.zoom));
                return true;
            }
        });
    }

    public void handleDrag() {
        if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
            int mouseX = Gdx.input.getX();
            int mouseY = Gdx.input.getY();

            if (!dragging) {
                dragging = true;
            } else {
                float dx = (lastMouseX - mouseX) * camera.zoom;
                float dy = (mouseY - lastMouseY) * camera.zoom; // y is inverted in screen coords

                camera.position.add(dx, dy, 0);
            }

            lastMouseX = mouseX;
            lastMouseY = mouseY;
        } else {
            dragging = false;
        }
    }

    public void clampToGrid(float gridWidth, float gridHeight, float cellSize, float viewportWidth, float viewportHeight) {
        float gridWorldWidth = gridWidth * cellSize;
        float gridWorldHeight = gridHeight * cellSize;

        float minX = -0.8f * gridWorldWidth;
        float maxX = 1.8f * gridWorldWidth;
        float minY = -0.8f * gridWorldHeight;
        float maxY = 1.8f * gridWorldHeight;

        camera.position.x = Math.max(minX, Math.min(maxX, camera.position.x));
        camera.position.y = Math.max(minY, Math.min(maxY, camera.position.y));
    }

    public void handlePan(float delta) {
        float speed = PAN_SPEED * delta * camera.zoom;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) camera.position.x -= speed;
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D))
            camera.position.x += speed;
        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) camera.position.y += speed;
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) camera.position.y -= speed;
    }
}
