package ch.cydcampus.hickup.pipeline.feature.copyrules;

import ch.cydcampus.hickup.pipeline.abstraction.Abstraction;

/*
 * Copy features from a child abstraction to the parent abstraction. Called after creation of a new parent abstraction.
 */
public class FeatureCopyRule {
    
    int inputIndex;
    int outputIndex;

    public FeatureCopyRule setInputIndex(int index) {
        this.inputIndex = index;
        return this;
    }

    public FeatureCopyRule setOutputIndex(int index) {
        this.outputIndex = index;
        return this;
    }

    public void copy(Abstraction childAbstraction, Abstraction parentAbstraction) {
        parentAbstraction.getFeatures()[outputIndex] = childAbstraction.getFeatures()[inputIndex];
    }

}
