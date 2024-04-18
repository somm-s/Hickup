package ch.cydcampus.hickup.pipeline.source;

import java.util.concurrent.ConcurrentLinkedQueue;

import ch.cydcampus.hickup.pipeline.abstraction.Abstraction;
import ch.cydcampus.hickup.pipeline.abstraction.AbstractionQueue;
import ch.cydcampus.hickup.pipeline.abstraction.PacketAbstraction;

/**
 * Abstract class for data sources. It implements the AbstractionQueue interface to
 * give a pipeline a uniform data structure for all levels of abstraction.
 */
public abstract class DataSource implements AbstractionQueue {

    private static final int MAX_QUEUE_SIZE = 2000000;
    private ConcurrentLinkedQueue<Abstraction> queue = new ConcurrentLinkedQueue<>();

    /**
     * Inserts an abstraction into the queue.
     * @param abstraction
     * @return true if the abstraction was successfully inserted, false otherwise
     */
    protected boolean produce(Abstraction abstraction) {
        if(queueLimitReached()) {
            System.out.println("Queue limit reached. Dropping packet.");
            return false;
        }
        return queue.offer(abstraction);
    }

    /**
     * Checks if the queue is full. Must be handled by the data source.
     * @return true if the queue is full, false otherwise
     */
    protected boolean queueLimitReached() {
        return queue.size() >= MAX_QUEUE_SIZE;
    }

    /**
     * @return the number of abstractions in the queue
     */
    public int getQueueSize() {
        return queue.size();
    }

    /**
     * Starts the data source.
     */
    public abstract void start();

    /**
     * Inserts a finish abstraction into the queue.
     */
    public void finish() {
        queue.offer(PacketAbstraction.FINISH_PACKET);
    }

    @Override
    public Abstraction getFirstAbstraction(long currentTime) {
        return queue.poll();
    }

    @Override
    public void addAbstraction(Abstraction abstraction) {
        throw new UnsupportedOperationException("Not supported for data source.");
    }

    @Override
    public int getSize() {
        return queue.size();
    }
    
}
