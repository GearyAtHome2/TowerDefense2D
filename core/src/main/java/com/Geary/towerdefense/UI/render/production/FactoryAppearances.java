package com.Geary.towerdefense.UI.render.production;

import com.badlogic.gdx.graphics.Color;

public final class FactoryAppearances {

    private FactoryAppearances() {}

    public static final FactoryAppearance WEAPONS =
        new FactoryAppearance(
            new Color(0.7f, 0.5f, 0.3f, 1f),
            new Color(0.4f, 0.3f, 0.2f, 1f),
            new Color(0.4f, 0.3f, 0.2f, 0.4f),
            new Color(0.9f, 0.8f, 0.5f, 1f),
            new Color(0.6f, 0.6f, 0.6f, 1f),
            new Color(0.5f, 0.5f, 0.5f, 0.4f),
            new float[][] {
                {-0.15f,  0.23f, 0.18f, -1.333f, 6, 20},
                { 0f,     0f,    0.20f,  1f,     8,  0},
                { 0.18f, -0.12f, 0.09f, -1.6f,   5, 60}
            }
        );

    public static final FactoryAppearance ARMOUR =
        new FactoryAppearance(
            Color.CYAN,
            new Color(0.2f, 0.3f, 0.4f, 1f),
            new Color(0.2f, 0.3f, 0.4f, 0.4f),
            Color.WHITE,
            Color.GRAY,
            new Color(0.6f, 0.6f, 0.6f, 0.4f),
            new float[][] {
                {0f, 0f, 0.25f, 1f, 10, 0}
            }
        );
}
