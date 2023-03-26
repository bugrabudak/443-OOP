package Components;

import Actors.AbstractActor;

import java.util.ArrayList;
import java.util.Map;

public class CollisionComponentWithDamage extends CollisionComponent implements IRealTimeComponent
{
    public CollisionComponentWithDamage(AbstractActor attachedActor, Map<AbstractActor, ICollisionListener> others) {
        super(attachedActor, others);
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
                pair.getValue().aCollisionIsHappened(attachedActor,true);
                attachedActor.makeDead();
            }
        }
        removedActors.forEach(abstractActor -> others.remove(abstractActor));
    }
}
