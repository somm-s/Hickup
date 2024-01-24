package ch.cydcampus.hickup.pipeline.abstraction;

import java.util.Queue;

/**
 * This class provides an implementation of the AbstractionQueue interface for high order abstractions.
 * Abstractions are ordered by their first update time and can only be consumed if they are sealed.
 */
public class HighOrderAbstractionQueue implements AbstractionQueue {

    private long timeout;
    private Queue<Abstraction> queue;

    /**
     * Create a new high order abstraction queue. To ensure an abstraction is sealed when consumed,
     * it is necessary to wait the maximum timeout of all lower levels of abstractions.
     * @param level The level of the abstraction queue.
     * @param timeouts The timeouts for each level of abstraction.
     */
    public HighOrderAbstractionQueue(int level, long[] timeouts) {
        long maxTimeout = 0;
        for(int i = 0; i < level + 1; i++) {
            if(timeouts[i] > maxTimeout) {
                maxTimeout = timeouts[i];
            }
        }
        this.timeout = maxTimeout;
        this.queue = new java.util.LinkedList<Abstraction>();
    }
    
    /**
     * Adds an abstraction to the deque. If the abstraction is already in the deque, it is moved to the end.
     * It is assumed that the abstraction is active, i.e., the timeout is reset.
     * @param abstraction The abstraction to add.
     */
    public void addAbstraction(Abstraction abstraction) {
        this.queue.add(abstraction);
    }

    /**
     * Returns the first abstraction from the deque if it is sealed, i.e., the timeout has expired.
     * Removes the abstraction from the deque. Returns null if the first abstraction is not sealed.
     * @param currentTime is the current time in microseconds.
     */
    public Abstraction getFirstAbstraction(long currentTime) {
        if(isEmpty()) {
            return null;
        }
        if(queue.peek().getLastUpdateTime() + timeout < currentTime) {
            return queue.poll();
        }
        return null;
    }

    /**
     * @return The size of the deque.
     */
    public int getSize() {
        return queue.size();
    }

    /**
     * @return True if the deque is empty.
     */
    public boolean isEmpty() {
        return queue.isEmpty();
    }

}
