package Components;

import Actors.Enemy;
import Util.Position2D;

import java.util.ArrayList;

public class HorizontalPatrolStrategy extends AbstractPatrolStrategy
{
    public HorizontalPatrolStrategy(Enemy enemy){
        this.attachedEnemy = enemy;
    }
    @Override
    public void update(float deltaT)
    {
        Position2D<Float> pos = attachedEnemy.getPos();
        float speed = attachedEnemy.getActorSpeed();
        float movement = speed * deltaT;
        pos.y = attachedEnemy.isPositiveDirection() ? pos.y + movement : pos.y - movement;
        attachedEnemy.setPos(pos);
    }
}
