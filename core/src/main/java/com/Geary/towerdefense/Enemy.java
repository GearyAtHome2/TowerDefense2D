package com.Geary.towerdefense;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

import java.util.List;

import static com.badlogic.gdx.math.MathUtils.random;

public class Enemy {

    float x, y;
    Texture texture;

    public int health = 2;

    // Path tracking
    int pathIndex = 0;
    float tileProgress = 0f;
    float speed;
    boolean reachedEnd = false;

    public Enemy(float startX, float startY) {
        texture = new Texture("enemy.png");
        double ran = random.nextDouble();
        speed = (float) (0.5F + ran / 8);
        this.x = startX;
        this.y = startY;
    }

    public void update(float delta, List<Cell> path, int cellSize) {

        //todo: consolidate these two - need to make them cause base damage anyway
        if (health <= 0 || reachedEnd) return;
        if (pathIndex >= path.size()) {
            reachedEnd = true;
            return;
        }

        Cell cell = path.get(pathIndex);
        // --- Advance along path ---
        tileProgress = computeTileProgress(cell, cellSize);

        if (tileProgress >= 1f) {
            pathIndex++;
            tileProgress = 0f;

            if (pathIndex >= path.size()) {
                reachedEnd = true;
                return;
            }
        }
        float move = speed * delta * cellSize;

        switch (cell.direction) {
            case RIGHT -> x += move;
            case LEFT -> x -= move;
            case UP -> y += move;
            case DOWN -> y -= move;
        }

        float cellX = x - ((int) (x / cellSize)) * cellSize + texture.getWidth() / 2F;//looks like cellSize cancels here but we're casting to int so it matters
        float cellY = y - ((int) (y / cellSize)) * cellSize + texture.getHeight() / 2F;
        double ran = random.nextDouble();
        if (ran < 0.25) x += 1;
        else if (ran < 0.5) x -= 1;
        else if (ran < 0.75) y += 1;
        else y -= 1;
        if (cell.direction == Direction.LEFT || cell.direction == Direction.RIGHT) {
            centrePositionUD(cellY, cellSize);
        }
        if (cell.direction == Direction.UP || cell.direction == Direction.DOWN) {
            centrePositionLR(cellX, cellSize);
        }
    }

    public void centrePositionLR(float cellX, int cellSize) {
        float delta = cellX - (cellSize / 2f);          // -50..50 for 100px tile
        float fraction = delta / (0.9F * (cellSize / 2f)) ;     // -1..1
        float deltaChance = Math.min(1f, Math.abs(fraction * fraction)); // square it
        if (random.nextDouble() < Math.abs(deltaChance)) {
            if (delta < 0) {
                x += 1;
            }
            if (delta > 0) {
                x -= 1;
            }
        }
    }

    public void centrePositionUD(float cellY, int cellSize) {
        float delta = cellY - (cellSize / 2f);          // -50..50 for 100px tile
        float fraction = delta / (0.9F * (cellSize / 2f));       // -1..1
        float deltaChance = Math.min(1f, Math.abs(fraction * fraction)); // square it
        double ranDub = random.nextDouble();
        if (ranDub < Math.abs(deltaChance)) {
            if (delta < 0) {
                y += 1;
            }
            if (delta > 0) {
                y -= 1;
            }
        }
    }

    // --- Targeting helpers ---
    private float computeTileProgress(Cell cell, int cellSize) {

        float localX = x - cell.x;
        float localY = y - cell.y;

        switch (cell.direction) {
            case RIGHT:
                return clamp(localX / cellSize);
            case LEFT:
                return clamp(1f - (localX / cellSize));
            case UP:
                return clamp(localY / cellSize);
            case DOWN:
                return clamp(1f - (localY / cellSize));
            default:
                return 0f;
        }
    }

    private float clamp(float v) {
        return Math.max(0f, Math.min(1f, v));
    }

    public float getPathProgress() {
        return pathIndex + tileProgress;
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
