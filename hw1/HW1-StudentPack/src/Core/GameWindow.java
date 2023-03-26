package Core;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;

public class GameWindow
{
    private static final long MIN_FRAME_TIME = 8; // milliseconds
    private GameEngine gameEngine;
    private JFrame gameFrame;
    private JPanel gamePanel;
    private Canvas gameCanvas;
    private Dimension screenDim;
    private Color backgroundColor;

    private GameWindow()
    {
        // Get a large screen wrt. user resolution
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        double width = d.getWidth() * 0.7;
        double height = width / 16.0 * 9.0;
        this.screenDim = new Dimension((int) width, (int) height);
        backgroundColor = new Color(76,79,69);

        gameFrame = new JFrame();
        gamePanel = new JPanel();
        gamePanel.setBorder(new EmptyBorder(0, 0, 0, 0));
        gameCanvas = new Canvas();

        gameCanvas.setMaximumSize(screenDim);
        gameCanvas.setMinimumSize(screenDim);
        gameCanvas.setPreferredSize(screenDim);
        gameCanvas.setBackground(backgroundColor);

        gamePanel.add(gameCanvas, BorderLayout.CENTER);
        gameFrame.setContentPane(gamePanel);

        gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        gameFrame.setTitle("HW1");
        gameFrame.setResizable(false);
        gameFrame.setVisible(true);
        gameFrame.pack();
        // We will repaint ourselves so don't paint
        gameCanvas.setIgnoreRepaint(true);
        gameFrame.setIgnoreRepaint(true);
        // Threaded Updates
        Thread updateThread = new Thread(() ->
        {
            // Double Buffered Window
            gameCanvas.createBufferStrategy(2);
            long elapsedTime = 0;
            for(long currentTime = System.currentTimeMillis(); true;
                currentTime += elapsedTime)
            {
                elapsedTime = System.currentTimeMillis() - currentTime;
                float elapsedTimeSeconds = (float)elapsedTime / 1000.0f;

                BufferStrategy bs = gameCanvas.getBufferStrategy();
                // Get the Current Buffer
                Graphics2D graphics = (Graphics2D) bs.getDrawGraphics();
                graphics.clearRect(0, 0, gameFrame.getWidth(), gameFrame.getHeight());
                if(gameEngine != null)
                    gameEngine.update(elapsedTimeSeconds, graphics);
                else
                {
                    GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
                    BufferCapabilities bufferCapabilities = gc.getBufferCapabilities();
                    if(!bufferCapabilities.isPageFlipping() || bufferCapabilities.isFullScreenRequired())
                    {
                        graphics.setColor(Color.black);
                        graphics.fillRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());
                        graphics.setColor(Color.red);
                        graphics.drawString("Hardware Acceleration is not supported...", 100, 100);
                        graphics.setColor(Color.white);
                        graphics.drawString("Page Flipping: " + (bufferCapabilities.isPageFlipping() ? "Available" : "Not Supported"), 100, 130);
                        graphics.drawString("Full Screen Required: " + (bufferCapabilities.isFullScreenRequired() ? "Required" : "Not Required"), 100, 160);
                        graphics.drawString("Multiple Buffer Capable: " + (bufferCapabilities.isMultiBufferAvailable() ? "Yes" : "No"), 100, 190);
                    }
                    else
                    {
                        graphics.setColor(Color.black);
                        graphics.fillRect(0, 0, gameCanvas.getWidth(), gameCanvas.getHeight());
                        graphics.setColor(Color.green);
                        graphics.drawString("Hardware Acceleration is Working...", 100, 100);
                        graphics.setColor(Color.white);
                        graphics.drawString("Device: " + gc.getDevice() + "\n", 100, 130);
                        graphics.drawString("Page Flipping: " + (bufferCapabilities.isPageFlipping() ? "Available" : "Not Supported"), 100, 160);
                        graphics.drawString("Full Screen Required: " + (bufferCapabilities.isFullScreenRequired() ? "Required" : "Not Required"), 100, 190);
                        graphics.drawString("Multiple Buffer Capable: " + (bufferCapabilities.isMultiBufferAvailable() ? "Yes" : "No"), 100, 210);
                    }
                }
                graphics.dispose();
                if (!bs.contentsLost())
                {
                    bs.show();
                }
                // Sleep here so that fast systems does not
                // run fast
                try
                {
                    // Delay and give other threads a chance to run
                    long sleepTime = Math.max(0, MIN_FRAME_TIME - elapsedTime);
                    // Don't force a context swich here
                    if(sleepTime > 0)  Thread.sleep(elapsedTime); // milliseconds
                } catch (InterruptedException ignore) {}
            }
        });
        // Start the thread
        updateThread.start();
    }

    // Singleton Pattern
    private static final GameWindow MAIN_WINDOW = new GameWindow();
    public static GameWindow GetInstance()
    {
        return MAIN_WINDOW;
    }

    // Interface
    public void setGame(GameEngine engine)
    {
        this.gameEngine = engine;
    }

    public void attachKeyListener(KeyListener kl)
    {
        gameCanvas.addKeyListener(kl);
    }
    public Dimension getScreenDimension()
    {
        return screenDim;
    }
}
