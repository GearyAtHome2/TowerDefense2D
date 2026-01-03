package com.Geary.towerdefense.entity.mob;

import com.Geary.towerdefense.Direction;
import com.Geary.towerdefense.entity.world.Cell;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.List;

import static com.badlogic.gdx.math.MathUtils.random;

public abstract class Mob {

    protected List<Cell> path;
    protected int cellSize;

    public float x, y;
    public float vx, vy;

    public Texture texture;

    public int health = 18;

    // Path state
    public int pathIndex;
    public float tileProgress = 0f;
    protected boolean reachedEnd = false;
    protected boolean reversed = false;
    public float collisionRadius;
    // Per-tile state
    protected boolean turnedThisTile = false;
    private int lastPathIndex = -1;

    protected float speed;
    protected Faction faction;

    protected Mob(float startX, float startY, Texture texture) {
        this.texture = texture;
        this.x = startX;
        this.y = startY;
        this.collisionRadius = texture.getWidth() * 0.5f;
        double ran = random.nextDouble();
        speed = (float) (0.5f + ran / 8f);
    }

    public boolean isHostileTo(Mob other) {
        return this.faction != other.faction;
    }

    public boolean isAlive() {
        return health > 0;
    }

    public void applyDamage(int amount) {
        health -= amount;
    }

    public void setPath(List<Cell> path, int cellSize, boolean reverse) {
        this.path = path;
        this.cellSize = cellSize;
        this.reversed = reverse;
        this.pathIndex = reverse ? path.size() - 1 : 0;
        this.lastPathIndex = -1;
    }

    public void update(float delta) {
        if (health <= 0 || reachedEnd) return;
        if (pathIndex < 0 || pathIndex >= path.size()) {
            reachedEnd = true;
            return;
        }

        Cell cell = path.get(pathIndex);

        // Tile entry detection
        if (pathIndex != lastPathIndex) {
            lastPathIndex = pathIndex;
            tileProgress = 0f;
            turnedThisTile = false;
            onEnterCell(cell);
        }

        Direction moveDir = resolveMoveDirection(cell);
        if (moveDir == null) return;

        float move = speed * delta * cellSize;
        float oldX = x;
        float oldY = y;

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
            advancePathIndex();
        }
    }

    protected void onEnterCell(Cell cell) {
        // default: nothing
    }

    protected abstract Direction resolveMoveDirection(Cell cell);

    protected float computeTileProgress(Cell cell, Direction moveDir) {
        float localX = getCenterX() - cell.x;
        float localY = getCenterY() - cell.y;

        return switch (moveDir) {
            case RIGHT -> clamp(localX / cellSize);
            case LEFT  -> clamp(1f - (localX / cellSize));
            case UP    -> clamp(localY / cellSize);
            case DOWN  -> clamp(1f - (localY / cellSize));
            default -> 0f;
        };
    }

    protected void advancePathIndex() {
        tileProgress = 0f;
        turnedThisTile = false;

        if (reversed) pathIndex--;
        else pathIndex++;

        if (pathIndex < 0 || pathIndex >= path.size()) {
            reachedEnd = true;
        }
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

    public enum Faction {
        FRIENDLY,
        ENEMY
    }
}
