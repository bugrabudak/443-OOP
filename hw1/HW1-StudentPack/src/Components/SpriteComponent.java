package Components;

import Util.AABB;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;

public class SpriteComponent implements IDrawable
{
    private BufferedImage img;

    public SpriteComponent(String filePath) throws IOException
    {
        img = ImageIO.read(new File(filePath));
    }

    /**
     * Draws a static sprite.
     *
     * @param g Current frame's graphic class
     * @param box surface area of the sprite that is going to be drawn
     *
     */
    @Override
    public void draw(Graphics2D g, AABB box)
    {
        // Draw to the closest pixel,
        g.drawImage(img,
                    Math.round(box.getPos().x),
                    Math.round(box.getPos().y),
                    Math.round(box.getSizeX()),
                    Math.round(box.getSizeY()),
                    (ImageObserver)null);
    }
    @Override
    public void update(float deltaT)
    {
        /* No Animation of the sprites so do nothing */

        // If you really into coding games, we have provided
        // a sprite map for the player and the enemy.
        // You can tinker with them if you want.
        //
        // You will NOT get extra points though!!!!!!!
    }
}
