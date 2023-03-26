import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


public class PrinterQueue implements IMPMCQueue<PrintItem>
{
    private PriorityQueue<PrintItem> priorityQueue;
    private final int MAX_CAPACITY;

    private final static AtomicInteger sequence = new AtomicInteger(0);
    private final static AtomicBoolean closed = new AtomicBoolean(false);
    private ReentrantLock lock = new ReentrantLock();
    private Condition isFull = lock.newCondition();
    private Condition isEmpty = lock.newCondition();
    private static  class ItemComparator implements Comparator<PrintItem> {

        @Override
        public int compare(PrintItem o1, PrintItem o2) {
            if( o1.getPrintType() == PrintItem.PrintType.INSTRUCTOR
                    && o2.getPrintType() == PrintItem.PrintType.STUDENT ) {
                return -1;
            } else if ( o1.getPrintType() == PrintItem.PrintType.STUDENT
                    && o2.getPrintType() == PrintItem.PrintType.INSTRUCTOR ) {
                return 1;
            } else {
                return Integer.compare(o1.getOrder(), o2.getOrder());
            }
        }
    }
    public PrinterQueue(int maxElementCount)
    {
        // TODO: Implement
        this.MAX_CAPACITY = maxElementCount;
        this.priorityQueue = new PriorityQueue<PrintItem>(maxElementCount,new ItemComparator());

    }

    @Override
    public void Add(PrintItem data) throws QueueIsClosedExecption {
        lock.lock();
        if(closed.get()) {
            isEmpty.signalAll();
            lock.unlock();
            throw new QueueIsClosedExecption();
        }
        try {
            while ( priorityQueue.size() == MAX_CAPACITY && !closed.get()) {
                isFull.await();
            }
            if(closed.get()) {
                isEmpty.signalAll();
                throw new QueueIsClosedExecption();
            }
            data.setOrder(sequence.incrementAndGet());
            this.priorityQueue.add(data);
            isEmpty.signalAll();
        }catch (InterruptedException e) {
            System.out.println(e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public PrintItem Consume() throws QueueIsClosedExecption {
        lock.lock();
        if(closed.get() && priorityQueue.size() == 0 ) {
            isFull.signalAll();
            lock.unlock();
            throw new QueueIsClosedExecption();
        }
        try {
            while (priorityQueue.size() == 0 && !closed.get()) {
                isEmpty.await();
            }
            if(closed.get() && priorityQueue.size() == 0 ) {
                isFull.signalAll();
                throw new QueueIsClosedExecption();
            }
            PrintItem item = this.priorityQueue.poll();
            isFull.signalAll();
            return item;
        }catch (InterruptedException e) {
            System.out.println(e);
        } finally {
            lock.unlock();
        }
        return null;
    }

    @Override
    public int RemainingSize() {
        return  MAX_CAPACITY - priorityQueue.size();
    }

    @Override
    public void CloseQueue() {
        lock.lock();
        closed.set(true);
        isEmpty.signalAll();
        isFull.signalAll();
        lock.unlock();
    }
}
