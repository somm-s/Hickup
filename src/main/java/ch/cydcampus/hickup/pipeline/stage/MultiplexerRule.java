package ch.cydcampus.hickup.pipeline.stage;

import ch.cydcampus.hickup.pipeline.abstraction.Abstraction;

public class MultiplexerRule {
    
    private int level;
    private int featureIdx;

    public MultiplexerRule(int level, int featureIdx) {
        this.level = level;
        this.featureIdx = featureIdx;
    }

    public String getIdentifier(Abstraction abstraction) {
        assert abstraction.getLevel() == level;

        return abstraction.getFeatures()[featureIdx].toString();
    }

}
