package ch.cydcampus.hickup.pipeline.stage;

import ch.cydcampus.hickup.pipeline.abstraction.Abstraction;

public class MultiplexerRule {
    
    private int featureIdx;
    private int packetLevelFeatureIdx;

    public MultiplexerRule(int level, int featureIdx, int packetLevelFeatureIdx) {
        this.featureIdx = featureIdx;
        this.packetLevelFeatureIdx = packetLevelFeatureIdx;
    }

    public String getIdentifier(Abstraction abstraction) {
        if(abstraction.getLevel() == 0) {
            return abstraction.getFeatures()[packetLevelFeatureIdx].toString();
        }

        return abstraction.getFeatures()[featureIdx].toString();
    }

}
