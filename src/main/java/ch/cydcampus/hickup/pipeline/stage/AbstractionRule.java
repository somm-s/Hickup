package ch.cydcampus.hickup.pipeline.stage;

import ch.cydcampus.hickup.pipeline.abstraction.Abstraction;
import ch.cydcampus.hickup.pipeline.feature.Feature;

public class AbstractionRule {
    
    private int[] featureIdxs;
    private long timeout;
    
    public AbstractionRule(int[] featureIdxs, long timeout) {
        this.featureIdxs = featureIdxs;
        this.timeout = timeout;
    }

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

        // TODO: support for more complex rules as e.g. conditioning on activeAbstraction features

        return true;

    }

}
