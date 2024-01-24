package ch.cydcampus.hickup.pipeline.feature.copyrules;

import ch.cydcampus.hickup.pipeline.abstraction.Abstraction;

/**
 * Copy features from a child abstraction to the parent abstraction. Called after creation of a new parent abstraction.
 */
public class FeatureCopyRule {
    
    int inputIndex;
    int outputIndex;

    /**
     * Set the index of the feature in the child abstraction that should be copied.
     * @param index
     * @return this object for chaining.
     */
    public FeatureCopyRule setInputIndex(int index) {
        this.inputIndex = index;
        return this;
    }

    /**
     * Set the index of the feature in the parent abstraction that should be set.
     * @param index
     * @return this object for chaining.
     */
    public FeatureCopyRule setOutputIndex(int index) {
        this.outputIndex = index;
        return this;
    }

    /**
     * Copy the feature from the child abstraction into the parent abstraction.
     * @param childAbstraction
     * @param parentAbstraction
     */
    public void copy(Abstraction childAbstraction, Abstraction parentAbstraction) {
        parentAbstraction.getFeatures()[outputIndex] = childAbstraction.getFeatures()[inputIndex];
    }

}
