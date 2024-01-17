package ch.cydcampus.hickup.pipeline.filter;

import ch.cydcampus.hickup.pipeline.abstraction.Abstraction;

public interface FilterRule {
    
    /*
     * Return true if the abstraction should be kept, false if it should be discarded.
     */
    boolean filter(Abstraction abstraction);

}
