package Components;

import Actors.AbstractActor;
import Util.AABB;

import java.util.ArrayList;
import java.util.Map;

public class CollisionComponent implements IRealTimeComponent
{
    protected Map<AbstractActor,ICollisionListener> others;
    protected AbstractActor attachedActor;

    public CollisionComponent(AbstractActor attachedActor, Map<AbstractActor,ICollisionListener> others) {
        this.attachedActor = attachedActor;
        this.others = others;
    }
    @Override
    public void update(float deltaT)
    {
        ArrayList<AbstractActor> removedActors = new ArrayList<>();

        for (Map.Entry<AbstractActor, ICollisionListener> pair : others.entrySet()) {
            if(pair.getKey().isDead()){
                removedActors.add(pair.getKey());
                continue;
            }
            if(attachedActor.collides(pair.getKey())) {
                pair.getValue().aCollisionIsHappened(attachedActor,false);
            }
        }
        removedActors.forEach(abstractActor -> others.remove(abstractActor));
    }


}
