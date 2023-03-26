import Core.GameEngine;
import Core.GameWindow;

import java.awt.*;

public class Main
{
    public static void main(String[] args)
    {
        if (args.length == 0)
        {
            System.out.println("Map is Not Available!");
            return;
        }
        Dimension screenDimension = GameWindow.GetInstance().getScreenDimension();
        GameEngine engine = new GameEngine(args[0], screenDimension);
        GameWindow.GetInstance().setGame(engine);

    }
}
