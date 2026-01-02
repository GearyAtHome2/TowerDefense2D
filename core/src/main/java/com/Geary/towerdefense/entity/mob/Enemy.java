package com.Geary.towerdefense.entity.mob;

import com.Geary.towerdefense.Direction;
import com.Geary.towerdefense.entity.world.Cell;
import com.badlogic.gdx.graphics.Texture;

public class Enemy extends Mob {

    public Enemy(float startX, float startY) {
        super(startX, startY, new Texture("enemy.png"));
    }

    @Override
    protected Direction resolveMoveDirection(Cell cell) {
        if (cell.type == Cell.Type.TURN) {
            return cell.calculateTurnDirection(this, false);
        }
        return cell.direction;
    }
}
