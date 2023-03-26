package Components;

import Util.AABB;

public interface ICollisionListener
{
    public void aCollisionIsHappened(AABB other,boolean shouldTakeDamage);
}
