package com.Geary.towerdefense.entity.world;

import com.Geary.towerdefense.Direction;
import com.Geary.towerdefense.entity.buildings.Building;
import com.Geary.towerdefense.entity.mob.Mob;
import com.Geary.towerdefense.entity.resources.Resource;
import com.Geary.towerdefense.world.GameWorld;

import static com.Geary.towerdefense.pathGeneration.DirectionUtil.opposite;


public class Cell {
    public enum Type {
        EMPTY,
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
    public Direction direction;
    public Direction nextDirection;
    public Direction reverseDirection;
    public Direction reverseNextDirection;
    public TurnType turnType;
    public Building building;
    public Resource resource;
    public boolean bridgable = true;

    public Cell(Type type, float x, float y, Direction direction) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.nextDirection = direction;
        this.reverseDirection = opposite(direction);
        this.reverseNextDirection = opposite(direction);
    }

    public Cell(Type type, float x, float y, Direction startDir, Direction nextDir) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.direction = startDir;
        this.nextDirection = nextDir;
        this.reverseDirection = opposite(startDir);
        this.reverseNextDirection = opposite(nextDir);
        if (type == Type.TURN) {
            this.turnType = computeTurnType(startDir, nextDir);
        }
    }

    private TurnType computeTurnType(Direction from, Direction to) {
        if ((from == Direction.RIGHT && to == Direction.UP) ||
            (from == Direction.UP && to == Direction.LEFT) ||
            (from == Direction.LEFT && to == Direction.DOWN) ||
            (from == Direction.DOWN && to == Direction.RIGHT)) {
            return TurnType.TL_BR;
        }

        return TurnType.BL_TR;
    }

    public Direction calculateTurnDirection(Mob mob, boolean reverse) {
        Direction entryDir = reverse ? opposite(direction) : direction;
        Direction exitDir  = reverse ? opposite(nextDirection) : nextDirection;

        float centerX = mob.getCenterX();
        float centerY = mob.getCenterY();

        float half = GameWorld.cellSize / 2f;

        // Determine turning switch point based on entry and exit direction
        return switch (entryDir) {
            case RIGHT -> (centerX - x >= half) ? exitDir : entryDir;
            case LEFT  -> (x + GameWorld.cellSize - centerX >= half) ? exitDir : entryDir;
            case UP    -> (centerY - y >= half) ? exitDir : entryDir;
            case DOWN  -> (y + GameWorld.cellSize - centerY >= half) ? exitDir : entryDir;
            default -> throw new RuntimeException("impossible");
        };
    }
}
