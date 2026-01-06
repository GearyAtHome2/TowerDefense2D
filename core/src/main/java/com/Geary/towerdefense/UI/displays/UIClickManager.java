package com.Geary.towerdefense.UI.displays;

import com.Geary.towerdefense.UI.GameUI;
import com.badlogic.gdx.Gdx;

public class UIClickManager {
    public static boolean isClickInGameArea(float screenY) {
        return screenY < Gdx.graphics.getHeight() - GameUI.UI_BAR_HEIGHT;
    }
}
