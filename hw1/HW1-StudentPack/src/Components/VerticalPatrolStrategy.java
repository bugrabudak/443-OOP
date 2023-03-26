package Components;

import Actors.Enemy;
import Util.Position2D;

import java.util.ArrayList;

public class VerticalPatrolStrategy extends AbstractPatrolStrategy
{
    public VerticalPatrolStrategy(Enemy enemy){
        this.attachedEnemy = enemy;
    }
    @Override
    public void update(float deltaT)
    {
        Position2D<Float> pos = attachedEnemy.getPos();
        float speed = attachedEnemy.getActorSpeed();
        float movement = speed * deltaT;
        pos.x = attachedEnemy.isPositiveDirection() ? pos.x + movement : pos.x - movement;
        attachedEnemy.setPos(pos);
    }
}
