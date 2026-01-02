package com.Geary.towerdefense.entity.mob;

import com.Geary.towerdefense.Direction;
import com.Geary.towerdefense.entity.world.Cell;
import com.Geary.towerdefense.world.GameWorld;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import static com.Geary.towerdefense.pathGeneration.DirectionUtil.opposite;

public class Friendly extends Mob {

    private int lastPathIndex = -1;
    private float tileEntryCoord = 0f;

    // For turn tiles
    private Direction turnEntryDir = null;
    private Direction turnExitDir  = null;
    private boolean turnedThisTile = false;

    public Friendly(float startX, float startY) {
        super(startX, startY, new Texture("friendly.png"));
        this.reversed = true;
    }

    @Override
    public void update(float delta) {
        if (health <= 0 || reachedEnd) return;
        if (pathIndex < 0 || pathIndex >= path.size()) {
            reachedEnd = true;
            return;
        }

        Cell cell = path.get(pathIndex);

        // Reset TURN info when entering a new tile
        if (pathIndex != lastPathIndex) {
            lastPathIndex = pathIndex;
            turnedThisTile = false;

            if (cell.type == Cell.Type.TURN) {
                turnEntryDir = opposite(cell.nextDirection);  // entering from opposite of exit
                turnExitDir  = opposite(cell.direction);      // exit along reversed path
            } else {
                turnEntryDir = turnExitDir = null;
            }

            // Entry coordinate along axis of first movement
            Direction firstMove = (cell.type == Cell.Type.TURN) ? turnEntryDir : cell.reverseDirection;
            tileEntryCoord = (firstMove == Direction.UP || firstMove == Direction.DOWN)
                ? getCenterY()
                : getCenterX();
        }

        Direction moveDir = resolveMoveDirection(cell);

        float move = speed * delta * cellSize;
        float oldX = x, oldY = y;

        switch (moveDir) {
            case RIGHT -> x += move;
            case LEFT  -> x -= move;
            case UP    -> y += move;
            case DOWN  -> y -= move;
        }

        vx = (x - oldX) / delta;
        vy = (y - oldY) / delta;

        tileProgress = computeTileProgress(cell, moveDir);

        if (tileProgress >= 1f) {
            pathIndex--; // reversed path
            tileProgress = 0f;
            turnedThisTile = false;

            if (pathIndex < 0) reachedEnd = true;
        }
    }

    @Override
    protected Direction resolveMoveDirection(Cell cell) {
        if (cell.type == Cell.Type.TURN) {
            // Switch from entry -> exit at halfway point
            if (!turnedThisTile && tileProgress >= 0.5f) {
                turnedThisTile = true;
                // reset tileEntryCoord for exit axis
                tileEntryCoord = (turnExitDir == Direction.UP || turnExitDir == Direction.DOWN)
                    ? getCenterY()
                    : getCenterX();
            }

            return turnedThisTile ? turnExitDir : turnEntryDir;
        }

        return cell.reverseDirection;
    }

    protected float computeTileProgress(Cell cell, Direction moveDir) {
        float axisPos;
        float progress;

        switch (moveDir) {
            case RIGHT -> {
                axisPos = getCenterX();
                progress = (axisPos - cell.x) / GameWorld.cellSize;
            }
            case LEFT -> {
                axisPos = getCenterX();
                progress = (cell.x + GameWorld.cellSize - axisPos) / GameWorld.cellSize;
            }
            case UP -> {
                axisPos = getCenterY();
                progress = (axisPos - cell.y) / GameWorld.cellSize;
            }
            case DOWN -> {
                axisPos = getCenterY();
                progress = (cell.y + GameWorld.cellSize - axisPos) / GameWorld.cellSize;
            }
            default -> progress = 0;
        }

        return clamp(progress);
    }


    @Override
    public void draw(SpriteBatch batch) {
        batch.draw(texture, x, y);
    }
}
