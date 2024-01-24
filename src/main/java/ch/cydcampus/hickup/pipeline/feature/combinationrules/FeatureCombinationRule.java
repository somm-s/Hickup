package ch.cydcampus.hickup.pipeline.feature.combinationrules;

import ch.cydcampus.hickup.pipeline.abstraction.Abstraction;

/**
 * Combine multiple features into one new features on the same level.
 */
public interface FeatureCombinationRule {

    /**
     * Set the indices of the features in the child abstraction that should be combined.
     * @param indices
     * @return this object for chaining.
     */
    public FeatureCombinationRule setIndices(int[] indices);

    /**
     * Set the index of the feature in the parent abstraction that should be set.
     * @param index
     * @return this object for chaining.
     */
    public FeatureCombinationRule setOutputIndex(int index);

    /**
     * Combine the features from the child abstraction into the parent abstraction.
     * @param abstraction
     */
    public void combine(Abstraction abstraction);
    
}
