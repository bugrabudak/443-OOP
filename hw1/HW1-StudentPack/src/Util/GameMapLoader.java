package Util;

import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;
/**
 *  Game Map Loader Class
 *
 *  This game maps are tile based
 *  every map is 32 by 18 tiles
 *  map object should be created with the appropriate screen size
 *  so that it can create the AABBs properly
 *
 *  Class is little bit static in order to simply the loading process
 *  According to the type, AABBs are morphed according to the type
 *  statically.
 *
 */
public class GameMapLoader
{
    private ArrayList<AABB> loadedWallAABBs;
    private ArrayList<AABB> loadedEnemyXAABBs;
    private ArrayList<AABB> loadedEnemyYAABBs;
    private ArrayList<AABB> loadedEnemyStationaryAABBs;
    private ArrayList<AABB> loadedPowerUpAABBs;
    private AABB loadedPlayerAABB;

    private static final float MAP_WIDTH = 32;
    private static final float MAP_HEIGHT = 18;

    private static final float POWER_UP_PERCENTAGE = 0.55f;
    private static final float PLAYER_ENEMY_PERCENTAGE = 0.75f;

    // Generated From Frame Size
    Position2D<Float> gridBoxSize;

    /**
     * Find the position of the tile on the screen,
     * Returns the "top left" corner of the tile.
     * In java screen space coordinates starts from top left of the screen.
     * @param gridX tile x coordinates
     * @param gridY tile y coordinates
     * @return returns the tile position in screen space coordinates
     */
    private Position2D<Float> findTileLocation(int gridX, int gridY)
    {
        return new Position2D<Float>(gridX * gridBoxSize.x,
                                     gridY * gridBoxSize.y);
    }

    /**
     * Reads entire contents of the file to a string.
     * @param filePath path of the file (directly fed to
     * @return returns the enitre text file as a single string,
     *         returns null if file could not be opened
     */
    private String devourFileToString(String filePath)
    {
        String content = "";
        try
        {
            BufferedReader in;
            String str;
            for(in = new BufferedReader(new FileReader(filePath));
                (str = in.readLine()) != null;
                content = content + str + "\n")
            {}
            in.close();
            return content;
        } catch (IOException e) {
            return null;
        }
    }

    /**
     * Loads the map to the internal data structures of the class,
     * Creates AABBs for player, walls, power ups and enemies.
     * Contents of the GameMapLoader is unspecified if load fails
     *
     * @param filePath path of the map file,
     * @return returns true if map is loaded, false if not.
     */
    public boolean loadMap(String filePath)
    {
        System.out.println("Loading Util.Map " + filePath + "!");
        String content = this.devourFileToString(filePath);
        // Silently return
        if (content == null) return false;

        // State to check if there is multiple players
        boolean secondPlayer = false;

        // Read Line by line
        int curHeight = 0;
        StringTokenizer token = new StringTokenizer(content, "\n");
        while(token.hasMoreTokens())
        {
            String line =  token.nextToken();
            if(line.length() < MAP_WIDTH) return false;
            // Ignore the rest
            for(int i = 0; i  < MAP_WIDTH; i ++)
            {
                Position2D<Float> d = findTileLocation(i, curHeight);
                AABB gridAABB = new AABB(d,
                                         this.gridBoxSize.x,
                                         this.gridBoxSize.y);
                // Parse Char
                switch (line.charAt(i))
                {
                    // WALL
                    case '1':
                    {
                        // Walls are covers the entire grid
                        loadedWallAABBs.add(gridAABB);
                        break;
                    }
                    // POWER_UP
                    case 'p':
                    {
                        gridAABB.shrink(POWER_UP_PERCENTAGE);
                        loadedPowerUpAABBs.add(gridAABB);
                        break;
                    }
                    // ENEMY HORIZONTAL
                    case 'x':
                    {
                        gridAABB.shrink(PLAYER_ENEMY_PERCENTAGE * 0.5f,
                                        PLAYER_ENEMY_PERCENTAGE);
                        loadedEnemyXAABBs.add(gridAABB);
                        break;
                    }
                    // ENEMY VERTICAL
                    case 'y':
                    {
                        gridAABB.shrink(PLAYER_ENEMY_PERCENTAGE * 0.5f,
                                        PLAYER_ENEMY_PERCENTAGE);
                        loadedEnemyYAABBs.add(gridAABB);
                        break;
                    }
                    // ENEMY STATIONARY
                    case 'z':
                    {
                        gridAABB.shrink(PLAYER_ENEMY_PERCENTAGE * 0.5f,
                                        PLAYER_ENEMY_PERCENTAGE);
                        loadedEnemyStationaryAABBs.add(gridAABB);
                        break;
                    }
                    // PLAYER
                    case 'a':
                    {
                        if(secondPlayer) return false;
                        gridAABB.shrink(PLAYER_ENEMY_PERCENTAGE * 0.5f,
                                        PLAYER_ENEMY_PERCENTAGE);
                        loadedPlayerAABB = gridAABB;
                        secondPlayer = true;
                        break;
                    }
                    // EMPTY SPACE
                    case ' ': continue;
                    // ALL OTHER CHARS ARE NOT SUPPORTED
                    default: return false;
                }
            }
            curHeight++;
        }
        return true;
    }

    /**
     * Initializes the class according to a screen size
     * @param canvasSize screen canvas size
     */
    public GameMapLoader(Dimension canvasSize)
    {
        this.gridBoxSize = new Position2D<Float>((float)canvasSize.getWidth() / MAP_WIDTH,
                                                 (float)canvasSize.getHeight() / MAP_HEIGHT);

        loadedWallAABBs = new ArrayList<AABB>();
        loadedEnemyXAABBs = new ArrayList<AABB>();
        loadedEnemyYAABBs = new ArrayList<AABB>();
        loadedEnemyStationaryAABBs = new ArrayList<AABB>();
        loadedPowerUpAABBs = new ArrayList<AABB>();
        loadedPlayerAABB = new AABB(new Position2D<Float>(0.0f, 0.0f), 0.0f, 0.0f);
    }

    public ArrayList<AABB> getLoadedWallAABBs()  { return loadedWallAABBs; }
    public ArrayList<AABB> getLoadedEnemyXAABBs() { return loadedEnemyXAABBs; }
    public ArrayList<AABB> getLoadedEnemyYAABBs()  { return loadedEnemyYAABBs; }
    public ArrayList<AABB> getLoadedEnemyStationaryAABBs() { return loadedEnemyStationaryAABBs; }
    public ArrayList<AABB> getLoadedPowerUpAABBs() { return loadedPowerUpAABBs; }
    public AABB getLoadedPlayerAABB()  { return loadedPlayerAABB; }
}
