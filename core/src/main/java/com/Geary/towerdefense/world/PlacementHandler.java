package com.Geary.towerdefense.world;

import com.Geary.towerdefense.behaviour.buildings.manager.FactoryManager;
import com.Geary.towerdefense.behaviour.buildings.manager.MineManager;
import com.Geary.towerdefense.behaviour.buildings.manager.TowerManager;
import com.Geary.towerdefense.behaviour.buildings.manager.TransportManager;

public class PlacementHandler {

    private final TowerManager towerManager;
    private final TransportManager transportManager;
    private final MineManager mineManager;
    private final FactoryManager factoryManager;

    public PlacementHandler(TowerManager towerManager,
                            TransportManager transportManager,
                            MineManager mineManager, FactoryManager factoryManager) {
        this.towerManager = towerManager;
        this.transportManager = transportManager;
        this.mineManager = mineManager;
        this.factoryManager = factoryManager;
    }

    /** Called from render() to update placement via keyboard */
    public void handleKeyboardInput(boolean transportKey, boolean towerKey, boolean mineKey, boolean factoryKey) {
        if (transportKey) {
            setPlacementState(false, true, false, false);
        } else if (towerKey) {
            setPlacementState(true, false, false, false);
        } else if (mineKey) {
            setPlacementState(false, false, true, false);
        }  else if (factoryKey) {
            setPlacementState(false, false, false, true);
        } else {
            setPlacementState(false, false, false, false);
        }
    }

    private void setPlacementState(boolean tower, boolean transport, boolean mine, boolean factory) {
        towerManager.setPlacementKeyboardActive(tower);
        transportManager.setPlacementKeyboardActive(transport);
        mineManager.setPlacementKeyboardActive(mine);
        factoryManager.setPlacementKeyboardActive(factory);
    }

    /** Called from render() to handle placements and update links */
    public boolean handlePlacements() {
        boolean placedTower = towerManager.handlePlacement();
        boolean placedTransport = transportManager.handlePlacement();
        boolean placedMine = mineManager.handlePlacement();
        boolean placedFactory = factoryManager.handlePlacement();

        if (placedTower || placedTransport || placedMine || placedFactory) {
            transportManager.updateAllTransportLinks();
        }

        return placedTower || placedTransport || placedMine || placedFactory;
    }
}
