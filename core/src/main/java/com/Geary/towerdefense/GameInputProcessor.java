package com.Geary.towerdefense;

import com.Geary.towerdefense.UI.CameraController;
import com.Geary.towerdefense.behaviour.buildings.manager.TowerManager;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameInputProcessor extends InputAdapter {

    private final TowerManager towerManager;
    private final CameraController cameraController;
    private final Viewport uiViewport;
    private int lastMouseX, lastMouseY;
    private final Vector3 touchDownScreen = new Vector3();
    private boolean isDraggingCamera = false;
    private static final float DRAG_THRESHOLD = 5f;

    private TowerClickListener towerClickListener;

    public GameInputProcessor(TowerManager towerManager, CameraController cameraController,
                              Viewport uiViewport) {
        this.towerManager = towerManager;
        this.cameraController = cameraController;
        this.uiViewport = uiViewport;
    }

    /**
     * Optional callback for world clicks
     */
    public void setTowerClickListener(TowerClickListener listener) {
        this.towerClickListener = listener;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == com.badlogic.gdx.Input.Buttons.LEFT) {
            touchDownScreen.set(screenX, screenY, 0);
            isDraggingCamera = false;
        }
        if (button == com.badlogic.gdx.Input.Buttons.RIGHT) {
            lastMouseX = screenX;
            lastMouseY = screenY;
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        if (com.badlogic.gdx.Gdx.input.isButtonPressed(com.badlogic.gdx.Input.Buttons.RIGHT)) {
            int dx = screenX - lastMouseX;
            int dy = screenY - lastMouseY;
            cameraController.dragBy(dx, dy);
            lastMouseX = screenX;
            lastMouseY = screenY;
        }

        float dx = Math.abs(screenX - touchDownScreen.x);
        float dy = Math.abs(screenY - touchDownScreen.y);
        if (dx > DRAG_THRESHOLD || dy > DRAG_THRESHOLD) {
            isDraggingCamera = true;
        }

        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (button != com.badlogic.gdx.Input.Buttons.LEFT) return false;

        Vector3 uiClick = new Vector3(screenX, screenY, 0);
        uiViewport.unproject(uiClick);

        if (uiClick.x >= 80 && uiClick.x <= 230 && uiClick.y >= 10 && uiClick.y <= 50) {
            towerManager.togglePlacementClick(uiClick, 80, 10, 150, 40);
            return true;
        }

        if (!towerManager.isPlacementActive() && !isDraggingCamera) {
            if (towerClickListener != null) {
                towerClickListener.onTowerClick(screenX, screenY);
            }
            return true;
        }

        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        cameraController.scrolled(amountX, amountY);
        return true;
    }

    public interface TowerClickListener {
        void onTowerClick(int screenX, int screenY);
    }
}
