package com.Geary.towerdefense.world;

import com.Geary.towerdefense.Direction;
import com.Geary.towerdefense.UI.displays.modal.Modal;
import com.Geary.towerdefense.UI.displays.modal.manufacturing.FactoryModal;
import com.Geary.towerdefense.UI.displays.modal.spawner.SpawnerModalManager;
import com.Geary.towerdefense.UI.displays.modal.tower.TowerModal;
import com.Geary.towerdefense.behaviour.MobManager;
import com.Geary.towerdefense.behaviour.ResourceManager;
import com.Geary.towerdefense.behaviour.SparkManager;
import com.Geary.towerdefense.behaviour.SpawnerManager;
import com.Geary.towerdefense.behaviour.buildings.manager.ManufactoryManager;
import com.Geary.towerdefense.behaviour.buildings.manager.ProductionManager;
import com.Geary.towerdefense.behaviour.buildings.manager.TowerManager;
import com.Geary.towerdefense.behaviour.buildings.manager.TransportManager;
import com.Geary.towerdefense.entity.buildings.Building;
import com.Geary.towerdefense.entity.buildings.Transport;
import com.Geary.towerdefense.entity.buildings.factory.Manufacturing;
import com.Geary.towerdefense.entity.buildings.production.Production;
import com.Geary.towerdefense.entity.buildings.tower.Tower;
import com.Geary.towerdefense.entity.mob.bullet.Bullet;
import com.Geary.towerdefense.entity.mob.enemy.Enemy;
import com.Geary.towerdefense.entity.mob.friendly.Friendly;
import com.Geary.towerdefense.entity.resources.Resource;
import com.Geary.towerdefense.entity.spawner.EnemySpawner;
import com.Geary.towerdefense.entity.spawner.FriendlySpawner;
import com.Geary.towerdefense.entity.world.Cell;
import com.Geary.towerdefense.levelSelect.levels.LevelData;
import com.Geary.towerdefense.pathGeneration.PathGenerator;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
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

    Map<Resource.RawResourceType, Integer> resourceAllocation = new HashMap<>();
    public List<Cell> path = new ArrayList<>();

    public List<Tower> towers = new ArrayList<>();
    public Tower ghostTower = null;
    public List<Transport> transports = new ArrayList<>();
    public Transport ghostTransport = null;
    public List<Production> productions = new ArrayList<>();
    public Production ghostProduction = null;
    public List<Manufacturing> factories = new ArrayList<>();
    public Manufacturing ghostManufacturing = null;

    public List<Enemy> enemies = new ArrayList<>();
    public List<Friendly> friends = new ArrayList<>();
    public List<Bullet> bullets = new ArrayList<>();
    public List<EnemySpawner> enemySpawners = new ArrayList<>();
    public List<FriendlySpawner> friendlySpawners = new ArrayList<>();

    private ResourceManager resourceManager;
    private TowerManager towerManager;
    private TransportManager transportManager;
    private ProductionManager productionManager;
    private ManufactoryManager manufactoryManager;
    private MobManager mobManager;
    private SparkManager sparkManager;
    private SpawnerManager spawnerManager;
    private GameStateManager gameStateManager;
    private SpawnerModalManager spawnerModalManager;

    private Modal activeModal;


    private OrthographicCamera worldCamera;

    public GameWorld(LevelData levelData) {
        grid = new Cell[gridWidth][gridHeight];
        occupied = new boolean[gridWidth][gridHeight];

        this.resourceManager = new ResourceManager(this);

        // Use levelData if provided, otherwise fallback to defaults
        if (levelData != null && levelData.getResourceAllocation() != null) {
            this.resourceAllocation.putAll(levelData.getResourceAllocation());
        } else {
            resourceAllocation.put(Resource.RawResourceType.IRON, 2);
            resourceAllocation.put(Resource.RawResourceType.COAL, 2);
            resourceAllocation.put(Resource.RawResourceType.COPPER, 2);
            resourceAllocation.put(Resource.RawResourceType.STONE, 2);
            resourceAllocation.put(Resource.RawResourceType.TIN, 2);
        }

        generateWorld(resourceAllocation);
    }


    public void initManagers(OrthographicCamera worldCamera) {
        this.worldCamera = worldCamera;
        sparkManager = new SparkManager(100);
        towerManager = new TowerManager(this, worldCamera);
        transportManager = new TransportManager(this, worldCamera);
        productionManager = new ProductionManager(this, worldCamera);
        manufactoryManager = new ManufactoryManager(this, worldCamera);
        mobManager = new MobManager(this, sparkManager); // no camera
        spawnerManager = new SpawnerManager(this);       // no camera
        gameStateManager = new GameStateManager();
        spawnerModalManager = new SpawnerModalManager();
    }

    private void generateWorld(Map<Resource.RawResourceType, Integer> resourceAllocation) {
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
            if (i == (generatedPath.size() - 1)) {
                FriendlySpawner spawner = new FriendlySpawner(cell.x, cell.y);
                friendlySpawners.add(spawner);
                grid[gx][gy].building = spawner;
                grid[gx][gy].bridgable = false;
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

    public void update(float delta) {
        towerManager.updateTowers(this.bullets, delta);
        transportManager.updateTransports(delta);
        mobManager.update(delta);
        spawnerManager.update(delta);
        sparkManager.update(delta);
        productionManager.animateMines(delta);
        manufactoryManager.animateFactories(delta);
        productionManager.calculateResourcesGenerated(delta);
        manufactoryManager.handleFactoryProduction(delta);
        spawnerModalManager.updateSpawner(delta);
    }

    public void deleteBuilding(Building building) {
        if (building == null) return;

        if (building instanceof Tower tower) {
            towers.remove(tower);
            towerManager.deleteTower(tower);
        } else if (building instanceof Transport transport) {
            transports.remove(transport);
            transportManager.deleteTransport(transport);
        } else if (building instanceof Production production) {
            productions.remove(production);
            productionManager.deleteBuilding(production, productions);
        } else if (building instanceof Manufacturing manufacturing) {
            factories.remove(manufacturing);
            manufactoryManager.deleteBuilding(manufacturing, factories);
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

    public void showFactoryModal(Manufacturing manufacturing, BitmapFont font, OrthographicCamera uiCamera) {
        activeModal = new FactoryModal(manufacturing, font, uiCamera);
    }

    public void showSpawnerModal(FriendlySpawner spawner, BitmapFont font, OrthographicCamera uiCamera) {
        activeModal = spawnerModalManager.getSpawnerModal(spawner, gameStateManager, font, uiCamera);
    }

    public void showTowerModal(Tower tower, BitmapFont font, OrthographicCamera uiCamera) {
        activeModal = new TowerModal(tower, gameStateManager, font, uiCamera);
    }

    public void setActiveModal(Modal modal) {
        this.activeModal = modal;
    }

    public void closeModal() {
        this.activeModal = null;
    }

    public Modal getActiveModal() {
        return activeModal;
    }

    public TowerManager getTowerManager() {
        return towerManager;
    }

    public TransportManager getTransportManager() {
        return transportManager;
    }

    public ProductionManager getProductionManager() {
        return productionManager;
    }

    public ManufactoryManager getFactoryManager() {
        return manufactoryManager;
    }

    public MobManager getMobManager() {
        return mobManager;
    }

    public SparkManager getSparkManager() {
        return sparkManager;
    }

    public SpawnerManager getSpawnerManager() {
        return spawnerManager;
    }

    public GameStateManager getGameStateManager() {
        return gameStateManager;
    }

    public OrthographicCamera getWorldCamera() {
        return worldCamera;
    }
}
