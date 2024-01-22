package ch.cydcampus.hickup.pipeline.filter;

import ch.cydcampus.hickup.pipeline.PipelineConfig;
import ch.cydcampus.hickup.pipeline.abstraction.Abstraction;

public class SizeFilter implements FilterRule {
    
    long size;

    public SizeFilter(long size) {
        this.size = size;
    }

    @Override
    public boolean filter(Abstraction abstraction) {
        return abstraction.getFeature(PipelineConfig.BYTES_INDEX).asLong() >= size;
    }
    
}
