package Components;

import Actors.AbstractActor;
import Actors.Bullet;
import Actors.Enemy;
import Actors.Player;
import Core.GameWindow;
import Util.Position2D;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PlayerInputComponent implements IRealTimeComponent, KeyListener
{
    // Internal States
    private boolean leftPressed;
    private boolean rightPressed;
    private boolean upPressed;
    private boolean downPressed;
    private boolean firePressed;

    private Player player;
    private IDrawable bulletSpriteComponent;
    private ArrayList<Bullet> bulletsInCirculation;
    private Bullet.Direction direction = Bullet.Direction.RIGHT;
    private ArrayList<Enemy> enemies;

    public PlayerInputComponent(Player player,
                                IDrawable bulletSpriteComponent,
                                ArrayList<Bullet> bulletsInCirculation,
                                ArrayList<Enemy> enemies)
    {
        this.player = player;
        this.bulletsInCirculation = bulletsInCirculation;
        this.bulletSpriteComponent = bulletSpriteComponent;
        this.enemies = enemies;
    }

    @Override
    public void update(float deltaT)
    {
        if(this.player.isDead()) {
            return;
        }
        Position2D<Float> pos = this.player.getPos();
        float speed = this.player.getActorSpeed();
        float movement = speed * deltaT;
        if(leftPressed) {
            pos.x -= movement;
            direction = Bullet.Direction.LEFT;
        }
        if(rightPressed) {
            pos.x += movement;
            direction = Bullet.Direction.RIGHT;
        }
        if(upPressed) {
            pos.y -= movement;
            direction = Bullet.Direction.UP;
        }
        if(downPressed) {
            pos.y += movement;
            direction = Bullet.Direction.DOWN;
        }
        this.player.setPos(pos);

        if(firePressed) {
            Position2D<Float> bulletPos = new Position2D<>(pos.x, pos.y);
            float szX = this.player.getSizeX();
            float szY = this.player.getSizeY();
            Bullet bullet = new Bullet(bulletPos,szX/2,szY/2,bulletSpriteComponent,direction);
            Map<AbstractActor,ICollisionListener> bulletListenerMap = new HashMap<>();
            for (Enemy enemy : enemies) {
                bulletListenerMap.put(enemy,enemy);
            }
            bullet.addComponent(new CollisionComponentWithDamage(bullet,bulletListenerMap));
            bulletsInCirculation.add(bullet);
            firePressed = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) { /* Do nothing */ }

    @Override
    public void keyPressed(KeyEvent e)
    {
        if(e.getKeyCode() == KeyEvent.VK_LEFT) leftPressed = true;
        if(e.getKeyCode() == KeyEvent.VK_RIGHT) rightPressed = true;
        if(e.getKeyCode() == KeyEvent.VK_UP) upPressed = true;
        if(e.getKeyCode() == KeyEvent.VK_DOWN) downPressed = true;
        // TODO: You can also change this code if you want to handle inputs differently
        // this is given as a guideline to read key events
    }

    @Override
    public void keyReleased(KeyEvent e)
    {
        if(e.getKeyCode() == KeyEvent.VK_LEFT) leftPressed = false;
        if(e.getKeyCode() == KeyEvent.VK_RIGHT) rightPressed = false;
        if(e.getKeyCode() == KeyEvent.VK_UP) upPressed = false;
        if(e.getKeyCode() == KeyEvent.VK_DOWN) downPressed = false;
        // Enforce release operation on fire
        if(e.getKeyCode() == KeyEvent.VK_SPACE) firePressed = true;
        // TODO: You can also change this code if you want to handle inputs differently
        // this is given as a guideline to read key events
    }
}
