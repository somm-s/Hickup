package ch.cydcampus.hickup.pipeline.filter;

import ch.cydcampus.hickup.pipeline.abstraction.Abstraction;

public interface FilterRule {
    
    /*
     * Return true if the abstraction should be discarded, false if it should be kept.
     */
    boolean filter(Abstraction abstraction);

}
