package com.Geary.towerdefense.UI.displays.modal.spawner;

import com.Geary.towerdefense.entity.spawner.FriendlySpawner;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class SpawnerModalManager {
    private SpawnerModal spawnerModal;

    public SpawnerModalManager() {
    }

    public void updateQueues(){
        if (this.spawnerModal == null) {
            return;
        }
        this.spawnerModal.processQueueCooldowns();
    }

    public SpawnerModal getSpawnerModal(FriendlySpawner spawner, BitmapFont font, OrthographicCamera camera) {
        if (this.spawnerModal == null) {
            this.spawnerModal = new SpawnerModal(spawner, font, camera);
        }
        spawnerModal.setToOpen();
        return spawnerModal;
    }
}
