package Actors;

import Components.IDrawable;
import Components.IRealTimeComponent;
import Util.Position2D;

import java.awt.*;
import java.util.ArrayList;

public class Bullet extends AbstractActor
{
    static public enum Direction {
        UP,
        DOWN,
        RIGHT,
        LEFT
    }
    private Direction direction;
    private float bulletLife = 0.7f;

    public Bullet(Position2D<Float> pos, float szX, float szY, IDrawable spriteComponent, Direction direction) {
        super(pos, szX, szY,spriteComponent);
        this.direction = direction;
        this.actorSpeed = 300;
    }

    @Override
    public void update(float deltaT, Graphics2D g)
    {
        bulletLife -= deltaT;
        if(bulletLife < 0) {
            this.makeDead();
            return;
        }
        Position2D<Float> pos = this.getPos();
        float speed = this.getActorSpeed();
        float movement = speed * deltaT;
        switch(direction){
            case UP -> pos.y -= movement;
            case DOWN -> pos.y += movement;
            case LEFT -> pos.x -= movement;
            case RIGHT -> pos.x += movement;
        }
        this.setPos(pos);
        super.update(deltaT,g);
    }

    @Override
    public boolean isDead()
    {
        return isDead;
    }

}
