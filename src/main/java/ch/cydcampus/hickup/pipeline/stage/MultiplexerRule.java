package ch.cydcampus.hickup.pipeline.stage;

import ch.cydcampus.hickup.pipeline.abstraction.Abstraction;

/**
 * Multiplexer rule that defines how to calculate the identifier of an abstraction.
 */
public class MultiplexerRule {
    
    private int featureIdx;
    private int packetLevelFeatureIdx;

    /**
     * Creates a new multiplexer rule.
     * @param featureIdx index of the feature that is used to calculate the identifier
     * @param packetLevelFeatureIdx index of the feature that is used to calculate the identifier on packet level
     */
    public MultiplexerRule(int featureIdx, int packetLevelFeatureIdx) {
        this.featureIdx = featureIdx;
        this.packetLevelFeatureIdx = packetLevelFeatureIdx;
    }

    /**
     * Calculates the identifier of an abstraction.
     * @param abstraction the abstraction
     * @return the identifier
     */
    public String getIdentifier(Abstraction abstraction) {
        if(abstraction.getLevel() == 0) {
            return abstraction.getFeatures()[packetLevelFeatureIdx].toString();
        }

        return abstraction.getFeatures()[featureIdx].toString();
    }

}
