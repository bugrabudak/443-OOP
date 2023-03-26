package Actors;

import Components.ICollisionListener;
import Components.IDrawable;
import Components.IRealTimeComponent;
import Util.AABB;
import Util.Position2D;

import java.awt.*;
import java.util.ArrayList;

public class Player extends AbstractActor implements ICollisionListener
{
    public Player(Position2D<Float> pos, float szX, float szY, IDrawable spriteComponent) {
        super(pos, szX, szY,spriteComponent);
        this.actorSpeed = 110;
    }

    @Override
    public void update(float deltaT, Graphics2D g)
    {
        super.update(deltaT,g);
    }

    @Override
    public boolean isDead()
    {
        return isDead;
    }


    @Override
    public void aCollisionIsHappened(AABB other,boolean shouldTakeDamage) {
        if (shouldTakeDamage) {
           makeDead();
           return;
        }
        moveIfCollide(other);
    }
}
