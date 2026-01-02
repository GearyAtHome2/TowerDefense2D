package com.Geary.towerdefense.world;

import com.Geary.towerdefense.Direction;
import com.Geary.towerdefense.entity.Bullet;
import com.Geary.towerdefense.entity.Tower;
import com.Geary.towerdefense.entity.mob.Enemy;
import com.Geary.towerdefense.entity.mob.Friendly;
import com.Geary.towerdefense.entity.spawner.EnemySpawner;
import com.Geary.towerdefense.entity.spawner.FriendlySpawner;
import com.Geary.towerdefense.entity.world.Cell;
import com.Geary.towerdefense.pathGeneration.PathGenerator;
import com.badlogic.gdx.math.MathUtils;

import java.util.ArrayList;
import java.util.List;

public class GameWorld {
    public static final int cellSize = 100;
    public final int gridWidth = 20;
    public final int gridHeight = 20;

    public Cell[][] grid;
    public boolean[][] occupied = new boolean[gridWidth][gridHeight];

    public List<Cell> path = new ArrayList<>();
    public List<Tower> towers = new ArrayList<>();
    public List<Enemy> enemies = new ArrayList<>();
    public List<Friendly> friends = new ArrayList<>();
    public List<Bullet> bullets = new ArrayList<>();
    public List<EnemySpawner> enemySpawners = new ArrayList<>();
    public List<FriendlySpawner> friendlySpawners = new ArrayList<>();

    public GameWorld() {
        grid = new Cell[gridWidth][gridHeight];
        occupied = new boolean[gridWidth][gridHeight];
        generateWorld();
    }

    private void generateWorld() {
        clearWorld();
        generateZones();
        populatePath();
        fillEmptyCells();
    }

    private void clearWorld() {
        path.clear();
        enemySpawners.clear();
        friendlySpawners.clear();

        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                grid[x][y] = null;
                occupied[x][y] = false;
            }
        }
    }

    private static final int ZONE_SIZE = 7;

    private void generateZones() {
        for (int x = 0; x < ZONE_SIZE; x++) {
            for (int y = 0; y < ZONE_SIZE; y++) {
                grid[x][y] = new Cell(
                    Cell.Type.HOME,
                    x * cellSize,
                    y * cellSize,
                    Direction.NONE
                );
                occupied[x][y] = false;
            }
        }
        for (int x = gridWidth - ZONE_SIZE; x < gridWidth; x++) {
            for (int y = gridHeight - ZONE_SIZE; y < gridHeight; y++) {
                grid[x][y] = new Cell(
                    Cell.Type.ENEMY,
                    x * cellSize,
                    y * cellSize,
                    Direction.NONE
                );
                occupied[x][y] = true;
            }
        }
    }

    private void populatePath() {
        path.clear();
        enemySpawners.clear();
        friendlySpawners.clear();

        PathGenerator generator =
            new PathGenerator(gridWidth, gridHeight, cellSize);

        List<Cell> generatedPath = generator.generatePathAttempts();

        for (int i = 0; i < generatedPath.size(); i++) {
            Cell cell = generatedPath.get(i);

            int gx = MathUtils.clamp((int) (cell.x / cellSize), 0, gridWidth - 1);
            int gy = MathUtils.clamp((int) (cell.y / cellSize), 0, gridHeight - 1);

            grid[gx][gy] = cell;
            path.add(cell);

            if (i == 0) {
                enemySpawners.add(new EnemySpawner(cell.x, cell.y));
            }
            if (i == (generatedPath.size()-1)) {
                friendlySpawners.add(new FriendlySpawner(cell.x, cell.y));
            }
        }
    }

    private void fillEmptyCells() {
        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                if (grid[x][y] == null) {
                    grid[x][y] = new Cell(
                        Cell.Type.TOWER,
                        x * cellSize,
                        y * cellSize,
                        Direction.NONE
                    );
                }
            }
        }
    }
}
