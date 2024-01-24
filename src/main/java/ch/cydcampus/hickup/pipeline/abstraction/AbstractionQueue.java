package ch.cydcampus.hickup.pipeline.abstraction;

/**
 * This interface provides the abstraction for the basic data structure where elements processed by the 
 * pipeline are consumed from. There is exactly one abstraction queue per level of abstraction. The abstraction
 * queue only allows the consumption of abstractions that are sealed.
 */
public interface AbstractionQueue {
    
    /**
     * Get the first abstraction in the queue.
     * @param currentTime is the current time in microseconds.
     * @return The first abstraction if it is sealed. Otherwise, return null.
     */
    public Abstraction getFirstAbstraction(long currentTime);

    /**
     * Add an abstraction to the queue.
     * @param abstraction The abstraction to add.
     */
    public void addAbstraction(Abstraction abstraction);

}
