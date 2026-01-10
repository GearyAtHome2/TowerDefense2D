package com.Geary.towerdefense.entity.mob;

import com.Geary.towerdefense.Direction;
import com.Geary.towerdefense.entity.Entity;
import com.Geary.towerdefense.entity.mob.navigation.ArcTurnHandler;
import com.Geary.towerdefense.entity.mob.navigation.MobPathNavigator;
import com.Geary.towerdefense.entity.mob.navigation.TileRandomMover;
import com.Geary.towerdefense.entity.world.Cell;
import com.Geary.towerdefense.world.GameWorld;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.ArrayList;
import java.util.List;

import static com.Geary.towerdefense.Direction.*;

public abstract class Mob extends Entity {

    public Texture texture;
    public float size;
    public float collisionRadius;
    public int health;
    public int damage;

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
    protected float ranMoveProb;

    protected float knockbackDamping;
    public float collisionCooldown = 0f;

    protected Mob(float xPos, float yPos, Texture texture, MobStats stats) {
        this.texture = texture;
        this.xPos = xPos;
        this.yPos = yPos;

        this.name = stats.name();
        this.size = GameWorld.cellSize * stats.size();
        this.health = stats.health();
        this.damage = stats.damage();
        this.speed = stats.speed();
        this.knockbackDamping = stats.knockbackDamping();
        this.ranMoveProb = stats.ranMoveProb();
        this.collisionRadius = size * 0.5f;
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

    public void update(float delta) {
        if (!canUpdate()) return;

        Cell cell = pathNavigator.getCurrentCell();
        if (cell == null) return;

        handleCellEntry(cell);

        Direction moveDir = resolveMoveDirection(cell);
        if (moveDir == null) return;

        int cellSize = pathNavigator.getCellSize();
        handleMovement(cell, moveDir, delta, cellSize);
        applyKnockback(delta);
        finalizeFrame(delta);
    }

    private boolean canUpdate() {
        return isAlive() && !pathNavigator.reachedEnd();
    }

    private void handleCellEntry(Cell cell) {
        if (pathNavigator.hasEnteredNewTile()) {
            onEnterCell(cell);
        }
    }

    private void handleMovement(Cell cell, Direction moveDir, float delta, int cellSize) {
        boolean bouncing = Math.abs(bounceVX) > 0.01f || Math.abs(bounceVY) > 0.01f;

        if (arcHandler.isInArcTurn()) {
            handleArcMovement(cell, delta, bouncing, cellSize);
        } else {
            handleLinearMovement(cell, moveDir, delta, cellSize);
        }
    }

    private void handleArcMovement(Cell cell, float delta, boolean bouncing, int cellSize) {
        if (bouncing) {
            Direction[] turn = computeTurnDirections(cell);
//            if (arcHandler.arcProgress < 1) {
                arcHandler.rebuildArcPreserveProgress(
                    cell,
                    turn[0],
                    turn[1],
                    getCenterX(),
                    getCenterY(),
                    cellSize
                );
//            }
        }

        float arcMove = speed * delta * cellSize;

        float initxPos = xPos;
        float inityPos = yPos;
        float[] pos = arcHandler.updateArc(arcMove);

        if (pos != null) {
            xPos = pos[0] - texture.getWidth() / 2f;
            yPos = pos[1] - texture.getHeight() / 2f;
        }

        if (xPos - initxPos > 40 || yPos - inityPos > 40) {

            System.out.println("teleport possible? Size > 20:");
            System.out.println(initxPos + ", " + inityPos + "->" + xPos + ", " + yPos);
            System.out.println("bouncing: " +bouncing);
            System.out.println("arc progress: " +arcHandler.arcProgress);
            System.out.println("----------collision log----------");
        }

        if (!arcHandler.isInArcTurn()) {
            pathNavigator.advance();
        }
    }

    private void handleLinearMovement(Cell cell, Direction moveDir, float delta, int cellSize) {
        float move = speed * delta * cellSize;

        float oldX = xPos;
        float oldY = yPos;

        switch (moveDir) {
            case RIGHT -> xPos += move;
            case LEFT -> xPos -= move;
            case UP -> yPos += move;
            case DOWN -> yPos -= move;
        }

        if (moveDir == UP || moveDir == DOWN)
            xPos += randomMover.computeMovement(getCenterX() - cell.x, delta, ranMoveProb);
        else
            yPos += randomMover.computeMovement(getCenterY() - cell.y, delta, ranMoveProb);

        vx = (xPos - oldX) / delta;
        vy = (yPos - oldY) / delta;

        pathNavigator.updateTileProgress(computeTileProgress(cell, moveDir));
        if (pathNavigator.getTileProgress() >= 1f)
            pathNavigator.advance();
    }


    private void applyKnockback(float delta) {
        float decay = knockbackDamping;

        if (decay > 0f) {
            float factor = (1f - (float) Math.exp(-decay * delta)) / decay;

            xPos += bounceVX * factor;
            yPos += bounceVY * factor;

            float damp = (float) Math.exp(-decay * delta);
            bounceVX *= damp;
            bounceVY *= damp;
        } else {
            xPos += bounceVX * delta;
            yPos += bounceVY * delta;
        }
    }

    private void finalizeFrame(float delta) {
        if (Math.abs(bounceVX) < 1f) bounceVX = 0f;
        if (Math.abs(bounceVY) < 1f) bounceVY = 0f;

        collisionCooldown = Math.max(0, collisionCooldown - delta);
    }

    protected void onEnterCell(Cell cell) {
        if (cell.type != Cell.Type.TURN) return;

        //guard against setting up the arc
        if (arcHandler.isInArcTurn()) return;

        Direction[] turn = computeTurnDirections(cell);
        arcHandler.setupArc(
            cell,
            turn[0],
            turn[1],
            getCenterX(),
            getCenterY(),
            pathNavigator.getCellSize()
        );
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

    private Direction[] computeTurnDirections(Cell cell) {
        Direction entry = adjusted(turnMultiplier < 0 ? cell.nextDirection : cell.direction);
        Direction exit = adjusted(turnMultiplier < 0 ? cell.direction : cell.nextDirection);
        return new Direction[]{entry, exit};
    }

    protected float clamp(float v) {
        return Math.max(0f, Math.min(1f, v));
    }

    public float getCenterX() {
        return xPos + texture.getWidth() / 2f;
    }

    public float getCenterY() {
        return yPos + texture.getHeight() / 2f;
    }

    public void draw(SpriteBatch batch) {
        batch.draw(texture, xPos, yPos);
    }

    public enum Faction {FRIENDLY, ENEMY}

    public int getPathIndex() {
        return pathNavigator != null ? pathNavigator.getPathIndex() : -1;
    }

    private Direction adjusted(Direction dir) {
        return turnMultiplier == 1 ? dir : multiplyDirection(dir, turnMultiplier);
    }

    public float getTileProgress() {
        return pathNavigator != null ? pathNavigator.getTileProgress() : 0f;
    }

    public void setTileProgress(float progress) {
        if (pathNavigator != null) pathNavigator.setTileProgress(progress);
    }

    @Override
    public List<String> getInfoLines() {
        List<String> lines = new ArrayList<>();
        lines.add(this.name);
        lines.add("health: " + this.health);
        return lines;
    }

    @Override
    public Color getInfoTextColor() {
        return Color.WHITE; // default
    }
}
