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
    public Texture texture;


    public int health = 18;

    // Path tracking
    public int pathIndex = 0;
    public float tileProgress = 0f;
    protected float speed;
    protected boolean reachedEnd = false;
    protected boolean turnedThisTile = false;
    public boolean reversed;
    protected float tileEntryCoord;
    private int lastPathIndex = -1; // new field in Mob
    public float vx = 0;
    public float vy = 0;

    protected Mob(float startX, float startY, Texture texture) {
        this.texture = texture;
        this.x = startX;
        this.y = startY;

        double ran = random.nextDouble();
        speed = (float) (0.5F + ran / 8);
    }

    public void setPath(List<Cell> path, int cellSize, boolean reverse) {
        this.path = path;
        this.cellSize = cellSize;
        this.pathIndex = reverse ? path.size() - 1 : 0;
        this.reversed = reverse;
    }

    // 🔑 SINGLE OVERRIDE POINT
    protected abstract Direction resolveMoveDirection(Cell cell);

    public void update(float delta) {
        if (health <= 0 || reachedEnd) return;
        if (pathIndex >= path.size()) {
            reachedEnd = true;
            return;
        }

        Cell cell = path.get(pathIndex);
        Direction moveDir = resolveMoveDirection(cell);

        if (pathIndex != lastPathIndex) {
            lastPathIndex = pathIndex;
            tileEntryCoord = (moveDir == Direction.UP || moveDir == Direction.DOWN) ? getCenterY() : getCenterX();
        }

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

        float cellX = x - ((int)(x / cellSize)) * cellSize + texture.getWidth() / 2f;
        float cellY = y - ((int)(y / cellSize)) * cellSize + texture.getHeight() / 2f;

//        if (moveDir == Direction.LEFT || moveDir == Direction.RIGHT) centrePositionUD(cellY, cellSize);
//        if (moveDir == Direction.UP || moveDir == Direction.DOWN) centrePositionLR(cellX, cellSize);

        tileProgress = computeTileProgress(cell, moveDir);

        if (tileProgress >= 1f) {
            if (reversed) pathIndex--;
            else pathIndex++;

            tileProgress = 0f;
            turnedThisTile = false;

            if (pathIndex < 0 || pathIndex >= path.size()) {
                reachedEnd = true;
            }
        }
    }

    //todo: hard centring currently, lower this
    protected void centrePositionLR(float cellX, int cellSize) {
        float delta = cellX - (cellSize / 2f);
        float fraction = delta / (0.01F * (cellSize / 2f));
        float chance = Math.min(1f, Math.abs(fraction * fraction));
        if (random.nextDouble() < chance) {
            if (delta < 0) x += 1;
            else x -= 1;
        }
    }

    protected void centrePositionUD(float cellY, int cellSize) {
        float delta = cellY - (cellSize / 2f);
        float fraction = delta / (0.01F * (cellSize / 2f));
        float chance = Math.min(1f, Math.abs(fraction * fraction));
        if (random.nextDouble() < chance) {
            if (delta < 0) y += 1;
            else y -= 1;
        }
    }

    protected float computeTileProgress(Cell cell, Direction moveDir) {
        float localX = x - cell.x;
        float localY = y - cell.y;

        // Use the mob's actual movement direction, not the cell's entry direction
        Direction referenceDir = moveDir;

        return switch (referenceDir) {
            case RIGHT -> clamp(localX / cellSize);
            case LEFT  -> clamp(1f - (localX / cellSize));
            case UP    -> clamp(localY / cellSize);
            case DOWN  -> clamp(1f - (localY / cellSize));
            default -> 0;
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
}
