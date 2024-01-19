package ch.cydcampus.hickup.pipeline.abstraction;

import java.util.Queue;

public class HighOrderAbstractionQueue implements AbstractionDeque {

    private long timeout; // timeout in microseconds
    private Queue<Abstraction> queue;

    public HighOrderAbstractionQueue(long timeout) {
        this.timeout = timeout;
        this.queue = new java.util.LinkedList<Abstraction>();
    }
    
    /*
     * Adds an abstraction to the deque. If the abstraction is already in the deque, it is moved to the end.
     * It is assumed that the abstraction is active, i.e., the timeout is reset.
     */
    public void addAbstraction(Abstraction abstraction) {
        this.queue.add(abstraction);
    }

    /*
     * Returns the first abstraction from the deque if it is sealed, i.e., the timeout has expired.
     * Removes the abstraction from the deque. Returns null if the first abstraction is not sealed.
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

    public int getSize() {
        return queue.size();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }

}
