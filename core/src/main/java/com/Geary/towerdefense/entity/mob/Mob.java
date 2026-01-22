package com.Geary.towerdefense.entity.mob;

import com.Geary.towerdefense.Direction;
import com.Geary.towerdefense.entity.Entity;
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

public abstract class Mob extends Entity implements Cloneable {

    public float size;
    public int armour = 0;
    public int damage;
    public Color color;
    public String effectText = "default Mob effect text";
    public String flavourText = "default Mob flavourtext";
    public float knockBackPower;
    public float weight;

    public float spawnTime;
    public EnumMap<Resource.RawResourceType, Double> rawResourceCost = new EnumMap<>(Resource.RawResourceType.class);
    public EnumMap<Resource.RefinedResourceType, Double> refinedResourceCost = new EnumMap<>(Resource.RefinedResourceType.class);
    public int coinCost;

    public float vx, vy; // velocity in pixels/sec

    protected Faction faction;
    public boolean reversed = false;

    protected MobPathNavigator pathNavigator;
    protected TileRandomMover randomMover;

    protected float speed;      // units per second
    protected float ranMoveProb; // probability for wiggle

    public float collisionCooldown = 0f;

    protected Mob(float xPos, float yPos, MobStats stats, Order order) {
        this.xPos = xPos;
        this.yPos = yPos;
        this.order = order;

        this.name = stats.name();
        this.effectText = stats.effectText();
        this.flavourText = stats.flavourText();

        this.size = GameWorld.cellSize * stats.size();
        this.health = stats.health();
        this.damage = stats.damage();
        this.knockBackPower = stats.knockback();
        this.weight = stats.weight();

        this.speed = stats.speed();
        this.color = stats.color();
        this.ranMoveProb = stats.ranMoveProb();
        this.armour = stats.armour();
        this.spawnTime = stats.spawnTime();

        this.collisionRadius = size * 0.5f;
    }

    public void setPath(List<Cell> path, int cellSize, boolean reverse) {
        pathNavigator = new MobPathNavigator(path, cellSize, reverse);
        randomMover = new TileRandomMover(cellSize);

        // Initialize velocity in the starting cell's direction
        Cell cell = pathNavigator.getCurrentCell();
        if (cell != null) {
            Direction dir = pathNavigator.getCurrentDirection();
            if (dir != null) {
                switch (dir) {
                    case RIGHT -> {
                        vx = speed;
                        vy = 0f;
                    }
                    case LEFT -> {
                        vx = -speed;
                        vy = 0f;
                    }
                    case UP -> {
                        vx = 0f;
                        vy = speed;
                    }
                    case DOWN -> {
                        vx = 0f;
                        vy = -speed;
                    }
                }
            }
        }
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
        int cellSize = pathNavigator.getCellSize();
        pathNavigator.updateFromPosition(getCenterX(), getCenterY());

        Cell cell = pathNavigator.getCurrentCell();
        if (cell == null) return;

        handleCellEntry(cell);
        Cell previousCell = pathNavigator.getPreviousCell();
        if (overlapsCell(previousCell)) {
//            applyPathWalls(previousCell, cellSize);
            applyOverlapMovement(previousCell, delta);
            applyPathWalls(previousCell, cellSize);//apply the walls of the previous cell if I overlap with it
            //won't do anything on straights, but on corners this matters.
        } else {
            handleMovement(delta);
        }
        applyPathWalls(cell, cellSize);//always apply the walls of the "current" cell
        applyKnockback(delta);
        finalizeFrame(delta);
    }

    private boolean canUpdate() {
        return isAlive() && !pathNavigator.reachedEnd();
    }

    private void handleCellEntry(Cell cell) {
        if (pathNavigator.hasEnteredNewTile()) onEnterCell(cell);
    }

    public void handleMovement(float delta) {
        if (pathNavigator.getCurrentCell() == null) return;

        Direction dir = pathNavigator.getCurrentDirection();

        if (dir == null) return;

        applyMovement(speed, dir, delta);
    }

    protected void applyOverlapMovement(Cell cell, float delta) {
        if (cell == null) return;
        Direction dir = pathNavigator.getLeaveDirectionForCell(cell);
        if (dir == null) return;

//        if (abs(vx) + abs(vy) < 1) {//only apply if we're not really moving maybe?
            applyMovement(speed, dir, delta);
//        }
    }

