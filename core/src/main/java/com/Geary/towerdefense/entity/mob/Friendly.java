package com.Geary.towerdefense.entity.mob;

import com.Geary.towerdefense.Direction;
import com.Geary.towerdefense.entity.world.Cell;
import com.badlogic.gdx.graphics.Texture;

import static com.Geary.towerdefense.pathGeneration.DirectionUtil.opposite;

public class Friendly extends Mob {

    private Direction turnEntryDir;
    private Direction turnExitDir;

    public Friendly(float startX, float startY) {
        super(startX, startY, new Texture("friendly.png"));
        this.reversed = true;
    }

    @Override
    protected void onEnterCell(Cell cell) {
        if (cell.type == Cell.Type.TURN) {
            // Friendly moves opposite to enemy
            turnEntryDir = opposite(cell.nextDirection);
            turnExitDir  = opposite(cell.direction);
        } else {
            turnEntryDir = null;
            turnExitDir  = null;
        }
    }

    @Override
    protected Direction resolveMoveDirection(Cell cell) {

        if (cell.type == Cell.Type.TURN) {
            if (!turnedThisTile && tileProgress >= 0.5f) {
                turnedThisTile = true;
            }
            return turnedThisTile ? turnExitDir : turnEntryDir;
        }

        return cell.reverseDirection;
    }
}
