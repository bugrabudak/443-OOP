package Components;

import Actors.AbstractActor;
import Actors.Enemy;

import java.util.ArrayList;

public abstract class  AbstractPatrolStrategy implements IRealTimeComponent
{
    protected Enemy attachedEnemy;
    @Override
    public void update(float deltaT)
    {
        // do nothing
    }
}
