package com.Geary.towerdefense.entity.world;

import com.Geary.towerdefense.Direction;
import com.Geary.towerdefense.entity.Enemy;
import com.Geary.towerdefense.world.GameWorld;

import java.util.Random;


public class Cell {
    public enum Type {
        TOWER,
        PATH,
        TURN,
        HOME,
        ENEMY
    }

    public enum TurnType {
        TL_BR, // top-left → bottom-right
        BL_TR  // bottom-left → top-right
    }

    public float x, y;
    public Type type;
    public Direction direction; // initial movement
    public Direction nextDirection; // for turn tiles
    public TurnType turnType;

    public Cell(){}

    public Cell(Type type, float x, float y, Direction direction) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.nextDirection = direction;
    }

    public Cell(Type type, float x, float y, Direction startDir, Direction nextDir) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.direction = startDir;
        this.nextDirection = nextDir;
        if (type == Type.TURN) {
            this.turnType = computeTurnType(startDir, nextDir);
        }
    }

    private TurnType computeTurnType(Direction from, Direction to) {
        // BL → TR diagonal
        if ((from == Direction.RIGHT && to == Direction.UP) ||
            (from == Direction.UP && to == Direction.LEFT) ||
            (from == Direction.LEFT && to == Direction.DOWN) ||
            (from == Direction.DOWN && to == Direction.RIGHT)) {
            return TurnType.TL_BR;
        }

        // TL → BR diagonal
        return TurnType.BL_TR;
    }

    public Direction calculateTurnDirection(Enemy enemy) {
        float localX = (enemy.getCenterX() - x);
        float localY = (enemy.getCenterY() - y);
        float chanceNewDir = 0;
        float reducedCellSizeDenominator = (GameWorld.cellSize * 0.9F);//0.9 is there to make sure it doesn't go near the far wall
        switch (direction) {
            case RIGHT:
                chanceNewDir = localX / reducedCellSizeDenominator;
                break;
            case LEFT:
                chanceNewDir = (GameWorld.cellSize - localX) / reducedCellSizeDenominator;
                break;
            case UP:
                chanceNewDir = localY / reducedCellSizeDenominator;
                break;
            case DOWN:
                chanceNewDir = (GameWorld.cellSize - localY) / reducedCellSizeDenominator;
                break;
            default:
                throw new RuntimeException("impossible tile type");
        }
        double ran = new Random().nextDouble();//todo: re-enable this once working
        return chanceNewDir > ran ? nextDirection : direction;
    }

    public boolean diagonalTriggered(Enemy enemy) {
        // Enemy local coordinates relative to tile
        float localX = enemy.getCenterX() - x;
        float localY = enemy.getCenterY() - y;

        // Clamp inside tile
        localX = Math.max(0, Math.min(GameWorld.cellSize, localX));
        localY = Math.max(0, Math.min(GameWorld.cellSize, localY));


        switch (direction) {
            case LEFT, RIGHT: // top-left → bottom-right
                return localY <= GameWorld.cellSize - localX;

            case UP, DOWN: // bottom-left → top-right
                // Trigger only after passing the line y = x
                return localY >= localX;

            default:
                return false;
        }
    }
}
