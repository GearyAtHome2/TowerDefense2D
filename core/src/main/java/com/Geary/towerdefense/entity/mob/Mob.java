package com.Geary.towerdefense.entity.mob;

import com.Geary.towerdefense.Direction;
import com.Geary.towerdefense.entity.mob.navigation.ArcTurnHandler;
import com.Geary.towerdefense.entity.mob.navigation.MobPathNavigator;
import com.Geary.towerdefense.entity.mob.navigation.TileRandomMover;
import com.Geary.towerdefense.entity.world.Cell;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.List;

import static com.Geary.towerdefense.Direction.*;

public abstract class Mob {

    public Texture texture;
    public float collisionRadius;
    public int health = 18;
    public int damage = 6;

    public float x, y;
    public float bounceVX = 0f;
    public float bounceVY = 0f;

    public float vx, vy;

    protected Faction faction;
    protected boolean useCustomTurnLogic = false;
    public boolean reversed = false;
    protected int turnMultiplier = 1;

    protected MobPathNavigator pathNavigator;
    protected ArcTurnHandler arcHandler = new ArcTurnHandler();
    protected TileRandomMover randomMover;

    protected float speed;
    protected float ranMoveProb = 0.15f;

    protected float knockbackDamping = 8f;
    public float collisionCooldown = 0f;

    protected Mob(float x, float y, Texture texture, MobStats stats) {
        this.texture = texture;
        this.x = x;
        this.y = y;

        this.health = stats.health();
        this.damage = stats.damage();
        this.speed = stats.speed();
        this.knockbackDamping = stats.knockbackDamping();

        this.collisionRadius = texture.getWidth() * 0.5f;
    }

    public void setPath(List<Cell> path, int cellSize, boolean reverse) {
        pathNavigator = new MobPathNavigator(path, cellSize, reverse);
        randomMover = new TileRandomMover(cellSize);
    }

    public boolean isAlive() {
        return health > 0;
    }

    public boolean isHostileTo(Mob other) {
        return faction != other.faction;
    }

    public void applyDamage(int amount) {
        health -= amount;
    }

    public void update(float delta) {
        if (!isAlive() || pathNavigator.reachedEnd()) return;
        Cell cell = pathNavigator.getCurrentCell();
        if (cell == null) return;

        if (pathNavigator.hasEnteredNewTile()) onEnterCell(cell);

        Direction moveDir = resolveMoveDirection(cell);
        if (moveDir == null) return;

        boolean bouncing = Math.abs(bounceVX) > 0.01f || Math.abs(bounceVY) > 0.01f;
        if (arcHandler.isInArcTurn()) {
            if (bouncing) {
                // Rebuild arc using current position
                Direction entry, exit;
                if (turnMultiplier < 0) {
                    entry = multiplyDirection(cell.nextDirection, turnMultiplier);
                    exit  = multiplyDirection(cell.direction, turnMultiplier);
                } else {
                    entry = multiplyDirection(cell.direction, turnMultiplier);
                    exit  = multiplyDirection(cell.nextDirection, turnMultiplier);
                }

                arcHandler.rebuildArcPreserveProgress(
                    cell,
                    entry,
                    exit,
                    getCenterX(),
                    getCenterY(),
                    pathNavigator.getCellSize()
                );
            }
            float arcMove = speed * delta * pathNavigator.getCellSize();

            float[] pos = arcHandler.updateArc(arcMove);
            if (pos != null) {
                x = pos[0] - texture.getWidth() / 2f;
                y = pos[1] - texture.getHeight() / 2f;
            }

            if (!arcHandler.isInArcTurn()) {
                pathNavigator.advance();
            }
        } else {
            float move = speed * delta * pathNavigator.getCellSize();

            float oldX = x;
            float oldY = y;

            switch (moveDir) {
                case RIGHT -> x += move;
                case LEFT -> x -= move;
                case UP -> y += move;
                case DOWN -> y -= move;
            }

            if (moveDir == UP || moveDir == DOWN)
                x += randomMover.computeMovement(getCenterX() - cell.x, delta, ranMoveProb);
            else
                y += randomMover.computeMovement(getCenterY() - cell.y, delta, ranMoveProb);

            vx = (x - oldX) / delta;
            vy = (y - oldY) / delta;

            pathNavigator.updateTileProgress(computeTileProgress(cell, moveDir));
            if (pathNavigator.getTileProgress() >= 1f)
                pathNavigator.advance();
        }

        float decay = knockbackDamping;

        if (decay > 0f) {
            float factor = (1f - (float)Math.exp(-decay * delta)) / decay;

            x += bounceVX * factor;
            y += bounceVY * factor;

            float damp = (float)Math.exp(-decay * delta);
            bounceVX *= damp;
            bounceVY *= damp;
        } else {
            x += bounceVX * delta;
            y += bounceVY * delta;
        }

        if (Math.abs(bounceVX) < 1f) bounceVX = 0f;
        if (Math.abs(bounceVY) < 1f) bounceVY = 0f;
        collisionCooldown = Math.max(0, collisionCooldown - delta);
    }

    protected void onEnterCell(Cell cell) {
        if (cell.type != Cell.Type.TURN) return;

        Direction entry, exit;
        if (turnMultiplier < 0) {
            entry = multiplyDirection(cell.nextDirection, turnMultiplier);
            exit = multiplyDirection(cell.direction, turnMultiplier);
        } else {
            entry = multiplyDirection(cell.direction, turnMultiplier);
            exit = multiplyDirection(cell.nextDirection, turnMultiplier);
        }

        arcHandler.setupArc(cell, entry, exit, getCenterX(), getCenterY(), pathNavigator.getCellSize());
    }

    protected Direction resolveMoveDirection(Cell cell) {
        return reversed ? cell.reverseDirection : cell.direction;
    }

    private Direction multiplyDirection(Direction dir, int multiplier) {
        if (multiplier == 1) return dir;
        return switch (dir) {
            case UP -> DOWN;
            case DOWN -> UP;
            case LEFT -> RIGHT;
            case RIGHT -> LEFT;
            default -> NONE;
        };
    }

    protected float computeTileProgress(Cell cell, Direction moveDir) {
        float localX = getCenterX() - cell.x;
        float localY = getCenterY() - cell.y;

        return switch (moveDir) {
            case RIGHT -> clamp(localX / pathNavigator.getCellSize());
            case LEFT -> clamp(1f - (localX / pathNavigator.getCellSize()));
            case UP -> clamp(localY / pathNavigator.getCellSize());
            case DOWN -> clamp(1f - (localY / pathNavigator.getCellSize()));
            default -> 0f;
        };
    }

    protected float clamp(float v) {
        return Math.max(0f, Math.min(1f, v));
    }

    public float getCenterX() {
        return x + texture.getWidth() / 2f;
    }

    public float getCenterY() {
        return y + texture.getHeight() / 2f;
    }

    public void draw(SpriteBatch batch) {
        batch.draw(texture, x, y);
    }

    public enum Faction {FRIENDLY, ENEMY}

    public int getPathIndex() {
        return pathNavigator != null ? pathNavigator.getPathIndex() : -1;
    }

    public float getTileProgress() {
        return pathNavigator != null ? pathNavigator.getTileProgress() : 0f;
    }

    public void setTileProgress(float progress) {
        if (pathNavigator != null) pathNavigator.setTileProgress(progress);
    }
}
