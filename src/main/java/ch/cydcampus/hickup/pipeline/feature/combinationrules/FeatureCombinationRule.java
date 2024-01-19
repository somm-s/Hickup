package ch.cydcampus.hickup.pipeline.feature.combinationrules;

import ch.cydcampus.hickup.pipeline.abstraction.Abstraction;

/*
 * Combine multiple features into one new features on the same level.
 */
public interface FeatureCombinationRule {

    public FeatureCombinationRule setIndices(int[] indices);
    public FeatureCombinationRule setOutputIndex(int index);
    public void combine(Abstraction abstraction);
    
}
