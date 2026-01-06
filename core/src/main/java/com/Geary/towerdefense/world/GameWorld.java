package com.Geary.towerdefense.world;

import com.Geary.towerdefense.Direction;
import com.Geary.towerdefense.behaviour.MobManager;
import com.Geary.towerdefense.behaviour.ResourceManager;
import com.Geary.towerdefense.behaviour.SparkManager;
import com.Geary.towerdefense.behaviour.SpawnerManager;
import com.Geary.towerdefense.behaviour.buildings.manager.MineManager;
import com.Geary.towerdefense.behaviour.buildings.manager.TowerManager;
import com.Geary.towerdefense.behaviour.buildings.manager.TransportManager;
import com.Geary.towerdefense.entity.buildings.Building;
import com.Geary.towerdefense.entity.buildings.Mine;
import com.Geary.towerdefense.entity.buildings.Tower;
import com.Geary.towerdefense.entity.buildings.Transport;
import com.Geary.towerdefense.entity.mob.Bullet;
import com.Geary.towerdefense.entity.mob.Enemy;
import com.Geary.towerdefense.entity.mob.Friendly;
import com.Geary.towerdefense.entity.resources.Resource;
import com.Geary.towerdefense.entity.spawner.EnemySpawner;
import com.Geary.towerdefense.entity.spawner.FriendlySpawner;
import com.Geary.towerdefense.entity.world.Cell;
import com.Geary.towerdefense.pathGeneration.PathGenerator;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameWorld {
    private static final int ZONE_SIZE = 7;

    public static final int cellSize = 100;
    public final int gridWidth = 20;
    public final int gridHeight = 20;

    public Cell[][] grid;
    public boolean[][] occupied = new boolean[gridWidth][gridHeight];

    Map<Resource.ResourceType, Integer> resourceAllocation = new HashMap<>();
    public List<Cell> path = new ArrayList<>();

    public List<Tower> towers = new ArrayList<>();
    public Tower ghostTower = null;
    public List<Transport> transports = new ArrayList<>();
    public Transport ghostTransport = null;
    public List<Mine> mines = new ArrayList<>();
    public Mine ghostMine = null;

    public List<Enemy> enemies = new ArrayList<>();
    public List<Friendly> friends = new ArrayList<>();
    public List<Bullet> bullets = new ArrayList<>();
    public List<EnemySpawner> enemySpawners = new ArrayList<>();
    public List<FriendlySpawner> friendlySpawners = new ArrayList<>();

    private ResourceManager resourceManager;
    private TowerManager towerManager;
    private TransportManager transportManager;
    private MineManager mineManager;
    private MobManager mobManager;
    private SparkManager sparkManager;
    private SpawnerManager spawnerManager;

    public GameWorld() {
        grid = new Cell[gridWidth][gridHeight];
        occupied = new boolean[gridWidth][gridHeight];
        this.resourceManager = new ResourceManager(this);
        resourceAllocation.put(Resource.ResourceType.IRON, 4);
        generateWorld(resourceAllocation);
    }

    public void initManagers(OrthographicCamera worldCamera) {
        sparkManager = new SparkManager(100);

        towerManager = new TowerManager(this, worldCamera);
        transportManager = new TransportManager(this, worldCamera);
        mineManager = new MineManager(this, worldCamera);

        mobManager = new MobManager(this, sparkManager); // no camera
        spawnerManager = new SpawnerManager(this);       // no camera
    }
    public TowerManager getTowerManager() { return towerManager; }
    public TransportManager getTransportManager() { return transportManager; }
    public MineManager getMineManager() { return mineManager; }
    public MobManager getMobManager() { return mobManager; }
    public SparkManager getSparkManager() { return sparkManager; }
    public SpawnerManager getSpawnerManager() { return spawnerManager; }

    private void generateWorld(Map<Resource.ResourceType, Integer> resourceAllocation) {
        clearWorld();
        generateZones();
        populatePath();
        fillEmptyCells();
        resourceManager.populate(resourceAllocation);
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
                FriendlySpawner spawner = new FriendlySpawner(cell.x, cell.y);
                friendlySpawners.add(spawner);
                grid[gx][gy].building = spawner;
            }
        }
    }

    public List<Cell> getFreeCells() {
        List<Cell> freeCells = new ArrayList<>();

        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                Cell cell = grid[x][y];

                if ((cell.type == Cell.Type.EMPTY || cell.type == Cell.Type.HOME)
                    && !occupied[x][y]
                    && cell.resource == null) {
                    freeCells.add(cell);
                }
            }
        }
        return freeCells;
    }

    private void fillEmptyCells() {
        for (int x = 0; x < gridWidth; x++) {
            for (int y = 0; y < gridHeight; y++) {
                if (grid[x][y] == null) {
                    grid[x][y] = new Cell(
                        Cell.Type.EMPTY,
                        x * cellSize,
                        y * cellSize,
                        Direction.NONE
                    );
                }
            }
        }
    }

    /** Update all in-world events; called from GameScreen */
    public void update(float delta) {
        towerManager.updateTowers(this.bullets, delta);
        transportManager.updateTransports(delta);
        mobManager.update(delta);
        spawnerManager.update(delta);
        sparkManager.update(delta);
        mineManager.animateMines(delta);
    }

    public void deleteBuilding(Building building) {
        if (building == null) return;

        // Remove from type-specific lists
        if (building instanceof Tower tower) {
            towers.remove(tower);
            towerManager.deleteTower(tower); // you may need a helper for clearing occupied cells
        } else if (building instanceof Transport transport) {
            transports.remove(transport);
            transportManager.deleteTransport(transport);
        } else if (building instanceof Mine mine) {
            mines.remove(mine);
            mineManager.deleteMine(mine);
        }

        // Clear grid cell
        int x = (int) building.xPos / cellSize;
        int y = (int) building.yPos / cellSize;
        if (x >= 0 && x < gridWidth && y >= 0 && y < gridHeight) {
            if (grid[x][y].building == building) {
                grid[x][y].building = null;
            }
            occupied[x][y] = false;
        }
        transportManager.updateAllTransportLinks();
    }


}
