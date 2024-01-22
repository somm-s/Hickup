package ch.cydcampus.hickup.pipeline.source;

import java.util.concurrent.ConcurrentLinkedQueue;

import ch.cydcampus.hickup.pipeline.abstraction.Abstraction;
import ch.cydcampus.hickup.pipeline.abstraction.AbstractionQueue;
import ch.cydcampus.hickup.pipeline.abstraction.PacketAbstraction;

public abstract class DataSource implements AbstractionQueue {

    private static final int MAX_QUEUE_SIZE = 200000;
    private ConcurrentLinkedQueue<Abstraction> queue = new ConcurrentLinkedQueue<>();

    protected boolean produce(Abstraction abstraction) {
        return queue.offer(abstraction);
    }

    protected boolean queueLimitReached() {
        return queue.size() >= MAX_QUEUE_SIZE;
    }

    public int getQueueSize() {
        return queue.size();
    }

    public abstract void start();

    /*
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
    
}
