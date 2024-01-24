package ch.cydcampus.hickup.pipeline.stage;

import ch.cydcampus.hickup.pipeline.abstraction.Abstraction;
import ch.cydcampus.hickup.pipeline.feature.Feature;

/**
 * A rule that defines when a new abstraction belongs to an active abstraction.
 * The active abstraction is one level above the new abstraction and the previous abstraction
 * 
 * The rule is defined by a set of feature indices and a timeout.
 * 
 * The rule is satisfied if the new abstraction has the same features as the
 * previous abstraction that was added and the time difference between the first update of the
 * new abstraction and the last update of the previous abstraction is less than the timeout.
 */
public class AbstractionRule {
    
    private int[] featureIdxs;
    private long timeout;
    
    /**
     * Creates a new abstraction rule.
     * @param featureIdxs indices of features that must be equal
     * @param timeout max time difference before rule is not satisfied anymore
     */
    public AbstractionRule(int[] featureIdxs, long timeout) {
        this.featureIdxs = featureIdxs;
        this.timeout = timeout;
    }

    /**
     * Checks if the new abstraction belongs to the active abstraction.
     * @param newAbstraction the new abstraction
     * @param activeAbstraction the active abstraction
     * @param prevChildAbstraction the previous abstraction that was added
     * @return true if the new abstraction belongs to the active abstraction
     */
    public boolean belongsToActiveAbstraction(Abstraction newAbstraction, Abstraction activeAbstraction, Abstraction prevChildAbstraction) {
        if(activeAbstraction == null) {
            return false;
        }
        long timeDiff = newAbstraction.getFirstUpdateTime() - prevChildAbstraction.getLastUpdateTime();
        if(timeDiff > timeout) {
            return false;
        }
        Feature[] newFeatures = newAbstraction.getFeatures();
        Feature[] prevFeatures = prevChildAbstraction.getFeatures();
        for(int featureIdx : featureIdxs) {
            if(!newFeatures[featureIdx].equals(prevFeatures[featureIdx])) {
                return false;
            }
        }
        return true;

    }

}