    private void applyMovement(float speed, Direction direction, float delta) {
        float dx = 0f, dy = 0f;
        switch (direction) {
            case RIGHT -> dx = 1f;
            case LEFT -> dx = -1f;
            case UP -> dy = 1f;
            case DOWN -> dy = -1f;
        }

        // --- 2. Apply directional acceleration ---
        float accel = speed * pathNavigator.getCellSize();
        vx += dx * accel * delta;
        vy += dy * accel * delta;

        // --- 3. Apply friction ---
        float friction = 1.7f;
        float frictionPerSecond = accel * friction / pathNavigator.getCellSize();
        float damping = (float) Math.exp(-frictionPerSecond * delta);

        vx *= damping;
        vy *= damping;

        xPos += vx * delta;
        yPos += vy * delta;
    }

    protected void applyKnockback(float delta) {
        // collisions handled elsewhere
    }

    protected void applyPathWalls(Cell cell, int cellSize) {
        if (cell == null) {
            return;
        }
        float left = cell.x;
        float right = cell.x + cellSize;
        float bottom = cell.y;
        float top = cell.y + cellSize;

        float bounceFactor = 0.2f; // 20% of velocity retained on bounce

        // --- STRAIGHT tiles ---
        switch (cell.type) {
            case PATH -> {
                // Vertical path (UP/DOWN) → walls left/right
                if (cell.direction == Direction.UP || cell.direction == Direction.DOWN) {
                    if (xPos < left) { // removed collisionRadius
                        xPos = left;
                        vx = -vx * bounceFactor;
                    }
                    if (xPos + size > right) { // removed collisionRadius
                        xPos = right - size;
                        vx = -vx * bounceFactor;
                    }
                }
                // Horizontal path (LEFT/RIGHT) → walls top/bottom
                else if (cell.direction == Direction.LEFT || cell.direction == Direction.RIGHT) {
                    if (yPos < bottom) { // removed collisionRadius
                        yPos = bottom;
                        vy = -vy * bounceFactor;
                    }
                    if (yPos + size > top) { // removed collisionRadius
                        yPos = top - size;
                        vy = -vy * bounceFactor;
                    }
                }
            }

            case TURN -> {
                applyTurnTileWalls(cell, cellSize);
            }
            default -> {
                // Optionally, clamp mob to cell boundaries for other types
                if (xPos < left + collisionRadius) {
                    xPos = left + collisionRadius;
                    vx = -vx * bounceFactor;
                }
                if (xPos + size > right - collisionRadius) {
                    xPos = right - size - collisionRadius;
                    vx = -vx * bounceFactor;
                }
                if (yPos < bottom + collisionRadius) {
                    yPos = bottom + collisionRadius;
                    vy = -vy * bounceFactor;
                }
                if (yPos + size > top - collisionRadius) {
                    yPos = top - size - collisionRadius;
                    vy = -vy * bounceFactor;
                }
            }
        }
    }

