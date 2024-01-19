package ch.cydcampus.hickup.pipeline.feature.aggregationrules;

import ch.cydcampus.hickup.pipeline.abstraction.Abstraction;
import ch.cydcampus.hickup.pipeline.feature.Feature;

public class FeatureSumRule implements FeatureAggregationRule {

    int inputIndex;
    int outputIndex;

    @Override
    public FeatureSumRule setInputIndex(int index) {
        this.inputIndex = index;
        return this;
    }

    @Override
    public FeatureSumRule setOutputIndex(int index) {
        this.outputIndex = index;
        return this;
    }

    @Override
    public void aggregate(Abstraction activeAbstraction, Abstraction newChildAbstraction) {

        Feature childFeature = newChildAbstraction.getFeatures()[inputIndex];
        Feature activeFeature = activeAbstraction.getFeatures()[outputIndex];
        if(activeAbstraction.getChildren().size() == 0) {
            childFeature.cloneTo(activeFeature);
            return;
        }

        switch(childFeature.getType()) {
            case INT:
                activeFeature.set(activeFeature.asInt() + childFeature.asInt());
                break;
            case DOUBLE:
                activeFeature.set(activeFeature.asDouble() + childFeature.asDouble());
                break;
            case LONG:
                activeFeature.set(activeFeature.asLong() + childFeature.asLong());
                break;
            default:
                throw new UnsupportedOperationException("Unsupported feature type for sum aggregation: " + childFeature.getType());
        }

    }
    
}
