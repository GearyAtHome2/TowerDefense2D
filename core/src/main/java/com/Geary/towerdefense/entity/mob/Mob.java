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

import static java.lang.Math.abs;

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
        handleMovement(delta);
        applyPathWalls(cell, cellSize);
        Cell previousCell = pathNavigator.getPreviousCell();
        if (overlapsCell(previousCell)) {
            applyPathWalls(previousCell, cellSize);
            applyMinorOverlapMovement(previousCell, delta);
        }
        Cell nextCell = pathNavigator.getNextCell();
        if (overlapsCell(nextCell)) {
            applyPathWalls(nextCell, cellSize);
            applyMinorOverlapMovement(nextCell, delta);
        }
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

    protected void applyMinorOverlapMovement(Cell cell, float delta) {
        if (cell == null) return;
        Direction dir = pathNavigator.getLeaveDirectionForCell(cell);
        if (dir == null) return;

        if (abs(vx) + abs(vy) < 1) {//only apply if we're not really moving maybe?
            applyMovement(speed * 0.05f, dir, delta);
        }
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
                    if (xPos < left + collisionRadius) {
                        xPos = left + collisionRadius;
                        vx = -vx * bounceFactor;
                    }
                    if (xPos + size > right - collisionRadius) {
                        xPos = right - size - collisionRadius;
                        vx = -vx * bounceFactor;
                    }
                }
                // Horizontal path (LEFT/RIGHT) → walls top/bottom
                else if (cell.direction == Direction.LEFT || cell.direction == Direction.RIGHT) {
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
            case TURN -> {
                handleTurn(cell, cellSize);
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

    private void handleTurn(Cell cell, int cellSize) {
        float bounceFactor = 0.2f;
        float left = cell.x;
        float right = cell.x + cellSize;
        float bottom = cell.y;
        float top = cell.y + cellSize;

        // Determine entry and exit directions for this tile
        Direction entryDir = reversed ? cell.reverseNextDirection : cell.direction;
        Direction exitDir = reversed ? cell.reverseDirection : cell.nextDirection;

        // --- Inside corner soft push ---
        float cx = 0, cy = 0;
        if ((entryDir == Direction.UP && exitDir == Direction.RIGHT) ||
            (entryDir == Direction.RIGHT && exitDir == Direction.UP)) {
            cx = left;
            cy = bottom;
        } else if ((entryDir == Direction.RIGHT && exitDir == Direction.DOWN) ||
            (entryDir == Direction.DOWN && exitDir == Direction.RIGHT)) {
            cx = left;
            cy = top;
        } else if ((entryDir == Direction.DOWN && exitDir == Direction.LEFT) ||
            (entryDir == Direction.LEFT && exitDir == Direction.DOWN)) {
            cx = right;
            cy = top;
        } else if ((entryDir == Direction.LEFT && exitDir == Direction.UP) ||
            (entryDir == Direction.UP && exitDir == Direction.LEFT)) {
            cx = right;
            cy = bottom;
        }

        float mx = xPos + size * 0.5f;
        float my = yPos + size * 0.5f;
        float dx = mx - cx;
        float dy = my - cy;
        float distSq = dx * dx + dy * dy;
        float minDist = collisionRadius;

        if (distSq < minDist * minDist) {
            float dist = (float) Math.sqrt(distSq);
            if (dist < 0.0001f) dist = 0.0001f;

            float nx = dx / dist;
            float ny = dy / dist;

            float penetration = minDist - dist;
            float pushFactor = 0.8f;
            xPos += nx * penetration * pushFactor;
            yPos += ny * penetration * pushFactor;

            float vn = vx * nx + vy * ny;
            if (vn < 0f) {
                vx -= vn * nx;
                vy -= vn * ny;
            }

            // small tangential push
            vx += nx * 0.05f;
            vy += ny * 0.05f;
        }

        // --- Entry wall: clamp on the same side as entryDir ---
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

        // --- Exit wall: clamp on the opposite side of exitDir ---
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
