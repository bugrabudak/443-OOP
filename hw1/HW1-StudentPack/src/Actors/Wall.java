package Actors;

import Components.IDrawable;
import Components.IRealTimeComponent;
import Util.Position2D;

import java.awt.*;
import java.util.ArrayList;

public class Wall extends AbstractActor
{

    public Wall(Position2D<Float> pos, float szX, float szY, IDrawable spriteComponent) {
        super(pos, szX, szY,spriteComponent);
    }

    @Override
    public void update(float deltaT, Graphics2D g)
    {
        super.update(deltaT,g);
    }

    @Override
    public boolean isDead()
    {
        return false;
    }
}
