package ch.cydcampus.hickup.pipeline.source;

import java.util.concurrent.ConcurrentLinkedQueue;

import ch.cydcampus.hickup.pipeline.abstraction.Abstraction;
import ch.cydcampus.hickup.pipeline.abstraction.AbstractionDeque;

public abstract class DataSource implements AbstractionDeque {

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

    @Override
    public Abstraction getFirstAbstraction(long currentTime) {
        return queue.poll();
    }

    @Override
    public void addAbstraction(Abstraction abstraction) {
        throw new UnsupportedOperationException("Not supported for data source.");
    }
    
}
