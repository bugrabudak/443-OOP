package Actors;

import Components.IDrawable;
import Components.IRealTimeComponent;
import Util.AABB;
import Util.Position2D;

import java.awt.*;
import java.util.ArrayList;

// Meta Actor Class
// Everything in the game is an actor
public abstract class AbstractActor extends AABB
{
    private ArrayList<IRealTimeComponent> components;
    private IDrawable spriteComponent;

    protected float actorSpeed = 0;
    protected boolean isDead = false;
    /**
     * Constructor, directly sets the every parameter
     *
     * @param pos "top right" (wrt. the screen coordinates) of the box
     * @param szX horizontal size of the box in pixels
     * @param szY vertical size of the box in pixels
     */
    public AbstractActor(Position2D<Float> pos, float szX, float szY, IDrawable spriteComponent) {
        super(pos, szX, szY);
        this.components = new ArrayList<IRealTimeComponent>();
        this.spriteComponent = spriteComponent;
    }

    public void update(float deltaT, Graphics2D g)
    {
        for (IRealTimeComponent component : components) {
            component.update(deltaT);
        }
        spriteComponent.draw(g,this);
    }

    public abstract boolean isDead();
    public void makeDead() {
        this.isDead = true;
    }
    public float getActorSpeed() { return this.actorSpeed; }
    public void addComponent(IRealTimeComponent component) {
        components.add(component);
    }
}
