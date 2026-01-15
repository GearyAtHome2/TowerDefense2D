package com.Geary.towerdefense.UI.displays.modal.spawner;

import com.Geary.towerdefense.entity.spawner.FriendlySpawner;
import com.Geary.towerdefense.world.GameStateManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;

public class SpawnerModalManager {
    private SpawnerModal spawnerModal;

    public SpawnerModalManager() {
    }

    public void updateSpawner(float delta) {
        if (this.spawnerModal == null) {
            return;
        }
        this.spawnerModal.processQueueCooldowns(delta);
        this.spawnerModal.updateAffordability();
    }

    public SpawnerModal getSpawnerModal(FriendlySpawner spawner, GameStateManager gameStateManager, BitmapFont font, OrthographicCamera camera) {
        if (this.spawnerModal == null) {
            this.spawnerModal = new SpawnerModal(spawner, gameStateManager, font, camera);
        }
        spawnerModal.setToOpen();
        return spawnerModal;
    }
}

