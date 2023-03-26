package Util;

import static java.lang.Math.abs;

/**
 * Axis-aligned bounding box (AABB) class
 * In this game, it stores the Actor position and the size of the actor as well.
 * Spire class also uses this to render a sprite (an image) to the screen.
 */
public class AABB
{
    private class Hit
    {
        public float offsetX;
        public float offsetY;
        public float signX;
        public float signY;
    };

    // Positions
    private Position2D<Float> pos; // Top Left Corner Position
    //
    private float szX;
    private float szY;

    /**
     * DRY principle, collide & moveIf collide shares functionality
     * this function encapsulates that
     */
    private Hit collideInternal(AABB other)
    {
        Hit result = new Hit();
        // Check X
        float diffX = ((pos.x + 0.5f * szX) -
                       (other.pos.x + 0.5f * other.szX));
        result.offsetX = (this.szX + other.szX) * 0.5f - abs(diffX);
        // Check Y
        float diffY = ((pos.y + 0.5f * szY) -
                       (other.pos.y + 0.5f * other.szY));
        result.offsetY = (this.szY + other.szY) * 0.5f - abs(diffY);
        result.signX = (diffX < 0) ? -1 : 1;
        result.signY = (diffY < 0) ? -1 : 1;
        return result;
    }

    /**
     Constructor, directly sets the every parameter
     * @param pos "top right" (wrt. the screen coordinates) of the box
     * @param szX   horizontal size of the box in pixels
     * @param szY   vertical size of the box in pixels
     */
    public AABB(Position2D<Float> pos, float szX, float szY)
    {
        this.pos = pos;
        this.szX = szX;
        this.szY = szY;
    }

    /**
     * Checks collision between two AABBs, returns true if two aabbs collide (intersects)
     * with each other.
     *
     * @param other other aabb that will be checked with this one
     */
    public boolean collides(AABB other)
    {
        Hit offsets = collideInternal(other);
        if(offsets.offsetX <= 0) return false;
        if(offsets.offsetY <= 0) return false;
        return true;
    }

    /**
     * Moves this AABB to the nearest valid location, if two aabbs collide (intersects).
     * Does not touch the other aabb. Usefull for checking moving objects with static objects,
     * (ie. player vs. walls)
     *
     * @param other other aabb that will be checked with this one
     */
    public boolean moveIfCollide(AABB other)
    {
        if(!collides(other)) return false;
        Hit hit = collideInternal(other);
        // We are at the "left" or "right" of the box
        // Only change X
        if(hit.offsetX < hit.offsetY)  { pos.x += hit.offsetX * hit.signX; }
        // We are at "top" or "bottom" same but for Y
        else  { pos.y += hit.offsetY * hit.signY; }
        // All Done!
        return true;
    }

    /**
     * Shrinks the aabb from the center, with different dimensions
     * @param percentageX should be between (0, 1.0], 1.0 means no shrink
     * @param percentageY should be between (0, 1.0], 1.0 means no shrink
     */
    public void shrink(float percentageX, float percentageY)
    {
        float newSizeX = percentageX * szX;
        float newSizeY = percentageY * szY;
        float diffX = szX - newSizeX;
        float diffY = szY - newSizeY;
        pos.x = pos.x + (diffX * 0.5f);
        pos.y = pos.y + (diffY * 0.5f);
        szX = newSizeX;
        szY = newSizeY;
    }

    /**
     * Shrinks the aabb from the center
     * @param percentage should be between (0, 1.0], 1.0 means no shrink
     */
    public void shrink(float percentage)
    {
        shrink(percentage, percentage);
    }

    /**
     * Accessors
     */
    public Position2D<Float> getPos() { return pos; }
    public float getSizeX() { return szX; }
    public float getSizeY() { return szY; }
    public Position2D<Float> getCenter()
    {
        return new Position2D<>(pos.x + szX * 0.5f,
                               pos.y + szY * 0.5f);
    }

    public void setPos(Position2D<Float> pos) { this.pos = pos; }
}