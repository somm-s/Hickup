package ch.cydcampus.hickup.pipeline.feature.combinationrules;

import ch.cydcampus.hickup.pipeline.abstraction.Abstraction;

public class ChildrenCountCombinationRule implements FeatureCombinationRule {

    int outputIndex;

    @Override
    public FeatureCombinationRule setIndices(int[] indices) {
        return this;
    }

    @Override
    public FeatureCombinationRule setOutputIndex(int index) {
        this.outputIndex = index;
        return this;
    }

    @Override
    public void combine(Abstraction abstraction) {
        abstraction.getFeature(outputIndex).set(abstraction.getChildren().size());
    }
    
}
