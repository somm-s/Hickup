package ch.cydcampus.hickup.pipeline.feature.differentialrules;

import ch.cydcampus.hickup.pipeline.abstraction.Abstraction;

/*
 * Differential features are features that are calculated from the previous abstraction and the new abstraction.
 * New feature is on the same level as the feature that is used to calculate the differential feature.
 */
public interface FeatureDifferentialRule {

    public FeatureDifferentialRule setInputIndex(int index);
    public FeatureDifferentialRule setOutputIndex(int index);
    public void differential(Abstraction prevAbstraction, Abstraction newAbstraction);
    
}
