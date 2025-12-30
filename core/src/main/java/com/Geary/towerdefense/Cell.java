package com.Geary.towerdefense;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class Cell {
    public enum Type { PATH, TOWER, ORIGIN }
    Type type;
    float x, y;          // bottom-left coordinate
    public Direction direction;  // only used for PATH cells


    public Cell(Type type, float x, float y, Direction direction) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.direction = direction;
    }
}


