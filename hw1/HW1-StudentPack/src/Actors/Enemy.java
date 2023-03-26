package Actors;

import Components.ICollisionListener;
import Components.IDrawable;
import Components.IRealTimeComponent;
import Util.AABB;
import Util.Position2D;

import java.awt.*;
import java.util.ArrayList;

public class Enemy extends AbstractActor implements ICollisionListener
{
    private boolean positiveDirection;
    private int health = 100;
    public Enemy(Position2D<Float> pos, float szX, float szY, IDrawable spriteComponent) {
        super(pos, szX, szY,spriteComponent);
        this.positiveDirection = true;
        this.actorSpeed = 120;
    }

    @Override
    public boolean isDead()
    {
        return isDead;
    }

    public boolean isPositiveDirection()
    {
        return positiveDirection;
    }

    @Override
    public void update(float deltaT, Graphics2D g)
    {
        if (health <= 0) {
            this.makeDead();
            return;
        }
        super.update(deltaT,g);

    }

    @Override
    public void aCollisionIsHappened(AABB other,boolean shouldTakeDamage) {
        if(shouldTakeDamage){
            this.health -= 50;
            return;
        }
        if (moveIfCollide(other)) {
            this.positiveDirection = !positiveDirection;
        }
    }
}
