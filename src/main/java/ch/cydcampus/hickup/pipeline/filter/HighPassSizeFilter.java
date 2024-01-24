package ch.cydcampus.hickup.pipeline.filter;

import ch.cydcampus.hickup.pipeline.PipelineConfig;
import ch.cydcampus.hickup.pipeline.abstraction.Abstraction;

/**
 * Filter abstractions based on the number of bytes in the abstraction.
 * This is a high pass filter, i.e. abstractions with a size smaller than the given size will be discarded.
 */
public class HighPassSizeFilter implements FilterRule {
    
    long size;

    /**
     * Create a new high pass size filter.
     * @param size the size to filter on.
     */
    public HighPassSizeFilter(long size) {
        this.size = size;
    }

    @Override
    public boolean filter(Abstraction abstraction) {
        return abstraction.getFeature(PipelineConfig.BYTES_INDEX).asLong() <= size;
    }
    
}
