package ch.cydcampus.hickup.pipeline.feature.combinationrules;

import ch.cydcampus.hickup.pipeline.PipelineConfig;
import ch.cydcampus.hickup.pipeline.abstraction.Abstraction;
import ch.cydcampus.hickup.pipeline.feature.Feature;

public class PacketTokenIDCombinationRule implements FeatureCombinationRule {

    int outputIndex;
    int numBuckets;
    int reservedTokens;
    boolean isLogScale;
    int minValue;
    int maxValue;

    public PacketTokenIDCombinationRule(int numBuckets, int reservedTokens, boolean isLogScale, int minValue, int maxValue) {
        this.numBuckets = numBuckets;
        this.reservedTokens = reservedTokens;
        this.isLogScale = isLogScale;
        this.minValue = minValue;
        this.maxValue = maxValue;
    }

    @Override
    public FeatureCombinationRule setIndices(int[] indices) {
        return this;
    }

    @Override
    public FeatureCombinationRule setOutputIndex(int index) {
        this.outputIndex = index;
        return this;
    }

    @Override
    public void combine(Abstraction abstraction) {
        long bytes = abstraction.getFeature(PipelineConfig.BYTES_INDEX).asLong();
        int tokenID = getTokenID(isLogScale, numBuckets, minValue, maxValue, bytes, reservedTokens);
        Feature tokenIdFeature = abstraction.getFeature(outputIndex);
        tokenIdFeature.set(tokenID);
    }

    private int getTokenID(boolean isLogScale, int numBuckets, int minValue, int maxValue, long bytes, int offset) {

        double bytesDouble = bytes;
        if(isLogScale) {
            bytesDouble = (long) getLogBytes(bytes);
        }

        if(bytesDouble <= minValue) {
            return offset;
        }

        if(bytesDouble >= maxValue) {
            return numBuckets + offset - 1;
        }

        int bucket = (int) (bytesDouble * (((double) (numBuckets - 1)) / (maxValue - minValue)));
        return bucket + offset;
    }

    private double getLogBytes(long bytes) {
        return (Math.log(bytes) / Math.log(2));
    }

    
}
