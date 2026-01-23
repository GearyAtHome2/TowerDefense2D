package com.Geary.towerdefense.UI.displays.modal.tower;

import com.Geary.towerdefense.entity.buildings.tower.Tower;
import com.Geary.towerdefense.world.GameStateManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class TowerModalManager {
    private TowerModal towerModal;

    public TowerModalManager() {
    }

    public void updateTower(float delta) {
        if (this.towerModal == null) {
            return;
        }
        //affordability updates per tick?
    }

    public TowerModal getTowerModal(Tower tower, GameStateManager gameStateManager, BitmapFont font, OrthographicCamera camera) {
        if (this.towerModal == null) {
            this.towerModal = new TowerModal(tower, gameStateManager, font, camera);
        }
        towerModal.setToOpen();
        return towerModal;
    }
}

