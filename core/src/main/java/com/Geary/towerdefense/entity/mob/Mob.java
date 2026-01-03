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
            case LEFT -> x -= move;
            case UP -> y += move;
            case DOWN -> y -= move;
        }

        moveRandomly(moveDir, cell, delta);

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
            case LEFT -> clamp(1f - (localX / cellSize));
            case UP -> clamp(localY / cellSize);
            case DOWN -> clamp(1f - (localY / cellSize));
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

    private void moveRandomly(Direction dir, Cell cell, float delta) {
        float ran = random();
        if (dir == Direction.UP || dir == Direction.DOWN) {
            float localX = getCenterX() - cell.x;
            x += weightedRandomMovement(localX, delta);
        } else {
            float localY = getCenterY() - cell.y;
            y += weightedRandomMovement(localY, delta);
        }
    }

    /**
     * Weighted random movement perpendicular to main axis, with gentle center shepherding.
     *
     * @param axisValue Local position along the perpendicular axis (pixels from tile start)
     * @param delta     Frame delta (for smooth, framerate-independent movement)
     * @return float offset to apply perpendicular to movement
     */
    private float weightedRandomMovement(float axisValue, float delta) {
        float minBound = 0.05f * cellSize;
        float maxBound = 0.95f * cellSize;
        float center = 0.5f * cellSize;

        // Hard clamp to prevent leaving the tile
        axisValue = Math.max(minBound, Math.min(maxBound, axisValue));

        float totalWidth = maxBound - minBound;
        float offsetFromCenter = axisValue - center; // negative = above/left, positive = below/right

        // Compute probabilities to move left/right (negative/positive)
        // Mobs far from center have higher probability to move back toward center
        float baseMoveProb = 0.35f; // base chance to move in one direction
        float distanceFactor = Math.abs(offsetFromCenter) / (totalWidth / 2f); // 0 at center, 1 at far edges
        float moveTowardCenterProb = baseMoveProb + distanceFactor * (1f - baseMoveProb);

        float probLeft, probRight;

        if (offsetFromCenter > 0) {
            // Mob below center → move left/back toward center
            probLeft = moveTowardCenterProb;
            probRight = baseMoveProb * (1f - distanceFactor);
        } else if (offsetFromCenter < 0) {
            // Mob above center → move right/back toward center
            probRight = moveTowardCenterProb;
            probLeft = baseMoveProb * (1f - distanceFactor);
        } else {
            // At center → equal chance left/right
            probLeft = probRight = baseMoveProb;
        }

        // Remaining probability to stay still
        float stayProb = 1f - (probLeft + probRight);
        stayProb = Math.max(stayProb, 0f); // clamp in case sum > 1

        // Random choice
        float r = random();
        float moveDir;
        if (r < probLeft) moveDir = -1f;
        else if (r < probLeft + probRight) moveDir = 1f;
        else moveDir = 0f;

        // Scale movement by distance to nearest wall to prevent overshoot
        float maxMove = moveDir < 0 ? axisValue - minBound : maxBound - axisValue;
        float moveAmount = moveDir * Math.min(Math.abs(moveDir), maxMove) * delta * 10f; // tweak factor for natural wandering

        return moveAmount;
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

    //center-random
    public enum Faction {
        FRIENDLY,
        ENEMY
    }
}
