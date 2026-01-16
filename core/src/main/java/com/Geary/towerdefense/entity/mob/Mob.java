package com.Geary.towerdefense.entity.mob;

import com.Geary.towerdefense.Direction;
import com.Geary.towerdefense.entity.Entity;
import com.Geary.towerdefense.entity.mob.navigation.ArcTurnHandler;
import com.Geary.towerdefense.entity.mob.navigation.MobPathNavigator;
import com.Geary.towerdefense.entity.mob.navigation.TileRandomMover;
import com.Geary.towerdefense.entity.resources.Resource;
import com.Geary.towerdefense.entity.world.Cell;
import com.Geary.towerdefense.world.GameWorld;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import static com.Geary.towerdefense.Direction.*;

public abstract class Mob extends Entity implements Cloneable {

    public final Order order;
    public float size;
    public int armour = 0;
    public int damage;
    public Color color;
    public String effectText = "default Mob effect text";
    public String flavourText = "default Mob flavourtext";

    public float spawnTime;
    public EnumMap<Resource.RawResourceType, Double> rawResourceCost = new EnumMap<>(Resource.RawResourceType.class);
    public EnumMap<Resource.RefinedResourceType, Double> refinedResourceCost = new EnumMap<>(Resource.RefinedResourceType.class);
    public int coinCost;

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
    private boolean isBouncing;

    protected Mob(float xPos, float yPos, MobStats stats, Order order) {
        this.xPos = xPos;
        this.yPos = yPos;

        this.order = order;
        // Metadata
        this.name = stats.name();
        this.effectText = stats.effectText();
        this.flavourText = stats.flavourText();

        // Combat/Physics
        this.size = GameWorld.cellSize * stats.size();
        this.health = stats.health();
        this.damage = stats.damage();
        this.speed = stats.speed();
        this.color = stats.color();
        this.knockbackDamping = stats.knockbackDamping();
        this.ranMoveProb = stats.ranMoveProb();
        this.armour = stats.armour();
        this.spawnTime = stats.spawnTime();

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

    public abstract Mob copy();

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

    public boolean isBouncing(){
        return isBouncing;
    }

    public void setPosition(float x, float y){
        this.xPos = x;
        this.yPos = y;
    }

    private void handleMovement(Cell cell, Direction moveDir, float delta, int cellSize) {
        this.isBouncing = Math.abs(bounceVX) > 0.01f || Math.abs(bounceVY) > 0.01f;

        if (arcHandler.isInArcTurn()) {
            handleArcMovement(cell, delta, cellSize);
        } else {
            handleLinearMovement(cell, moveDir, delta, cellSize);
        }
    }

    private void handleArcMovement(Cell cell, float delta, int cellSize) {
        if (isBouncing) {
            Direction[] turn = computeTurnDirections(cell);
            arcHandler.rebuildArcPreserveProgress(
                cell,
                turn[0],
                turn[1],
                getCenterX(),
                getCenterY(),
                cellSize
            );
        }

        float arcMove = speed * delta * cellSize;
        float[] pos = arcHandler.updateArc(arcMove);

        if (pos != null) {
            xPos = pos[0] - size / 2f;
            yPos = pos[1] - size / 2f;
        }

        if (!arcHandler.isInArcTurn()) {
            pathNavigator.advance();
        }
    }

    private void handleLinearMovement(Cell cell, Direction moveDir, float delta, int cellSize) {
        float moveDistance = speed * delta * cellSize;

        float oldX = xPos;
        float oldY = yPos;

        // --- Move along main axis ---
        switch (moveDir) {
            case RIGHT -> xPos += moveDistance;
            case LEFT -> xPos -= moveDistance;
            case UP -> yPos += moveDistance;
            case DOWN -> yPos -= moveDistance;
            default -> {
            }
        }

        // --- Random wiggle perpendicular ---
        float perpendicularOffset = 0f;
        if (moveDir == UP || moveDir == DOWN) {
            perpendicularOffset = randomMover.computeMovement(getCenterX() - cell.x, delta, ranMoveProb);
            xPos += perpendicularOffset;
        } else if (moveDir == LEFT || moveDir == RIGHT) {
            perpendicularOffset = randomMover.computeMovement(getCenterY() - cell.y, delta, ranMoveProb);
            yPos += perpendicularOffset;
        }

        // --- Apply bounce ---
        vx = (xPos - oldX) / delta;
        vy = (yPos - oldY) / delta;

        // --- Tile progress update ---
        pathNavigator.updateTileProgress(computeTileProgress(cell, moveDir));
        if (pathNavigator.getTileProgress() >= 1f) pathNavigator.advance();

        // --- Spring correction perpendicular to main axis only ---
        float centerX = cell.x + cellSize / 2f;
        float centerY = cell.y + cellSize / 2f;
        float springStrength = 5f;

        if (moveDir == UP || moveDir == DOWN) {
            float dx = centerX - getCenterX();
            xPos += dx * springStrength * delta;
        } else if (moveDir == LEFT || moveDir == RIGHT) {
            float dy = centerY - getCenterY();
            yPos += dy * springStrength * delta;
        }
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
        return xPos + size / 2f;
    }

    public float getCenterY() {
        return yPos + size / 2f;
    }

    public void draw(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(this.color);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(xPos - size / 2, yPos - size / 2, size, size);
        shapeRenderer.end();
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

    public String getCostText() {
        StringBuilder sb = new StringBuilder();

        if (coinCost > 0) {
            sb.append("Coins: ").append(coinCost);
        }

        for (var e : rawResourceCost.entrySet()) {
            if (sb.length() > 0) sb.append("\n");
            sb.append(e.getKey().name()).append(": ").append(e.getValue().intValue());
        }

        for (var e : refinedResourceCost.entrySet()) {
            if (sb.length() > 0) sb.append("\n");
            sb.append(e.getKey().name()).append(": ").append(e.getValue().intValue());
        }

        return sb.length() == 0 ? "Free" : sb.toString();
    }

    public enum Order {
        NEUTRAL,
        TECH,
        NATURE,
        DARK,
        LIGHT,
        FIRE,
        WATER
    }
}
