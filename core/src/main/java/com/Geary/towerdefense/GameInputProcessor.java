package com.Geary.towerdefense;

import com.Geary.towerdefense.UI.CameraController;
import com.Geary.towerdefense.UI.displays.modal.Modal;
import com.Geary.towerdefense.UI.gameUI.GameUI;
import com.Geary.towerdefense.behaviour.buildings.manager.FactoryManager;
import com.Geary.towerdefense.behaviour.buildings.manager.MineManager;
import com.Geary.towerdefense.behaviour.buildings.manager.TowerManager;
import com.Geary.towerdefense.behaviour.buildings.manager.TransportManager;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameInputProcessor extends InputAdapter {

    private final TowerManager towerManager;
    private final MineManager mineManager;
    private final TransportManager transportManager;
    private final FactoryManager factoryManager;
    private final CameraController cameraController;
    private final Viewport uiViewport;
    private int lastMouseX, lastMouseY;
    private final Vector3 touchDownScreen = new Vector3();
    private boolean isDraggingCamera = false;
    private static final float DRAG_THRESHOLD = 5f;

    private WorldClickListener worldClickListener;
    private UiClickListener uiClickListener;
    public interface UiScrollListener {
        boolean onUiScroll(float amountY, Vector3 uiCoords);
    }
    private UiScrollListener uiScrollListener;

    private Modal activeModal = null;
    private OrthographicCamera worldCameraRef = null;


    public GameInputProcessor(TowerManager towerManager, MineManager mineManager, TransportManager transportManager, FactoryManager factoryManager, CameraController cameraController,
                              Viewport uiViewport) {
        this.towerManager = towerManager;
        this.mineManager = mineManager;
        this.factoryManager = factoryManager;
        this.transportManager = transportManager;
        this.cameraController = cameraController;
        this.uiViewport = uiViewport;
    }

    /**
     * Optional callback for world clicks
     */
    public void setWorldClickListener(WorldClickListener listener) {
        this.worldClickListener = listener;
    }

    public void setUiClickListener(UiClickListener listener) {
        this.uiClickListener = listener;
    }

    public void setActiveModal(Modal modal, OrthographicCamera worldCamera) {
        this.activeModal = modal;
        this.worldCameraRef = worldCamera;
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

        if (!isDraggingCamera && uiClickListener != null) {
            if (uiClickListener.onUiClick(uiClick)) {
                return true;
            }
        }

        if (!isAnyPlacementActive() && !isDraggingCamera) {
            if (worldClickListener != null) {
                worldClickListener.onClick(screenX, screenY);
            }
            return true;
        }

        return false;
    }

    private boolean isAnyPlacementActive() {
        return towerManager.isPlacementActive() ||
            transportManager.isPlacementActive() ||
            mineManager.isPlacementActive() ||
            factoryManager.isPlacementActive();
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {

        // Get mouse position in UI space
        Vector3 uiCoords = new Vector3(
            com.badlogic.gdx.Gdx.input.getX(),
            com.badlogic.gdx.Gdx.input.getY(),
            0
        );
        uiViewport.unproject(uiCoords);

        // 1️⃣ UI scroll region (bottom bar, etc)
        if (uiScrollListener != null && uiCoords.y <= GameUI.UI_BAR_HEIGHT) {
            if (uiScrollListener.onUiScroll(amountY, uiCoords)) {
                return true;
            }
        }

        // 2️⃣ Modal scroll (modal overlays world)
        if (activeModal != null && worldCameraRef != null) {
            if (activeModal.handleScroll(amountY)) {
                return true;
            }
        }

        // 3️⃣ World / camera scroll
        cameraController.scrolled(amountX, amountY);
        return true;
    }


    public interface WorldClickListener {
        void onClick(int screenX, int screenY);
    }

    public interface UiClickListener {
        boolean onUiClick(Vector3 uiCoords);
    }

    public void setUiScrollListener(UiScrollListener listener) {
        this.uiScrollListener = listener;
    }
}