    protected void applyTurnTileWalls(Cell cell, int cellSize) {
        if (cell == null) return;

        float left = cell.x;
        float right = cell.x + cellSize;
        float bottom = cell.y;
        float top = cell.y + cellSize;
        float bounceFactor = 0.2f;

        // Determine entry and exit directions
        Direction entryDir = reversed ? cell.reverseNextDirection : cell.direction;
        Direction exitDir = reversed ? cell.reverseDirection : cell.nextDirection;

        // --- Entry wall (hard clamp on same side as entryDir) ---
        switch (entryDir) {
            case LEFT -> {
                if (xPos < left + collisionRadius) {
                    xPos = left + collisionRadius;
                    vx = -vx * bounceFactor;
                }
            }
            case RIGHT -> {
                if (xPos + size > right - collisionRadius) {
                    xPos = right - size - collisionRadius;
                    vx = -vx * bounceFactor;
                }
            }
            case UP -> {
                if (yPos + size > top - collisionRadius) {
                    yPos = top - size - collisionRadius;
                    vy = -vy * bounceFactor;
                }
            }
            case DOWN -> {
                if (yPos < bottom + collisionRadius) {
                    yPos = bottom + collisionRadius;
                    vy = -vy * bounceFactor;
                }
            }
        }

        // --- Exit wall (hard clamp on opposite side of exitDir) ---
        switch (exitDir) {
            case LEFT -> {
                if (xPos + size > right - collisionRadius) {
                    xPos = right - size - collisionRadius;
                    vx = -vx * bounceFactor;
                }
            }
            case RIGHT -> {
                if (xPos < left + collisionRadius) {
                    xPos = left + collisionRadius;
                    vx = -vx * bounceFactor;
                }
            }
            case UP -> {
                if (yPos < bottom + collisionRadius) {
                    yPos = bottom + collisionRadius;
                    vy = -vy * bounceFactor;
                }
            }
            case DOWN -> {
                if (yPos + size > top - collisionRadius) {
                    yPos = top - size - collisionRadius;
                    vy = -vy * bounceFactor;
                }
            }
        }

        // --- Inner corner as diagonal wall ---
        float cx = 0f, cy = 0f;
        boolean isDiagonalUpRight = false; // will help select normal

        if ((entryDir == Direction.UP && exitDir == Direction.RIGHT) ||
            (entryDir == Direction.LEFT && exitDir == Direction.DOWN)) {
            cx = right;
            cy = bottom;
            isDiagonalUpRight = true;
        } else if ((entryDir == Direction.RIGHT && exitDir == Direction.UP) ||
            (entryDir == Direction.DOWN && exitDir == Direction.LEFT)) {
            cx = left;
            cy = top;
            isDiagonalUpRight = true;
        } else if ((entryDir == Direction.DOWN && exitDir == Direction.RIGHT) ||
            (entryDir == Direction.LEFT && exitDir == Direction.UP)) {
            cx = right;
            cy = top;
            isDiagonalUpRight = false;
        } else if ((entryDir == Direction.RIGHT && exitDir == Direction.DOWN) ||
            (entryDir == Direction.UP && exitDir == Direction.LEFT)) {
            cx = left;
            cy = bottom;
            isDiagonalUpRight = false;
        }

        // Vector from corner to mob center
        float mx = xPos + size * 0.5f;
        float my = yPos + size * 0.5f;
        float dx = mx - cx;
        float dy = my - cy;
        float distSq = dx * dx + dy * dy;
        float minDist = collisionRadius;

        if (distSq < minDist * minDist) {
            float dist = (float) Math.sqrt(distSq);
            if (dist < 0.0001f) dist = 0.0001f;

            // Push mob out of corner
            float overlap = minDist - dist;
            xPos += (dx / dist) * overlap;
            yPos += (dy / dist) * overlap;

            // Diagonal wall normal
            float sqrt2inv = 1f / (float) Math.sqrt(2f);
            float nx = isDiagonalUpRight ? -sqrt2inv : sqrt2inv;
            float ny = sqrt2inv;

            // Reflect velocity over diagonal
            float dot = vx * nx + vy * ny;
            vx = vx - 2f * dot * nx;
            vy = vy - 2f * dot * ny;

            // Apply bounce factor
            vx *= bounceFactor;
            vy *= bounceFactor;
        }
    }


    private void finalizeFrame(float delta) {
        collisionCooldown = Math.max(0, collisionCooldown - delta);
    }

    protected void onEnterCell(Cell cell) {
    }

    public float getCenterX() {
        return xPos + size / 2f;
    }

    public float getCenterY() {
        return yPos + size / 2f;
    }

    public void setPosition(float x, float y) {
        this.xPos = x - this.size / 2;
        this.yPos = y - this.size / 2;
    }

    public void draw(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(this.color);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(xPos, yPos, size, size);
        shapeRenderer.end();
    }

    public enum Faction {FRIENDLY, ENEMY}

    public int getPathIndex() {
        return pathNavigator != null ? pathNavigator.getPathIndex() : -1;
    }

    @Override
    public List<String> getInfoLines() {
        List<String> lines = new ArrayList<>();
        lines.add(name);
        lines.add("health: " + health);
        return lines;
    }

    @Override
    public Color getInfoTextColor() {
        return Color.WHITE;
    }

    public String getCostText() {
        StringBuilder sb = new StringBuilder();
        if (coinCost > 0) sb.append("Coins: ").append(coinCost);
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

    private boolean overlapsCell(Cell cell) {
        if (cell == null) return false;

        float left = cell.x;
        float right = cell.x + pathNavigator.getCellSize();
        float bottom = cell.y;
        float top = cell.y + pathNavigator.getCellSize();

        return xPos + size > left &&
            xPos < right &&
            yPos + size > bottom &&
            yPos < top;
    }
}
