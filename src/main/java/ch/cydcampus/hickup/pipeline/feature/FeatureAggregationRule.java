package ch.cydcampus.hickup.pipeline.feature;

import ch.cydcampus.hickup.pipeline.abstraction.Abstraction;

/*
 * Aggregate features from child abstractions into the parent abstraction.
 */
public interface FeatureAggregationRule {
    
    public FeatureAggregationRule setInputIndex(int index);
    public FeatureAggregationRule setOutputIndex(int index);
    public void aggregate(Abstraction activeAbstraction, Abstraction newChildAbstraction);

}
