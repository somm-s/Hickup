package ch.cydcampus.hickup.pipeline.filter;

import ch.cydcampus.hickup.pipeline.abstraction.Abstraction;

/**
 * Filter abstractions based on some criteria.
 */
public interface FilterRule {
    
    /**
     * Filter the abstraction.
     * @param abstraction to be filtered.
     * @return true if the abstraction should be discarded, false if it should be kept.
     */
    boolean filter(Abstraction abstraction);

}
