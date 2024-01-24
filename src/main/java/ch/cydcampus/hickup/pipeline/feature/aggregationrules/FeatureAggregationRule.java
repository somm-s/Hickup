package ch.cydcampus.hickup.pipeline.feature.aggregationrules;

import ch.cydcampus.hickup.pipeline.abstraction.Abstraction;

/**
 * Aggregate features from child abstractions into the parent abstraction.
 */
public interface FeatureAggregationRule {
    
    /**
     * Set the index of the feature in the child abstraction that should be aggregated.
     * @param index
     * @return this object for chaining
     */
    public FeatureAggregationRule setInputIndex(int index);

    /**
     * Set the index of the feature in the parent abstraction that should be set.
     * @param index
     * @return this object for chaining
     */
    public FeatureAggregationRule setOutputIndex(int index);

    /**
     * Aggregate the feature from the child abstraction into the parent abstraction.
     * @param activeAbstraction
     * @param newChildAbstraction
     */
    public void aggregate(Abstraction activeAbstraction, Abstraction newChildAbstraction);

}
