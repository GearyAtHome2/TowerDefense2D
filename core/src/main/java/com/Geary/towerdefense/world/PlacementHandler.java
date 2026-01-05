package com.Geary.towerdefense.world;

import com.Geary.towerdefense.behaviour.buildings.manager.MineManager;
import com.Geary.towerdefense.behaviour.buildings.manager.TowerManager;
import com.Geary.towerdefense.behaviour.buildings.manager.TransportManager;

public class PlacementHandler {

    private final TowerManager towerManager;
    private final TransportManager transportManager;
    private final MineManager mineManager;

    public PlacementHandler(TowerManager towerManager,
                            TransportManager transportManager,
                            MineManager mineManager) {
        this.towerManager = towerManager;
        this.transportManager = transportManager;
        this.mineManager = mineManager;
    }

    /** Called from render() to update placement via keyboard */
    public void handleKeyboardInput(boolean transportKey, boolean towerKey, boolean mineKey) {
        if (transportKey) {
            setPlacementState(false, true, false);
        } else if (towerKey) {
            setPlacementState(true, false, false);
        } else if (mineKey) {
            setPlacementState(false, false, true);
        } else {
            setPlacementState(false, false, false);
        }
    }

    private void setPlacementState(boolean tower, boolean transport, boolean mine) {
        towerManager.setPlacementKeyboardActive(tower);
        transportManager.setPlacementKeyboardActive(transport);
        mineManager.setPlacementKeyboardActive(mine);
    }

    /** Called from render() to handle placements and update links */
    public boolean handlePlacements() {
        boolean placedTower = towerManager.handlePlacement();
        boolean placedTransport = transportManager.handlePlacement();
        boolean placedMine = mineManager.handlePlacement();

        if (placedTower || placedTransport || placedMine) {
            transportManager.updateAllTransportLinks();
        }

        return placedTower || placedTransport || placedMine;
    }
}
