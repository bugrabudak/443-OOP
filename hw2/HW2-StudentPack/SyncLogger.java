import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

public class SyncLogger
{
    private static final String LOG_FILE_PATH = "print.log";
    private static Instant startInstant = Instant.now();
    /**
     * Thread safe logging functionality,
     * I think OS's guarantee these are atomic
     * <a href="https://stackoverflow.com/questions/1154446/is-file-append-atomic-in-unix">StackOverflow Link</a>
     * to a same file however let's just be sure here. This is synchronized
     */
    private synchronized void LogFile(String finalStr)
    {
        try
        {
            FileWriter f = new FileWriter(LOG_FILE_PATH, true);
            f.write(finalStr);
            // Let the OS handle the rest by closing the fie
            f.close();

            // Might as well write to console for debugging etc.
            System.out.print(finalStr);
            // Force flush maybe? (we did not use "ln" version of the function,
            // so it may not get flushed)
            // If flush is only a hint it still might not get flushed, so
            // be careful order might not be exact (TimeStamps are exact though)
            System.out.flush();
        }
        catch (IOException e)
        {
            System.out.println("Fatal Error, while writing to log! (" + e.getMessage() + ")");
            System.exit(1);
        }
    }

    /**
     * Thread Type
     */
    public enum ThreadType
    {
        MAIN_THREAD("MT         "),
        PRODUCER("PRODUCER   "),
        CONSUMER("[C]PRINTER ");

        private final String text;

        ThreadType(final String text)
        {
            this.text = text;
        }

        @Override
        public String toString()
        {
            return text;
        }
    }

    // Format String to prevent print mistakes
    public static final String FORMAT_PRINTER_LAUNCH    = "Printer %d is launched.";
    public static final String FORMAT_PRODUCER_LAUNCH   = "Creating Producer %d";
    public static final String FORMAT_PRINT_DONE        = "Printing        %s is done!";
    public static final String FORMAT_ADD               = "Trying to Add   %s";
    public static final String FORMAT_ROOM_CLOSED       = "Room is closed, %s is skipped!";
    public static final String FORMAT_TERMINATING       = "Terminating...";

    public void Log(ThreadType t, int uniqueId, String s)
    {
        // Pre-generate the timestamp before waiting for most
        // accurate time
        Instant timeInstant = Instant.now();

        Duration duration = Duration.between(startInstant, timeInstant);
        long TEN_TO_THE_POWER_9 = 1000000000;
        long totalNano = (long)duration.getNano() + duration.getSeconds() * TEN_TO_THE_POWER_9;
        String finalStr = String.format("%s [%02d]; %019d; %s\n",
                                        t, uniqueId,
                                        totalNano,
                                        s);

        // Now we can enter to the atomic block
        LogFile(finalStr);
    }

    // This is bad design but simple solution for deleting log every run
    // (Useful for students when they do the HW also good for black box testing)
    static
    {
        new File(LOG_FILE_PATH).delete();
    }
    // Singleton Pattern
    private static final SyncLogger INSTANCE = new SyncLogger();
    public static SyncLogger Instance()
    {
        return INSTANCE;
    }
}
