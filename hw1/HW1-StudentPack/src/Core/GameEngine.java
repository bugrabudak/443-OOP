package Core;

import Actors.*;
import Components.*;
import Util.AABB;
import Util.GameMapLoader;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class GameEngine
{
    private final Dimension screenSize;
    private final String currentMap;
    // Game Objects
    private Player player;
    // Concrete Types of the game
    private ArrayList<Wall> walls;
    private ArrayList<Enemy> enemies;
    private ArrayList<PowerUp> powerUps;
    private ArrayList<Bullet> bulletsInCirculation;
    // Add extra components if you like
    private ArrayList<IRealTimeComponent> miscComponents;

    private void ResetGame()
    {
        bulletsInCirculation.clear();
        walls.clear();
        enemies.clear();
        powerUps.clear();
        miscComponents.clear();

        GameMapLoader map = new GameMapLoader(screenSize);
        boolean mapOK = map.loadMap(this.currentMap);

        if(!mapOK)
        {
            System.out.println("Util.Map Load Failed!");
            System.exit(1);
        }

        // TODO: Add code if your design requires so
        IDrawable enemySpriteComponent;
        IDrawable playerSpriteComponent;
        IDrawable powerUpSpriteComponent;
        IDrawable wallSpriteComponent;
        IDrawable bulletSpriteComponent;

        try {
            enemySpriteComponent=  new SpriteComponent("./data/img/enemy.png");
            playerSpriteComponent=  new SpriteComponent("./data/img/player.png");
            powerUpSpriteComponent=  new SpriteComponent("./data/img/scroll.png");
            wallSpriteComponent=  new SpriteComponent("./data/img/wall.png");
            bulletSpriteComponent=  new SpriteComponent("./data/img/bullet.png");
        } catch(Exception e){
            System.out.println(e.toString());
            return;
        }

        ArrayList<Enemy> verticalEnemies = new ArrayList<Enemy>() ;
        ArrayList<Enemy> horizontalEnemies = new ArrayList<Enemy>() ;
        ArrayList<Enemy> stationaryEnemies = new ArrayList<Enemy>() ;

        map.getLoadedWallAABBs().forEach(aabb ->
                this.walls.add(new Wall(aabb.getPos(), aabb.getSizeX(),aabb.getSizeY(),wallSpriteComponent)));

        map.getLoadedPowerUpAABBs().forEach(aabb ->
                this.powerUps.add(new PowerUp(aabb.getPos(), aabb.getSizeX(),aabb.getSizeY(),powerUpSpriteComponent)));

        map.getLoadedEnemyXAABBs().forEach(aabb ->
                verticalEnemies.add(new Enemy(aabb.getPos(), aabb.getSizeX(),aabb.getSizeY(),enemySpriteComponent)));
        for (Enemy verticalEnemy : verticalEnemies) {
            verticalEnemy.addComponent(new VerticalPatrolStrategy(verticalEnemy));
        }

        map.getLoadedEnemyYAABBs().forEach(aabb ->
                horizontalEnemies.add(new Enemy(aabb.getPos(), aabb.getSizeX(),aabb.getSizeY(),enemySpriteComponent)));
        for (Enemy horizontalEnemy : horizontalEnemies) {
            horizontalEnemy.addComponent(new HorizontalPatrolStrategy(horizontalEnemy));
        }

        map.getLoadedEnemyStationaryAABBs().forEach(aabb ->
                stationaryEnemies.add(new Enemy(aabb.getPos(), aabb.getSizeX(),aabb.getSizeY(),enemySpriteComponent)));

        enemies.addAll(verticalEnemies);
        enemies.addAll(horizontalEnemies);
        enemies.addAll(stationaryEnemies);

        AABB player = map.getLoadedPlayerAABB();
        this.player = new Player(player.getPos(),player.getSizeX(),player.getSizeY(),playerSpriteComponent);

        PlayerInputComponent inputComponent = new PlayerInputComponent(this.player,
                                                                        bulletSpriteComponent,
                                                                        this.bulletsInCirculation,this.enemies);
        GameWindow.GetInstance().attachKeyListener(inputComponent);
        this.miscComponents.add(inputComponent);

        // Setting Collision Components
        Map<AbstractActor,ICollisionListener> wallListenerMap = new HashMap<>();
        for (Enemy enemy : enemies) {
            wallListenerMap.put(enemy,enemy);
        }
        wallListenerMap.put(this.player,this.player);

        for (Wall wall : walls) {
            CollisionComponent collisionComponent = new CollisionComponent(wall,wallListenerMap);
            wall.addComponent(collisionComponent);
        }

        Map<AbstractActor,ICollisionListener> playerListenerMap = new HashMap<>();
        for (PowerUp powerUp : powerUps) {
            playerListenerMap.put(powerUp,powerUp);
        }
        this.player.addComponent(new CollisionComponent(this.player,playerListenerMap));
        Map<AbstractActor,ICollisionListener> playerDamagedListenerMap = new HashMap<>();
        for (Enemy enemy : enemies) {
            playerDamagedListenerMap.put(enemy,enemy);
        }
        this.player.addComponent(new CollisionComponentWithDamage(this.player,playerDamagedListenerMap));
    }

    public GameEngine(String mapFilePath, Dimension screenSize)
    {
        this.currentMap = mapFilePath;
        this.screenSize = screenSize;

        this.walls = new ArrayList<Wall>();
        this.enemies = new ArrayList<Enemy>();
        this.powerUps = new ArrayList<PowerUp>();
        this.bulletsInCirculation = new ArrayList<Bullet>();
        this.miscComponents = new ArrayList<IRealTimeComponent>();

        // TODO: Add code if your design requires so

        ResetGame();
    }

    public synchronized void update(float deltaT, Graphics2D currentDrawBuffer)
    {
        // ==================================== //
        // YOU SHOULD NOT CHANGE THIS FUNCTION  //
        // ============================================= //
        // THIS SHOULD ALREADY DOES EVERYTHING YOU NEED  //
        // ============================================= //
        // You can still change it though with a penalty.

        // Do update first
        walls.forEach(actor -> actor.update(deltaT, currentDrawBuffer));
        enemies.forEach(actor -> actor.update(deltaT, currentDrawBuffer));
        powerUps.forEach(actor -> actor.update(deltaT, currentDrawBuffer));
        bulletsInCirculation.forEach(actor-> actor.update(deltaT, currentDrawBuffer));
        player.update(deltaT, currentDrawBuffer);
        miscComponents.forEach(c -> c.update(deltaT));
        // Now stuff would die etc. check the states and delete
        enemies.removeIf(actor -> actor.isDead());
        powerUps.removeIf(actor -> actor.isDead());
        bulletsInCirculation.removeIf(actor -> actor.isDead());
        // If player dies game is over reset the system
        if(player.isDead())
        {
            ResetGame();
        }
        // If there are no power-ups left,
        // Player won the game!, still reset..
        if(powerUps.isEmpty())
        {
            ResetGame();
        }
        // And the game goes on forever...
    }
}
