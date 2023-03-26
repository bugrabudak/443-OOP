import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PrinterRoom
{
    private class Printer implements Runnable
    {
        private final int id;
        private IMPMCQueue<PrintItem> roomQueue;

        public Printer(int id, IMPMCQueue<PrintItem> roomQueue)
        {
            this.id = id;
            this.roomQueue = roomQueue;
        }

        @Override
        public void run() {
            while(true) {
                try {
                    PrintItem item = roomQueue.Consume();
                    if ( item != null) {
                        item.print();
                        SyncLogger.Instance().Log(SyncLogger.ThreadType.CONSUMER, id,
                                String.format(SyncLogger.FORMAT_PRINT_DONE, item));
                    }

                } catch (QueueIsClosedExecption e) {
                    SyncLogger.Instance().Log(SyncLogger.ThreadType.CONSUMER, id,
                            String.format(SyncLogger.FORMAT_TERMINATING));
                    break;
                }
            }
        }

        public int getId() {
            return id;
        }
    }

    private IMPMCQueue<PrintItem> roomQueue;
    private final List<Printer> printers;
    private final List<Thread> threads = new ArrayList<>();
    public PrinterRoom(int printerCount, int maxElementCount)
    {
        // Instantiating the shared queue
        roomQueue = new PrinterQueue(maxElementCount);

        // Let's try streams
        // Printer creation automatically launches its thread
        printers = Collections.unmodifiableList(IntStream.range(0, printerCount)
                                                         .mapToObj(i -> new Printer(i, roomQueue))
                                                         .collect(Collectors.toList()));
        // Printers are launched using the same queue
        for (Printer printer : printers) {
            Thread t = new Thread(printer);
            SyncLogger.Instance().Log(SyncLogger.ThreadType.MAIN_THREAD, 0,
                    String.format(SyncLogger.FORMAT_PRINTER_LAUNCH,printer.getId()));
            threads.add(t);
            t.start();
        }
    }

    public boolean SubmitPrint(PrintItem item, int producerId)
    {
        SyncLogger.Instance().Log(SyncLogger.ThreadType.PRODUCER, producerId,
                String.format(SyncLogger.FORMAT_ADD, item));
        try {
            roomQueue.Add(item);
            return true;
        } catch (QueueIsClosedExecption e) {
            SyncLogger.Instance().Log(SyncLogger.ThreadType.PRODUCER, producerId,
                    String.format(SyncLogger.FORMAT_ROOM_CLOSED, item));
            return false;
        }
    }

    public void CloseRoom()
    {
        roomQueue.CloseQueue();
        try {
            for (Thread thread : threads) {
                thread.join();
            }
        } catch (InterruptedException e) {
            System.out.println(e);
        }
    }
}
