package com.Geary.towerdefense.world;

import com.Geary.towerdefense.behaviour.buildings.manager.*;

public class PlacementHandler {

    private final TowerManager towerManager;
    private final TransportManager transportManager;
    private final ProductionManager productionManager;
    private final FactoryManager factoryManager;

    /** True only while a keyboard key is held */
    private boolean keyboardOverrideActive = false;

    public PlacementHandler(
        TowerManager towerManager,
        TransportManager transportManager,
        ProductionManager productionManager,
        FactoryManager factoryManager
    ) {
        this.towerManager = towerManager;
        this.transportManager = transportManager;
        this.productionManager = productionManager;
        this.factoryManager = factoryManager;
    }

    public void handleKeyboardInput(
        boolean transportKey,
        boolean towerKey,
        boolean mineKey,
        boolean factoryKey
    ) {
        if (towerKey) {
            activateKeyboardPlacement(towerManager);
        } else if (transportKey) {
            activateKeyboardPlacement(transportManager);
        } else if (mineKey) {
            activateKeyboardPlacement(productionManager);
        } else if (factoryKey) {
            activateKeyboardPlacement(factoryManager);
        } else {
            clearKeyboardPlacement();
        }
    }

    private void activateKeyboardPlacement(BuildingManager<?> manager) {
        if (keyboardOverrideActive && manager.isThisActiveManager()) {
            return;
        }

        keyboardOverrideActive = true;
        BuildingManager.setActivePlacement(manager);
    }

    private void clearKeyboardPlacement() {
        if (!keyboardOverrideActive) return;

        keyboardOverrideActive = false;
        BuildingManager.clearActivePlacement();
    }

    /** Called every frame */
    public boolean handlePlacements() {
        boolean placedTower = towerManager.handlePlacement();
        boolean placedTransport = transportManager.handlePlacement();
        boolean placedMine = productionManager.handlePlacement();
        boolean placedFactory = factoryManager.handlePlacement();

        if (placedTower || placedTransport || placedMine || placedFactory) {
            transportManager.updateAllTransportLinks();
        }

        return placedTower || placedTransport || placedMine || placedFactory;
    }
}
