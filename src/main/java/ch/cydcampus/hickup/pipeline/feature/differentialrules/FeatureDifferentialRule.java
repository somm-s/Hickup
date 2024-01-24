package ch.cydcampus.hickup.pipeline.feature.differentialrules;

import ch.cydcampus.hickup.pipeline.abstraction.Abstraction;

/**
 * Differential features are features that are calculated from the previous abstraction and the new abstraction.
 * New feature is on the same level as the feature that is used to calculate the differential feature.
 */
public interface FeatureDifferentialRule {

    /**
     * Set the index of the feature in the previous abstraction that should be used to calculate the differential feature.
     * @param index
     * @return this object for chaining.
     */
    public FeatureDifferentialRule setInputIndex(int index);

    /**
     * Set the index of the feature in the new abstraction that should be set.
     * @param index
     * @return this object for chaining.
     */
    public FeatureDifferentialRule setOutputIndex(int index);

    /**
     * Calculate the differential feature from the previous abstraction and the new abstraction.
     * @param prevAbstraction may be null --> use this to initialize the feature
     * @param newAbstraction abstraction where the feature should be set
     */
    public void differential(Abstraction prevAbstraction, Abstraction newAbstraction);
}
